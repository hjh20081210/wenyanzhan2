#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""gaokao-poetry(高考必备60篇) + qingjianmoyuan(必背篇目10000) 导入 -> article
qingjian: title/dynasty/writer/content (含注释译文于content中)
gaokao: title/author/content (分句,标记必背)
用法: python3 import_backbi.py
"""
import json, os, re

def esc(s):
    return (s or '').replace("\\", "\\\\").replace("'", "''")

# 朝代规范化: 两汉->汉, 魏晋->魏晋南北朝, 金朝->金
def norm_dynasty(d):
    d = (d or '').strip().replace('朝代','')
    m = {'两汉':'汉','金朝':'金','南北魏':'魏晋南北朝','魏晋':'魏晋南北朝','隋代':'隋','近代':'清以后','当代':'清以后','现代':'清以后','未知':''}
    return m.get(d, d)

def main():
    out_dir = '/Coze/Drive/文言斩/wenyanzhan/data_import'
    base = '/Coze/Drive/文言斩/wenyanzhan/data_repos'
    rows = []
    aid = int(os.environ.get('START_ID', '8150000000000000000'))
    n = 0

    # 1) qingjianmoyuan 10000篇
    qj = json.load(open(os.path.join(base, 'qingjianmoyuan/data-index.json'), encoding='utf-8'))
    for it in qj:
        a = aid + n
        title = it.get('title','').strip()
        writer = it.get('writer','').strip()
        content = it.get('content','').strip()
        dynasty = norm_dynasty(it.get('dynasty',''))
        genre = '必背诗词'
        rows.append(f"({a},'{esc(title)}','{esc(writer)}','{esc(dynasty)}','{esc(content)}','{esc(genre)}','必背篇目','','qingjianmoyuan')")
        n += 1

    # 2) gaokao-poetry 60篇 (必背, 分句content为整段)
    gp = json.load(open(os.path.join(base, 'gaokao-poetry/poetry.json'), encoding='utf-8'))
    for it in gp:
        a = aid + n
        title = it.get('title','').strip()
        author = it.get('author','').strip()
        content = it.get('content','').strip() if isinstance(it.get('content'), str) else ' '.join(it.get('content',[]))
        rows.append(f"({a},'{esc(title)}','{esc(author)}','','{esc(content)}','高考必背','高考必背60篇','','gaokao-poetry')")
        n += 1

    with open(os.path.join(out_dir, 'backbi.sql'), 'w', encoding='utf-8') as f:
        f.write("-- qingjianmoyuan(必背篇目10000) + gaokao-poetry(高考必备60)\n")
        f.write("SET NAMES utf8mb4;\n")
        f.write("INSERT INTO article (article_id,title,author,dynasty,content,genre,book,pinyin,source) VALUES\n")
        f.write(",\n".join(rows) + ";\n")
    print(f"篇目: {n} (qingjian {len(qj)} + gaokao {len(gp)}) -> backbi.sql ({os.path.getsize(os.path.join(out_dir,'backbi.sql'))//1024//1024}MB)")

if __name__ == '__main__':
    main()