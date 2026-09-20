#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
教材课文导入器 (按书本整理)
数据源: weimeng23/SchoolChinese  (MIT)
  r/[grade][book].md  — 课内文言文(按教材分册)
    r/71=七上 r/72=七下 r/81=八上 r/82=八下 r/91=九上 r/92=九下
  s/[1-5].md — 文言阅读(课外短文, 不分册, 归通用)
导入表: article (grade/book/pinyin/title/content/source)
标题规范: <h1>标题</h1>, 正文为 markdown, 每条"数字. 内容(...)"合并为多段
用法:
  python3 import_school_books.py --dir <schoolchinese> [--out out.sql] [--db]
"""
import argparse, glob, os, re, sys
sys.path.insert(0, os.path.dirname(__file__))
from common import write_batch

BOOKS = {
    'r/71': '人教版语文七年级上册',
    'r/72': '人教版语文七年级下册',
    'r/81': '人教版语文八年级上册',
    'r/82': '人教版语文八年级下册',
    'r/91': '人教版语文九年级上册',
    'r/92': '人教版语文九年级下册',
}
GRADE = {  # 学段
    'r/71': 1, 'r/72': 1, 'r/81': 1, 'r/82': 1, 'r/91': 1, 'r/92': 1,
    's/1': 1, 's/2': 1, 's/3': 1, 's/4': 1, 's/5': 1,
}
PINYIN_PREFIX_MAP = {  # 篇名拼音→规范篇名(补充常见例外)
    'lunyu': '《论语》十则', 'mulanshi': '木兰诗', 'guancanghai': '观沧海',
    'ailianshuo': '爱莲说', 'loushiming': '陋室铭', 'sanxia': '三峡',
    'maowu': '茅屋为秋风所破歌', 'chushibiao': '出师表', 'yugong': '愚公移山',
    'guanju': '关雎', 'jianjia': '蒹葭', 'mashuo': '马说', 'xiaoshitanji': '小石潭记',
    'chibi': '赤壁', 'chunwang': '春望', 'denglou': '登楼', 'wangyue': '望岳',
    'zouji': '邹忌讽齐王纳谏', 'caoguilunzhan': '曹刿论战', 'tangju': '唐雎不辱使命',
    'chentang_shi': '陈涉世家',
}

def parse_md(text):
    """抽取 <h1>标题</h1> + 作者引用行 + 正文. 返回 (title, author, content)."""
    title, author = '', ''
    m = re.search(r'^#\s*(.+)$', text, re.M)
    if m:
        title = m.group(1).strip().strip('《》').strip()
    content_lines = []
    started = False
    for ln in text.splitlines():
        if ln.strip().startswith('# '):
            started = True
            continue
        if started and ln.strip():
            stripped = ln.strip()
            # 作者引用行: "> 作者" (markdown 引用)
            if stripped.startswith('>'):
                a = stripped.lstrip('>').strip()
                if a and not author:
                    author = a
                continue
            content_lines.append(stripped)
    content = '\n'.join(content_lines).strip()
    return title, author, content

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('--dir', required=True)
    ap.add_argument('--out')
    ap.add_argument('--db', action='store_true')
    a = ap.parse_args()
    out_f = open(a.out, 'w', encoding='utf-8') if a.out else None
    conn = cur = None
    if a.db:
        from common import get_conn
        conn = get_conn(); cur = conn.cursor()
    rows, count = [], 0
    def flush(force=False):
        nonlocal rows
        if rows and (len(rows) >= 400 or force):
            if out_f:
                write_batch(out_f, 'article',
                            ['article_id','title','author','content','genre','grade','book','pinyin','is_required','source'],
                            rows)
            if cur:
                cur.executemany(
                    "INSERT INTO article(article_id,title,author,content,genre,grade,book,pinyin,is_required,source) "
                    "VALUES(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)",
                    rows)
            rows = []
    seen = set()
    for subdir in BOOKS or None:
        pass
    # 课内课文 r/
    for d, book in BOOKS.items():
        g = GRADE[d]
        for fp in sorted(glob.glob(os.path.join(a.dir, d, '*.md'))):
            pin = os.path.splitext(os.path.basename(fp))[0]
            title, author, content = parse_md(open(fp, encoding='utf-8').read())
            if not content:
                continue
            key = (book, title, content[:30])
            if key in seen:
                continue
            seen.add(key)
            pid = 'sc%s' % count
            rows.append((pid, title or pin.title(), author, content, '文言文', g, book, pin, 1, 'SchoolChinese'))
            count += 1
            flush()
    # 课外短文 s/
    for d in sorted(glob.glob(os.path.join(a.dir, 's/*'))):
        if not os.path.isdir(d):
            continue
        book = '课外文言短文'
        for fp in sorted(glob.glob(os.path.join(d, '*.md'))):
            pin = os.path.splitext(os.path.basename(fp))[0]
            title, author, content = parse_md(open(fp, encoding='utf-8').read())
            if not content:
                continue
            rows.append(('sc%s' % count, title or pin.title(), author, content, '文言文', 1, book, pin, 0, 'SchoolChinese'))
            count += 1
            flush()
    flush(True)
    if out_f: out_f.close()
    if conn: conn.close()
    print('教材课文: %d 篇 -> %s' % (count, a.out or '数据库'))

if __name__ == '__main__':
    main()