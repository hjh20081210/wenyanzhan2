#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""rainewhk/gaokao-chinese 高考默写真题导入 -> exam_question
questions_by_piece.md: ## 篇目 + - 【年份·卷】题干，答案在 <u>**...**</u>
写入 exam_question: type=UNDERSTAND_DICTATE, exam_level=3(高考)
用法: python3 import_gaokao_chinese.py
"""
import json, os, re

def esc(s):
    return (s or '').replace("\\", "\\\\").replace("'", "''")

def main():
    out_dir = '/Coze/Drive/文言斩/wenyanzhan/data_import'
    src = '/Coze/Drive/文言斩/wenyanzhan/data_repos/gaokao-chinese/docs/questions_by_piece.md'
    qid = int(os.environ.get('START_ID', '8700000000000000000'))
    # 从已生成的 school_books/yuwen 提取篇目ID映射(标题->id) 简化: 这里留空article_id,由部署后脚本回填
    rows = []
    n = 0
    cur_piece = ''
    cur_book = ''
    for line in open(src, encoding='utf-8'):
        line = line.rstrip('\n')
        mh = re.match(r'^## 📖 《(.+?)》\s*\((.+?)\)\s*$', line)
        if mh:
            cur_piece = mh.group(1).strip()
            cur_book = mh.group(2).strip()
            continue
        mq = re.match(r'^- (【[^】]+】)\s*(.+)$', line)
        if mq and cur_piece:
            meta = mq.group(1)   # 【2022·全国甲卷】
            body = mq.group(2)
            # 提取答案(所有<u>**..**</u>和<u>..</u>)
            answers = re.findall(r'<u>\*?\*?(.+?)\*?\*?</u>', body)
            # 题干: 去掉答案标记
            stem = re.sub(r'<u>\*?\*?(.+?)\*?\*?</u>', r'__', body).strip()
            a = qid + n
            answer = '；'.join(aa.strip() for aa in answers if aa.strip())
            title = f"{cur_piece}默写：{stem}"
            tag = json.dumps([cur_book, cur_piece], ensure_ascii=False).replace("'","''")
            rows.append(f"({a},NULL,NULL,3,'UNDERSTAND_DICTATE','{esc(title)}',NULL,'{esc(answer)}','{esc(meta)}',2,'{tag}',1,1,'gaokao-chinese')")
            n += 1
    with open(os.path.join(out_dir, 'gaokao_chinese.sql'), 'w', encoding='utf-8') as f:
        f.write("-- gaokao-chinese 2016-2025高考默写真题\n")
        f.write("SET NAMES utf8mb4;\n")
        f.write("INSERT INTO exam_question (question_id,article_id,word_id,exam_level,type,title,options,answer,analysis,difficulty,tag_list,is_free,is_high_order,source) VALUES\n")
        f.write(",\n".join(rows) + ";\n")
    print(f"高考默写真题: {n} 题 -> gaokao_chinese.sql")

if __name__ == '__main__':
    main()