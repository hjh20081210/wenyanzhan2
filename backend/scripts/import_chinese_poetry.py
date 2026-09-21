#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""chinese-poetry 导入器: 全唐诗/宋词/元曲/五代/四书五经/幽梦影 -> article表SQL
字段: title/author/content -> book(体裁集), genre, dynasty
用法: python3 import_chinese_poetry.py --repo <chinese-poetry路径> --out <out.sql>
"""
import json, os, sys, glob

def clean(s):
    return (s or '').replace("'", "''").strip()

def esc(s):
    return (s or '').replace("\\", "\\\\").replace("'", "''")

def main():
    repo = os.environ.get('REPO', '/Coze/Drive/文言斩/wenyanzhan/data_repos/chinese-poetry')
    out = os.environ.get('OUT', '/Coze/Drive/文言斩/wenyanzhan/data_import/chinese_poetry.sql')
    limit = int(os.environ.get('LIMIT', '0'))
    sid = int(os.environ.get('START_ID', '800000000000000000'))

    rows = []
    # 1) 全唐诗 poet.tang.*.json
    for f in sorted(glob.glob(os.path.join(repo, '全唐诗', 'poet.tang.*.json'))):
        data = json.load(open(f, encoding='utf-8'))
        for it in data:
            rows.append((it.get('title',''), it.get('author',''), '唐', '\n'.join(it.get('paragraphs',[])), '唐诗'))
    # 2) 全唐诗 poet.song.*.json 为宋诗
    for f in sorted(glob.glob(os.path.join(repo, '全唐诗', 'poet.song.*.json'))):
        data = json.load(open(f, encoding='utf-8'))
        for it in data:
            rows.append((it.get('title',''), it.get('author',''), '宋', '\n'.join(it.get('paragraphs',[])), '宋诗'))
    # 3) 宋词 ci.song.*.json
    for f in sorted(glob.glob(os.path.join(repo, '宋词', 'ci.song.*.json'))):
        data = json.load(open(f, encoding='utf-8'))
        for it in data:
            rows.append((it.get('rhythmic',''), it.get('author',''), '宋', '\n'.join(it.get('paragraphs',[])), '宋词'))
    # 4) 元曲
    yf = os.path.join(repo, '元曲', 'yuanqu.json')
    if os.path.exists(yf):
        data = json.load(open(yf, encoding='utf-8'))
        for it in data:
            rows.append((it.get('title',''), it.get('author',''), '元', '\n'.join(it.get('paragraphs',[])), '元曲'))
    # 5) 五代 花间集
    for f in sorted(glob.glob(os.path.join(repo, '五代诗词', '**', '*.json'), recursive=True)):
        data = json.load(open(f, encoding='utf-8'))
        for it in data:
            if not isinstance(it, dict): continue
            rows.append((it.get('title',''), it.get('author',''), '五代', '\n'.join(it.get('paragraphs',[])), '五代词'))
    # 6) 四书五经 (chapter结构)
    for f in sorted(glob.glob(os.path.join(repo, '四书五经', '*.json'))):
        base = os.path.splitext(os.path.basename(f))[0]
        data = json.load(open(f, encoding='utf-8'))
        for it in data:
            if not isinstance(it, dict): continue
            title = f"{base}·{it.get('chapter','')}" if it.get('chapter') else base
            rows.append((title, '', '先秦', '\n'.join(it.get('paragraphs',[])), '经典'))
    # 7) 幽梦影
    ym_path = os.path.join(repo, '幽梦影')
    for f in sorted(glob.glob(os.path.join(ym_path, '*.json'))):
        data = json.load(open(f, encoding='utf-8'))
        for i, it in enumerate(data, 1):
            rows.append((f"幽梦影·第{str(i).zfill(3)}则", '张潮', '清', it.get('content',''), '清言'))

    print(f"总篇目: {len(rows)}")
    if limit: rows = rows[:limit]

    with open(out, 'w', encoding='utf-8') as f:
        f.write("-- chinese-poetry 导入: 全唐诗/宋词/元曲/五代/四书五经/幽梦影\n")
        f.write("SET NAMES utf8mb4;\n")
        f.write("INSERT INTO article (article_id,title,author,dynasty,content,genre,book,pinyin,source) VALUES\n")
        buf = []
        for i, (title, author, dyn, content, genre) in enumerate(rows):
            a = sid + i
            book = genre
            # pinyin 简单留title
            pinyin = ''
            buf.append(f"({a},'{esc(title)}','{esc(author)}','{esc(dyn)}','{esc(content)}','{esc(genre)}','{esc(book)}','{esc(pinyin)}','chinese-poetry')")
        f.write(",\n".join(buf) + ";\n")
    print(f"写入: {out} ({os.path.getsize(out)//1024//1024}MB)")

if __name__ == '__main__':
    main()