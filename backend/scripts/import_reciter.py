# -*- coding: utf-8 -*-
"""导入 Binkic/Reciter 默写题库 -> write_exercise 表。

数据源：https://github.com/Binkic/Reciter （MIT），JSON 结构：
  normal/poems.json            : {"poems":[{"title","content":[[词组...],...]}]} 每组最后一项为挖空答案
  comprehensions/comprehensions.json : {"comprehensions":[{"title","content":"...$0，$1...","answer":[...]}]}
  universal/universal.json     : {"repositories":[{"title","normal":[[...]],...}]}

本脚本将三类型统一导入 write_exercise：
  - normal/universal：content 为逐组原文(组内元素空格连接), 最后一元素为挖空答案
  - comprehensions：content 保留占位 $0.., answer 数组 存 answer 字段

用法：
  python import_reciter.py --dir /path/Reciter --out reciter.sql [--db]
"""
import argparse
import json
import os
import re
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from common import Snowflake, get_conn, write_batch


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--dir", required=True, help="Reciter 仓库根目录")
    ap.add_argument("--out", default="reciter.sql")
    ap.add_argument("--db", action="store_true")
    ap.add_argument("--article_id", type=int, default=0, help="关联篇目ID(可指定,默认0)")
    ap.add_argument("--grade", type=int, default=0, help="学段")
    args = ap.parse_args()

    sf = Snowflake()
    conn = get_conn() if args.db else None
    f = open(args.out, "w", encoding="utf-8") if not args.db else None
    if f:
        f.write("-- Reciter 默写题导入\nSET NAMES utf8mb4;\n")

    cols = ["exercise_id", "article_id", "content", "blank_pos", "answer", "type", "hint", "grade"]
    rows = []
    count = 0

    def flush():
        nonlocal rows
        if args.db:
            ph = ",".join(["%s"] * len(cols))
            sql = "INSERT INTO `write_exercise` (%s) VALUES (%s)" % (",".join("`%s`" % c for c in cols), ph)
            with conn.cursor() as cur:
                for r in rows:
                    try: cur.execute(sql, list(r))
                    except Exception: pass
        else:
            write_batch(f, "write_exercise", cols, rows)
        rows = []

    def add(content, blank_pos, answer, t, hint):
        nonlocal count
        if not content: return
        rows.append((sf.next_id(), args.article_id, content,
                     json.dumps(blank_pos, ensure_ascii=False) if blank_pos else None,
                     json.dumps(answer, ensure_ascii=False) if answer else None,
                     t, hint, args.grade))
        count += 1
        if len(rows) >= 300: flush()

    # ---- normal ----
    nf = os.path.join(args.dir, "normal", "poems.json")
    if os.path.exists(nf):
        data = json.load(open(nf, encoding="utf-8"))
        for poem in data.get("poems", []):
            title = poem.get("title", "")
            for group in poem.get("content", []):
                if not group: continue
                # 组内元素空格连接成原句，最后一项为答案
                blank = group[-1]
                filled = " ".join(str(x) for x in group)
                add(filled, None, [blank], 1, "")
        print("normal: %d 条" % count)

    # ---- comprehensions ----
    cf = os.path.join(args.dir, "comprehensions", "comprehensions.json")
    if os.path.exists(cf):
        data = json.load(open(cf, encoding="utf-8"))
        for it in data.get("comprehensions", []):
            content = it.get("content", "")
            answer = it.get("answer", [])
            title = it.get("title", "")
            # 保留 $0.. 占位 → 存 hint 作为题干(替换占位为____也可)
            add(content, None, answer, 2, title)
        print("comprehensions: %d 条" % count)

    # ---- universal ----
    uf = os.path.join(args.dir, "universal", "universal.json")
    if os.path.exists(uf):
        data = json.load(open(uf, encoding="utf-8"))
        for repo in data.get("repositories", []):
            title = repo.get("title", "")
            for group in repo.get("normal", []):
                if not group: continue
                blank = group[-1]
                filled = " ".join(str(x) for x in group)
                add(filled, None, [blank], 1, title)
        print("universal: %d 条" % count)

    flush()
    if f: f.close()
    if conn: conn.close()
    print("Reciter 导入完成, 共 %d 题 -> %s" % (count, args.out if f else "DB"))


if __name__ == "__main__":
    main()