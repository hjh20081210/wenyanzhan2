#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""NiuTrans/Classical-Modern 古文-现代文对齐语料 导入 -> translate_pair
bitext.txt 每两行: 古文：... / 现代文：...
用法: python3 import_classical_modern.py <repo_path> <out.sql>
"""
import os, sys, glob

def esc(s):
    return (s or '').replace("\\", "\\\\").replace("'", "''")

def main():
    repo = sys.argv[1] if len(sys.argv)>1 else '/Coze/Drive/文言斩/wenyanzhan/data_repos/Classical-Modern'
    out = sys.argv[2] if len(sys.argv)>2 else '/Coze/Drive/文言斩/wenyanzhan/data_import/translate_pair.sql'
    bid = int(os.environ.get('START_ID', '8600000000000000000'))
    rows = []
    n = 0
    for b in sorted(glob.glob(os.path.join(repo, '双语数据', '**', 'bitext.txt'), recursive=True)):
        # 古籍/章节: 路径 双语数据/<古籍>/<章节>/bitext.txt
        parts = b.split('/')
        try:
            book = parts[parts.index('双语数据')+1]
            chapter = parts[parts.index('双语数据')+2]
        except (ValueError, IndexError):
            book = '未知'; chapter = ''
        with open(b, encoding='utf-8') as f:
            lines = [l.rstrip('\n') for l in f]
        i = 0
        while i+1 < len(lines):
            cl = lines[i].strip(); mo = lines[i+1].strip()
            if cl.startswith('古文：'): cl = cl[len('古文：'):]
            if mo.startswith('现代文：'): mo = mo[len('现代文：'):]
            if cl and mo:
                a = bid + n
                rows.append(f"({a},'{esc(cl)}','{esc(mo)}','{esc(book)}','{esc(chapter)}')")
                n += 1
            i += 2
    with open(out, 'w', encoding='utf-8') as f:
        f.write("-- NiuTrans/Classical-Modern 古文-现代文对齐语料\n")
        f.write("SET NAMES utf8mb4;\n")
        f.write("INSERT INTO translate_pair (pair_id,classical,modern,book,chapter) VALUES\n")
        f.write(",\n".join(rows) + ";\n")
    print(f"翻译对齐对: {n} 条 -> {out} ({os.path.getsize(out)//1024//1024}MB)")

if __name__ == '__main__':
    main()