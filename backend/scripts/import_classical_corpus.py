#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""chinese-classical-corpus 导入器
- shuowen(说文解字) -> dictionary 表 (type=1 单字, 专业文言字典: 部首/反切/释义)
- corpus.jsonl 其余章节 -> article 表 (十三经/二十四史前15部/资治通鉴)
用法: python3 import_classical_corpus.py
"""
import json, os, collections

def esc(s):
    return (s or '').replace("\\", "\\\\").replace("'", "''")

def main():
    out_dir = '/Coze/Drive/文言斩/wenyanzhan/data_import'
    repo = '/Coze/Drive/文言斩/wenyanzhan/data_repos/chinese-classical-corpus/output'
    # 说文 -> dictionary
    shuowen = json.load(open(os.path.join(repo, 'shuowen.json'), encoding='utf-8'))
    dict_rows = []
    did = int(os.environ.get('DICT_START', '8300000000000000000'))
    for i, it in enumerate(shuowen):
        dic_id = did + i
        # radical/反切/释义
        radical = it.get('radical','')
        pinyin = it.get('pinyin','')
        fanqie = it.get('fanqie','')
        content = it.get('content','')
        # explain JSON 含反切+释义
        explain = json.dumps({"fanqie": fanqie, "meaning": content, "source": "说文解字"}, ensure_ascii=False).replace("'", "''")
        ch = it.get("char", "")
        dict_rows.append(f"({dic_id},'{esc(ch)}',1,'{esc(pinyin)}','{esc(radical)}',0,'{explain}','说文解字')")
    with open(os.path.join(out_dir, 'shuowen_dict.sql'), 'w', encoding='utf-8') as f:
        f.write("-- chinese-classical-corpus 说文解字 (专业文言字典)\n")
        f.write("SET NAMES utf8mb4;\n")
        f.write("INSERT INTO dictionary (dict_id,entry,type,pinyin,radical,stroke,explain,source) VALUES\n")
        f.write(",\n".join(dict_rows) + ";\n")
    print(f"说文解字字典: {len(dict_rows)} 字 -> shuowen_dict.sql")

    # corpus.jsonl 其余 -> article
    rows = []
    aid = int(os.environ.get('ART_START', '8100000000000000000'))
    n = 0
    with open(os.path.join(repo, 'corpus.jsonl'), encoding='utf-8') as f:
        for line in f:
            o = json.loads(line)
            if o.get('category') == '字书':  # 说文,已有shuowen_dict处理
                continue
            a = aid + n
            title = o.get('chapter') or o.get('source')
            genre = {'史':'史书','经':'经典'}.get(o.get('category'), o.get('category','经典'))
            content = o.get('content','')
            author = o.get("author","")
            era = o.get("era","")
            source = o.get("source","")
            rows.append(f"({a},'{esc(title)}','{esc(author)}','{esc(era)}','{esc(content)}','{esc(genre)}','{esc(source)}','','{esc(source)}')")
            n += 1
    with open(os.path.join(out_dir, 'classical_corpus.sql'), 'w', encoding='utf-8') as f:
        f.write("-- chinese-classical-corpus 十三经+史书+资治通鉴 章节\n")
        f.write("SET NAMES utf8mb4;\n")
        f.write("INSERT INTO article (article_id,title,author,dynasty,content,genre,book,pinyin,source) VALUES\n")
        f.write(",\n".join(rows) + ";\n")
    print(f"典籍章节: {n} 篇 -> classical_corpus.sql ({os.path.getsize(os.path.join(out_dir,'classical_corpus.sql'))//1024//1024}MB)")

if __name__ == '__main__':
    main()