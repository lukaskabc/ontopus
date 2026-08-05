#!/usr/bin/env python3
"""
Parses a JOPA-generated Vocabulary.java file into a dict: field_name -> resolved_value.

Handles the two field shapes JOPA emits:
    public final static String s_c_Foo = "http://example.org/Foo";
    public final static URI    u_c_Foo = URI.create(s_c_Foo);

URI fields are resolved by looking up the String field they reference, so both
s_ and u_ variants end up mapped to the same underlying IRI value. Any other
"public final static <Type> NAME = <expr>;" field with a plain string literal
(e.g. the ONTOLOGY_IRI_* constants) is captured too.
"""
import re
import sys
import json

FIELD_RE = re.compile(
    r'public\s+final\s+static\s+(?P<type>\w+)\s+(?P<name>[A-Za-z_][A-Za-z0-9_]*)\s*=\s*(?P<init>[^;]+);',
    re.MULTILINE,
)
STRING_LITERAL_RE = re.compile(r'^"(.*)"$', re.DOTALL)
URI_CREATE_RE = re.compile(r'^URI\.create\(\s*([A-Za-z_][A-Za-z0-9_]*)\s*\)$')


def unescape_java_string(s: str) -> str:
    # Minimal Java string unescape, sufficient for IRI content.
    return (
        s.replace('\\\\', '\x00ESC\x00')
         .replace('\\"', '"')
         .replace('\\n', '\n')
         .replace('\\t', '\t')
         .replace('\x00ESC\x00', '\\')
    )


def parse_vocabulary(path: str):
    """Returns (fields: dict[name -> (value, type)], unresolved: list[(name, type, init)], order: list[name])"""
    with open(path, 'r', encoding='utf-8') as f:
        text = f.read()

    raw_fields = []  # (name, type, init) in file order
    for m in FIELD_RE.finditer(text):
        raw_fields.append((m.group('name'), m.group('type'), m.group('init').strip()))

    values = {}   # name -> value (str)
    types = {}    # name -> type (str)
    unresolved = []

    # Pass 1: direct string literals
    for name, ftype, init in raw_fields:
        lit = STRING_LITERAL_RE.match(init)
        if lit:
            values[name] = unescape_java_string(lit.group(1))
            types[name] = ftype

    # Pass 2: URI.create(ref) — resolve against pass-1 results
    for name, ftype, init in raw_fields:
        if name in values:
            continue
        ref = URI_CREATE_RE.match(init)
        if ref and ref.group(1) in values:
            values[name] = values[ref.group(1)]
            types[name] = ftype
        else:
            unresolved.append((name, ftype, init))

    fields = {name: (values[name], types[name]) for name in values}
    return fields, unresolved, [n for n, _, _ in raw_fields]


if __name__ == '__main__':
    path = sys.argv[1]
    fields, unresolved, order = parse_vocabulary(path)
    print(f"Parsed {len(order)} fields, resolved {len(fields)}, unresolved {len(unresolved)}")
    if unresolved:
        print("Unresolved fields (need manual look):")
        for n, t, i in unresolved[:20]:
            print(f"  {t} {n} = {i}")
    if len(sys.argv) > 2 and sys.argv[2] == '--dump':
        out = sys.argv[3] if len(sys.argv) > 3 else path + '.json'
        with open(out, 'w') as f:
            json.dump({n: {"value": v, "type": t} for n, (v, t) in fields.items()}, f, indent=2, sort_keys=True)
        print(f"Dumped fields to {out}")
