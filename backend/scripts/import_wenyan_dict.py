#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
文言文汉字字典导入器
数据源: hefengbao/jingmo-data/api/characters_*.json + chinese_character_v3_*.json
导入表: dictionary (type=1 单字) — 文言释义(含《说文》等古籍引文与词语)
explain 结构(JSON): [{"pinyin":读音,"defs":[{"meaning":义项,"cites":[{t,b}],"words":[{w,t}]}]}]
用法:
  python3 import_wenyan_dict.py --dir <api目录> [--out out.sql] [--db]
"""
import argparse, json, glob, os, sys
sys.path.insert(0, os.path.dirname(__file__))
from common import load_json, write_batch

def load_chars(d):
    seen, out = {}, []
    pats = glob.glob(os.path.join(d, 'characters_*.json')) + glob.glob(os.path.join(d, 'chinese_character_v3_*.json'))
    for fn in sorted(pats):
        raw = load_json(fn)
        items = raw.get('data', raw) if isinstance(raw, dict) else raw
        for o in items:
            c = o.get('char')
            if not c or c in seen:
                continue
            seen[c] = o
    return list(seen.values())

def build_explain(o):
    defs = []
    for pr in (o.get('pronunciations') or []):
        py = pr.get('pinyin', '')
        dlist = []
        for e in (pr.get('explanations') or []):
            item = {"meaning": e.get('content', '')}
            cites = [{"t": x.get('text',''), "b": x.get('book','')}
                     for x in (e.get('detail') or []) if x.get('text')]
            if cites: item["cites"] = cites
            words = [{"w": x.get('word',''), "t": x.get('text','')}
                     for x in (e.get('words') or []) if x.get('word')]
            if words: item["words"] = words
            dlist.append(item)
        defs.append({"pinyin": py, "defs": dlist})
    return defs

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('--dir', required=True, help='jingmo-data/api 目录')
    ap.add_argument('--out', help='输出SQL文件(缺省则打印行数)')
    ap.add_argument('--db', action='store_true', help='直连MySQL写入')
    a = ap.parse_args()
    chars = load_chars(a.dir)
    out_f = open(a.out, 'w', encoding='utf-8') if a.out else None
    conn, cur = None, None
    if a.db:
        from common import get_conn
        conn = get_conn(); cur = conn.cursor()
    rows, count = [], 0
    for o in chars:
        c = o.get('char','')
        exp = json.dumps(build_explain(o), ensure_ascii=False)
        pinyins = [x.get('pinyin','') for x in (o.get('pronunciations') or []) if x.get('pinyin')]
        all_py = ' '.join(pinyins)
        radical = o.get('radicals') or ''
        stroke = o.get('strokes') or 0
        wubi = o.get('wubi') or ''
        row = ('%dwx' % (o.get('id') or count), c, 1, all_py, radical, stroke, wubi, exp, 'jingmo-wenyan')
        if out_f:
            write_batch(out_f, 'dictionary',
                        ['dict_id','entry','type','pinyin','radical','stroke','wubi','explain','source'],
                        [row])
        if cur:
            cur.execute("INSERT INTO dictionary(dict_id,entry,type,pinyin,radical,stroke,wubi,explain,source) "
                        "VALUES(%s,%s,%s,%s,%s,%s,%s,%s,%s)",
                        (row[0], row[1], row[2], row[3], row[4], row[5], row[6], row[7], row[8]))
        count += 1
    if out_f: out_f.close()
    if conn: conn.close()
    print('文言汉字: %d 条 -> %s' % (count, a.out or '数据库'))

if __name__ == '__main__':
    main()