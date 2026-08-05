# Vocabulary field-rename migration

Three scripts, meant to run in this order. All are plain Python 3, no dependencies.

## 1. (optional) sanity-check a Vocabulary.java parses cleanly

```
python3 parse_vocabulary.py path/to/Vocabulary.java
```

Prints how many fields were found/resolved. Add `--dump out.json` to see the
parsed `{name: {value, type}}` map. `unresolved: 0` means every field was
either a plain string literal or `URI.create(<ref-to-a-string-field>)` — the
two shapes JOPA emits. If you ever see unresolved fields, look at them before
trusting the mapping step.

## 2. Build the rename map

```
python3 build_mapping.py path/to/OLD_Vocabulary.java path/to/NEW_Vocabulary.java [out_dir]
```

Writes `mapping.json` (old_name -> new_name, for every field, including the
ones that didn't change) and `report.json` into `out_dir` (default: current
dir). **Read `report.json` before proceeding** — check:

- `unmatched_old` — old fields whose IRI value doesn't exist anywhere in the
  new file at all (the underlying ontology entity was removed/renamed at the
  IRI level, not just the Java field name — these need a manual decision,
  the script won't touch their usages).
- `unmatched_new` — new fields that didn't get matched to anything old (new
  entities, informational).
- `ambiguous` — same `(value, type)` pair shared by more than one field name
  on one side. The script already auto-resolves the common case (identity
  name reused on both sides), so anything still listed here needs your eyes.
  This mainly shows up with the `ONTOLOGY_IRI_*` constants, which sometimes
  duplicate an `s_i_.../u_i_...` entry with the same string value.

Matching is done on `(value, type)`, not value alone, so a `String` field and
a `URI` field can never get cross-matched even when they hold the same text
(this really happens: e.g. `ONTOLOGY_IRI_ONTOPUS` and `s_i_ontopus` are both
`String` fields with the same value, while `u_i_ontopus` is a `URI` field
with that same value again).

## 3. Apply the rename across the codebase

```
python3 apply_replacements.py mapping.json path/to/repo/root --dry-run
```

Review the printed diff summary, then re-run without `--dry-run` to write
the changes. It handles:

- `Vocabulary.OLD_NAME` -> `Vocabulary.NEW_NAME` anywhere (qualified usage,
  including through other import aliases/paths).
- `import static ...Vocabulary.OLD_NAME;` -> rewritten import, plus bare
  `OLD_NAME` usages *within that same file* renamed too.
- `import static ...Vocabulary.*;` (wildcard) -> bare usages of any mapped
  name in that file are renamed. This one can't be verified as tightly as
  the explicit-import case (a wildcard import means any bare identifier
  *could* be a Vocabulary field), so double-check the diff for files using
  wildcard static imports.
- Skips `Vocabulary.java` itself — replace that file wholesale with the
  newly-generated one rather than patching it.
- Only ever touches identifiers that are keys in `mapping.json`, matched with
  `\b` word boundaries, so it can't accidentally rename an unrelated variable
  that happens to share a substring.

It won't touch build directories (`target`, `build`, `node_modules`, `.git`,
`.idea`).

## Verifying nothing's value changed

Since the mapping is built by matching on IRI value (not by guessing the new
naming convention), any successful match is guaranteed to point at a field
holding the exact same value in the new file — so a replaced usage always
resolves to the same string/URI at runtime as before. The things that need
your judgment are only the ambiguous/unmatched cases listed in `report.json`.

## What I already validated

- Parsed the `Vocabulary.java` you uploaded: 1966 fields, 0 unresolved.
- Self-test: mapping the file against itself produces a perfect identity
  mapping (1966/1966 matched, 0 unmatched, 0 ambiguous) — confirms the
  parser/matcher round-trips correctly.
- Replacement script tested against a small synthetic repo covering all
  three usage patterns above (qualified, explicit static import, wildcard
  static import) plus an unrelated variable that must NOT be touched — all
  passed.

Once you have the new, regenerated `Vocabulary.java`, run step 2 with your
old and new files and skim `report.json` first — that's the only step that
needs a human in the loop.
