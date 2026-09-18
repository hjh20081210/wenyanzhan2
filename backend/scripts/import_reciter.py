# -*- coding: utf-8 -*-
"""导入 Binkic/Reciter 默写题库 -> write_exercise 表。

数据源：https://github.com/Binkic/Reciter （MIT）
目录结构（需先 clone）：
  normal/        普通挖空默写
  comprehensions/ 理解性默写
  universal/     通用
每份题目文件通常是 Markdown/文本，从已知篇目名到挖空句提取。

由于 Reciter 原始格式为 Markdown（非固定 JSON），本脚本提供两种模式：
  1. --md：直接导入给定的 Markdown 文本文件（简单逐句拆成一道直接默写题）
  2. --json：导入结构化 JSON（重点篇目，按挖空/理解性默写）

用法（示例）：
  python import_reciter.py --md path/normal_file.md --title '岳阳楼记' --type 1
  python import_reciter.py --json path/reciter.json
"""
import argparse
import json
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from common import Snowflake, get_conn, write_batch, batch_iter


def md_to_direct(content):
    """把 Markdown 原文按空行/段落切成一句道题(直接默写, 挖空由前端生成)。"""
    lines = [l.strip() for l in content.splitlines() if l.strip()]
    return "\n".join(lines)


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--md", help="Markdown 默写题文件")
    ap.add_argument("--title", default="", help="篇目标题")
    ap.add_argument("--type", type=int, default=1, help="1直接默写/2理解性默写")
    ap.add_argument("--hint", default="", help="理解性默写题干提示")
    ap.add_argument("--json", help="结构化JSON(列表,每项含 article_id/content) ")
    ap.add_argument("--article_id", type=int, default=0, help="关联篇目ID(JSON模式用)")
    ap.add_argument("--grade", type=int, default=0, help="学段")
    ap.add_argument("--out", default="write_exercise.sql")
    ap.add_argument("--db", action="store_true")
    args = ap.parse_args()

    sf = Snowflake()
    columns = ["exercise_id", "article_id", "content", "blank_pos", "answer", "type", "hint", "grade"]
    conn = get_conn() if args.db else None
    f = open(args.out, "w", encoding="utf-8") if not args.db else None
    if f:
        f.write("-- Reciter 默写导入\nSET NAMES utf8mb4;\n")

    batch = []
    count = 0

    def flush():
        nonlocal batch, count
        if args.db:
            placeholders = ",".join(["%s"] * len(columns))
            sql = "INSERT INTO `write_exercise` (%s) VALUES (%s)" % (",".join("`%s`" % c for c in columns), placeholders)
            with conn.cursor() as cur:
                for r in batch:
                    cur.execute(sql, list(r))
        else:
            write_batch(f, "write_exercise", columns, batch)
        batch = []

    if args.json and os.path.exists(args.json):
        with open(args.json, "r", encoding="utf-8") as fh:
            items = json.load(fh)
        for it in items:
            content = it.get("content") or it.get("text")
            if not content:
                continue
            batch.append((
                sf.next_id(), it.get("article_id") or args.article_id, content,
                None, None, it.get("type") or args.type,
                it.get("hint") or args.hint, it.get("grade") or args.grade,
            ))
            count += 1
            if len(batch) >= 500:
                flush()
    elif args.md and os.path.exists(args.md):
        with open(args.md, "r", encoding="utf-8") as fh:
            content = md_to_direct(fh.read())
        # 先按文章标题建一个默认篇目(若无 article_id) —— 简单模式一条
        batch.append((
            sf.next_id(), args.article_id, content, None, None,
            args.type, args.hint, args.grade,
        ))
        count += 1
    else:
        print("错误: 需要 --md 或 --json")
        sys.exit(1)

    flush()
    if f:
        f.close()
    if conn:
        conn.close()
    print("导入完成, 共 %d 题 -> %s" % (count, args.out if f else "数据库"))


if __name__ == "__main__":
    main()