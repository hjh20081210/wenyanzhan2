#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Ancient-Chinese-Allusion 典故导入 -> word_lib
core_structure_09.jsonl: 典源词/典源内容/衍生列表(典形词+释义+同源例句)
写 word_lib: word=典故名, word_type=7典故, explain=典源+释义, example=例句
用法: python3 import_allusion.py
"""
import json, os

def esc(s):
    return (s or '').replace("\\", "\\\\").replace("'", "''")

def strip_u(s):
    # 去掉 <u>标注
    return re.sub(r'</?u>', '', s or '') if s else ''

import re

def main():
    out_dir = '/Coze/Drive/文言斩/wenyanzhan/data_import'
    src = '/Coze/Drive/文言斩/wenyanzhan/data_repos/Ancient-Chinese-Allusion-Resource-Database/Allusion_Knowledge_Base/core_structure_09.jsonl'
    wid = int(os.environ.get('START_ID', '8500000000000000000'))
    rows = []
    examples_used = set()
    with open(src, encoding='utf-8') as f:
        for i, line in enumerate(f):
            o = json.loads(line)
            word = (o.get('典源词') or '').strip()
            if not word: continue
            w = wid + i
            source_content = strip_u(o.get('典源内容',''))
            # 汇总衍生释义
            defs = []
            ex = []
            for dl in o.get('衍生列表', []) or []:
                if not isinstance(dl, dict): continue
                dyx = dl.get('典形词') or ''
                sy = dl.get('释义') or ''
                if sy: defs.append(f"{dyx}:{sy}")
                for tl in dl.get('同源列表', []) or []:
                    if isinstance(tl, dict):
                        for e in (tl.get('例句',[]) or []):
                            ex.append(strip_u(e))
            explain = (source_content + ('\n'.join(defs) if defs else '')).strip()
            example = '；'.join(ex)[:2000]
            rows.append(f"({w},'{esc(word)}',7,'典故','{esc(explain)}','{esc(example)}','[]',2)")
    with open(os.path.join(out_dir, 'allusion.sql'), 'w', encoding='utf-8') as f:
        f.write("-- Ancient-Chinese-Allusion 典故库\n")
        f.write("SET NAMES utf8mb4;\n")
        f.write("INSERT INTO word_lib (word_id,word,word_type,pos,explain,example,tag_list,difficulty) VALUES\n")
        f.write(",\n".join(rows) + ";\n")
    print(f"典故: {len(rows)} 条 -> allusion.sql ({os.path.getsize(os.path.join(out_dir,'allusion.sql'))//1024//1024}MB)")

if __name__ == '__main__':
    main()