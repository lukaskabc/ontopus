#!/usr/bin/env python3
"""
Given the OLD and NEW Vocabulary.java, build a rename map: old_field_name -> new_field_name.

Matching is done on (value, type) so a String field never gets matched to a URI
field (or vice-versa) even if they happen to share the same IRI text (this
genuinely happens: ONTOLOGY_IRI_* constants often duplicate an s_i_/u_i_ entry).

Outputs:
  - mapping.json           : old_name -> new_name for every field we could match unambiguously
  - report.json            : unmatched_old, unmatched_new, ambiguous (value/type shared by >1 name on either side)
"""
import sys
import re
import json
from collections import defaultdict
from parse_vocabulary import parse_vocabulary

# JOPA field naming convention: {datatype}_{identifierType}_{fieldName}
#   datatype:       s = string value, u = URI value  (already disambiguated by
#                    the (value, type) grouping key, so not re-checked here)
#   identifierType: c = class, p = predicate, i = individual
IDENTIFIER_TYPE_RE = re.compile(r'^[su]_([cpi])_')


def identifier_type(name: str):
    """Returns 'c'/'p'/'i' if name follows the {datatype}_{identifierType}_... JOPA
    convention, else None (e.g. ONTOLOGY_IRI_* constants don't follow it)."""
    m = IDENTIFIER_TYPE_RE.match(name)
    return m.group(1) if m else None


def build(old_path: str, new_path: str):
    old_fields, old_unresolved, _ = parse_vocabulary(old_path)
    new_fields, new_unresolved, _ = parse_vocabulary(new_path)

    # group by (value, type) on each side
    old_by_key = defaultdict(list)
    for name, (val, typ) in old_fields.items():
        old_by_key[(val, typ)].append(name)

    new_by_key = defaultdict(list)
    for name, (val, typ) in new_fields.items():
        new_by_key[(val, typ)].append(name)

    mapping = {}
    ambiguous = []       # keys where old side or new side has >1 name
    unmatched_old = []   # old names whose (value,type) doesn't exist in new file at all

    for key, old_names in old_by_key.items():
        new_names = new_by_key.get(key)
        if not new_names:
            unmatched_old.extend(old_names)
            continue
        if len(old_names) == 1 and len(new_names) == 1:
            mapping[old_names[0]] = new_names[0]
            continue
        # Ambiguous on at least one side. Resolve deterministically, in order:
        resolved_pairs = []
        remaining_old = list(old_names)
        remaining_new = list(new_names)

        # Strategy 1: identity — name unchanged is a valid pairing.
        for n in list(remaining_old):
            if n in remaining_new:
                resolved_pairs.append((n, n))
                remaining_old.remove(n)
                remaining_new.remove(n)

        # Strategy 2: identifierType consistency (class<->class, predicate<->predicate,
        # individual<->individual), per the {datatype}_{identifierType}_{fieldName}
        # naming convention. Only pairs within a group when it's 1:1 in that group.
        if remaining_old and remaining_new:
            old_by_itype = defaultdict(list)
            for n in remaining_old:
                old_by_itype[identifier_type(n)].append(n)
            new_by_itype = defaultdict(list)
            for n in remaining_new:
                new_by_itype[identifier_type(n)].append(n)
            for itype, onames in old_by_itype.items():
                nnames = new_by_itype.get(itype)
                if nnames and len(onames) == 1 and len(nnames) == 1:
                    resolved_pairs.append((onames[0], nnames[0]))
                    remaining_old.remove(onames[0])
                    remaining_new.remove(nnames[0])

        # Strategy 3: last-one-standing — only if the sole remaining old/new pair
        # also agree on identifierType (or neither follows the convention at all),
        # so we never silently pair e.g. a leftover predicate with a leftover class.
        if len(remaining_old) == 1 and len(remaining_new) == 1:
            if identifier_type(remaining_old[0]) == identifier_type(remaining_new[0]):
                resolved_pairs.append((remaining_old[0], remaining_new[0]))
                remaining_old, remaining_new = [], []

        for o, n in resolved_pairs:
            mapping[o] = n
        if remaining_old or remaining_new:
            ambiguous.append({
                "value": key[0], "type": key[1],
                "old_names": remaining_old, "new_names": remaining_new,
            })

    new_names_used = set(mapping.values())
    unmatched_new = [n for n in new_fields if n not in new_names_used
                      and n not in {x for a in ambiguous for x in a["new_names"]}]

    report = {
        "old_field_count": len(old_fields),
        "new_field_count": len(new_fields),
        "mapped_count": len(mapping),
        "changed_count": sum(1 for o, n in mapping.items() if o != n),
        "unmatched_old_count": len(unmatched_old),
        "unmatched_new_count": len(unmatched_new),
        "ambiguous_count": len(ambiguous),
        "old_unresolved": old_unresolved,
        "new_unresolved": new_unresolved,
        "unmatched_old": sorted(unmatched_old),
        "unmatched_new": sorted(unmatched_new),
        "ambiguous": ambiguous,
    }
    return mapping, report


if __name__ == '__main__':
    old_path, new_path = sys.argv[1], sys.argv[2]
    out_dir = sys.argv[3] if len(sys.argv) > 3 else '.'
    mapping, report = build(old_path, new_path)

    with open(f'{out_dir}/mapping.json', 'w') as f:
        json.dump(mapping, f, indent=2, sort_keys=True)
    with open(f'{out_dir}/report.json', 'w') as f:
        json.dump(report, f, indent=2, sort_keys=True)

    print(f"old fields:      {report['old_field_count']}")
    print(f"new fields:      {report['new_field_count']}")
    print(f"mapped:          {report['mapped_count']}  (of which renamed: {report['changed_count']})")
    print(f"unmatched old:   {report['unmatched_old_count']}  (old field's value no longer exists in new file)")
    print(f"unmatched new:   {report['unmatched_new_count']}  (new field not matched to any old field)")
    print(f"ambiguous:       {report['ambiguous_count']}  (same value+type used by >1 name on one side)")
    if report['unmatched_old_count']:
        print("  sample unmatched old:", report['unmatched_old'][:10])
    if report['ambiguous_count']:
        print("  sample ambiguous:", report['ambiguous'][:3])
