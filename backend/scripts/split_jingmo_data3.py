#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
jingmo-data3 海量文言文章分批分卷生成器
将 classical_literature_writings_v2_*.json 按文件切片，每卷生成一个 SQL(<100MB, 可入 GitHub)
用法:
  python3 split_jingmo_data3.py --dir <data3/api> --outdir <输出目录>
产物: <outdir>/part_000.sql, part_001.sql ... (每卷约90MB)
"""
import argparse, glob, json, os, sys
sys.path.insert(0, os.path.dirname(__file__))
from import_jingmo_data import parse_writing
from common import write_batch

def iter_data(path):
    raw = json.load(open(path, encoding='utf-8'))
    items = raw.get('data', raw) if isinstance(raw, dict) else raw
    return items

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('--dir', required=True, help='jingmo-data3/api 目录')
    ap.add_argument('--outdir', required=True)
    a = ap.parse_args()
    files = sorted(glob.glob(os.path.join(a.dir, 'classical_literature_writings_v2_*.json')))
    os.makedirs(a.outdir, exist_ok=True)
    print('总文件数:', len(files))
    shard_idx = 0
    out_f = None
    rows = []
    def flush(pages_f, force=False):
        nonlocal out_f, rows
        if rows and (len(rows) >= 3000 or force):
            write_batch(pages_f, 'article',
                        ['article_id','title','author','dynasty','content','translate','appreciate','notes','genre','source'],
                        rows)
            rows = []
    for i, fp in enumerate(files):
        if out_f is None:
            out_f = open(os.path.join(a.outdir, 'part_%03d.sql' % shard_idx), 'w', encoding='utf-8')
        for o in iter_data(fp):
            rec = parse_writing(o)
            if not rec:
                continue
            rows.append(('rd3_%d_%d' % (shard_idx, len(rows)), rec['title'], rec['author'], rec['dynasty'],
                         rec['content'], rec['translate'], rec['appreciate'], rec['notes'], rec['genre'], rec['source']))
            if len(rows) >= 3000:
                flush(out_f)
        # 每 ~28 文件一卷 (约 90-100MB)
        if (i + 1) % 28 == 0:
            flush(out_f, True)
            out_f.close()
            out_f = None
            shard_idx += 1
            print('卷 %d 完成 (%d 文件)' % (shard_idx, i + 1))
    if out_f:
        flush(out_f, True)
        out_f.close()
        shard_idx += 1
    print('共生成 %d 卷' % shard_idx)

if __name__ == '__main__':
    main()