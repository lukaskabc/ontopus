#!/usr/bin/env python3
"""
Walks a Java source tree and rewrites every usage of a renamed Vocabulary field
so the code compiles against the newly regenerated Vocabulary.java, without
changing the underlying value any usage resolves to.

Handles:
  1. Qualified usage:      Vocabulary.OLD_NAME            -> Vocabulary.NEW_NAME
     (also works through other qualifiers/aliases, e.g. gen.Vocabulary.OLD_NAME,
     because it matches on `.OLD_NAME` immediately preceded by the literal
     token "Vocabulary" and a word boundary)
  2. Explicit static import: import static ...Vocabulary.OLD_NAME;
     -> import static ...Vocabulary.NEW_NAME;
     and, only within that same file, bare usages of OLD_NAME -> NEW_NAME.
  3. Wildcard static import: import static ...Vocabulary.*;
     -> bare usages of any OLD_NAME found in that file are renamed too
     (flagged in the report since it can't be verified as tightly as case 2).

Skips the Vocabulary.java file itself (that one gets replaced wholesale by
the newly generated version, not patched).

Safe by construction: only renames identifiers that are keys in mapping.json,
using \\b word boundaries, so it will never touch an unrelated identifier that
merely shares a substring with a field name.
"""
import sys
import os
import re
import json


def find_java_files(root):
    for dirpath, dirnames, filenames in os.walk(root):
        # skip common build/output dirs
        dirnames[:] = [d for d in dirnames if d not in ('.git', 'target', 'build', 'node_modules', '.idea')]
        for fn in filenames:
            if fn.endswith('.java') and fn != 'Vocabulary.java':
                yield os.path.join(dirpath, fn)


def compile_qualified_pattern(names):
    # \bVocabulary\.(NAME1|NAME2|...)\b   -- longest names first to avoid partial-prefix issues
    alt = '|'.join(re.escape(n) for n in sorted(names, key=len, reverse=True))
    return re.compile(r'\bVocabulary\.(' + alt + r')\b')


def compile_bare_pattern(names):
    alt = '|'.join(re.escape(n) for n in sorted(names, key=len, reverse=True))
    return re.compile(r'\b(' + alt + r')\b')


STATIC_IMPORT_RE = re.compile(r'import\s+static\s+[\w.]*\bVocabulary\.([A-Za-z_][A-Za-z0-9_]*|\*)\s*;')


def process_file(path, mapping, qualified_re, changed_names):
    """Returns (new_text or None if unchanged, list of change-notes)"""
    with open(path, 'r', encoding='utf-8') as f:
        text = f.read()
    original = text
    notes = []

    # 1. qualified usages: Vocabulary.OLD -> Vocabulary.NEW
    def repl_qualified(m):
        old = m.group(1)
        new = mapping[old]
        if old != new:
            notes.append(f"Vocabulary.{old} -> Vocabulary.{new}")
        return f"Vocabulary.{new}"
    text = qualified_re.sub(repl_qualified, text)

    # 2. static imports (single-name or wildcard) + bare usages within this file
    static_imports = STATIC_IMPORT_RE.findall(original)
    if static_imports:
        explicit_names = {n for n in static_imports if n != '*'}
        has_wildcard = '*' in static_imports

        bare_candidates = set(explicit_names) if explicit_names else set()
        if has_wildcard:
            # any mapped name could plausibly appear bare in this file
            bare_candidates |= set(mapping.keys())

        if bare_candidates:
            bare_re = compile_bare_pattern(bare_candidates)

            def repl_bare(m):
                old = m.group(1)
                new = mapping.get(old, old)
                if old != new:
                    notes.append(f"(bare, static-import) {old} -> {new}")
                return new
            text = bare_re.sub(repl_bare, text)

        # rewrite the import statements themselves
        def repl_import(m):
            name = m.group(1)
            if name == '*':
                return m.group(0)
            new = mapping.get(name, name)
            return m.group(0).replace(f'Vocabulary.{name}', f'Vocabulary.{new}')
        text = STATIC_IMPORT_RE.sub(repl_import, text)

    if text != original:
        return text, notes
    return None, notes


def main():
    mapping_path = sys.argv[1]
    root = sys.argv[2]
    dry_run = '--dry-run' in sys.argv

    with open(mapping_path) as f:
        mapping = json.load(f)
    changed_names = {o: n for o, n in mapping.items() if o != n}
    if not changed_names:
        print("Mapping has no renamed fields (old == new for everything). Nothing to do.")
        return

    qualified_re = compile_qualified_pattern(mapping.keys())

    results = []
    total_changes = 0
    for path in find_java_files(root):
        new_text, notes = process_file(path, mapping, qualified_re, changed_names)
        if new_text is not None:
            total_changes += len(notes)
            results.append((path, notes))
            if not dry_run:
                with open(path, 'w', encoding='utf-8') as f:
                    f.write(new_text)

    print(f"Files touched: {len(results)}")
    print(f"Total usage replacements: {total_changes}")
    for path, notes in results:
        print(f"\n{path}  ({len(notes)} change(s))")
        for n in notes[:10]:
            print(f"    {n}")
        if len(notes) > 10:
            print(f"    ... and {len(notes) - 10} more")

    if dry_run:
        print("\n[DRY RUN] no files were modified.")


if __name__ == '__main__':
    main()
