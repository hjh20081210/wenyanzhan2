# -*- coding: utf-8 -*-
"""导入 hantang/yuwen 或 weimeng23/SchoolChinese 统编教材课文 -> article 表。

数据源为 Markdown 课文（每篇一个 .md），结构不统一，本脚本做启发式解析：
- 文件名作为标题（去序号/点）
- 第一行非空作作者行（可选）
- 后续为原文，按段落保留

用法：
  python import_yuwen.py --dir /path/to/yuwen/mds [--out yuwen.sql] [--genre 课文] [--grade 2]
"""
import argparse
import os
import re
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from common import Snowflake, get_conn, write_batch, batch_iter


def clean_title(fn, first_nonempty):
    base = os.path.splitext(fn)[0]
    base = re.sub(r"^\d+[\.\s_\-]*", "", base)  # 去序号
    base = base.replace("_", "").replace("-", "").strip()
    if base:
        return base[:128]
    return (first_nonempty or fn)[:128]


def parse_md(path, fn):
    with open(path, "r", encoding="utf-8") as fh:
        content = fh.read()
    lines = [l.rstrip() for l in content.splitlines() if l.strip()]
    if not lines:
        return None
    header = lines[0]
    # 作者行启发式：常见作者关键词
    author = None
    if lines and re.search(r"(作者[:：]|^[〔【\[]|[唐宋元明清|春秋战国])", lines[0]):
        m = re.match(r"[\s]*([（(]?[^）)。]{1,12}[)）告?]?)", lines[0])
        author = lines[0][:64]
        body_start = 1
    else:
        body_start = 0
    title = clean_title(fn, header)
    body = "\n".join(lines[body_start:])
    if not body:
        body = "\n".join(lines)
    return dict(title=title, author=author, content=body)


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--dir", required=True, help="课文 Markdown 目录")
    ap.add_argument("--out", default="yuwen.sql")
    ap.add_argument("--db", action="store_true")
    ap.add_argument("--genre", default="课文")
    ap.add_argument("--grade", type=int, default=2)
    ap.add_argument("--is_required", type=int, default=0)
    ap.add_argument("--limit", type=int, default=0)
    args = ap.parse_args()

    sf = Snowflake()
    columns = ["article_id", "title", "author", "dynasty", "content", "genre",
               "grade", "is_required", "source"]
    conn = get_conn() if args.db else None
    f = open(args.out, "w", encoding="utf-8") if not args.db else None
    if f:
        f.write("-- yuwen 课文导入\nSET NAMES utf8mb4;\n")

    batch = []
    count = 0
    files = sorted(f for f in os.listdir(args.dir) if f.endswith((".md", ".txt", ".markdown")))

    for fn in files:
        if args.limit and count >= args.limit:
            break
        a = parse_md(os.path.join(args.dir, fn), fn)
        if not a:
            continue
        batch.append((
            sf.next_id(), a["title"], a["author"], None, a["content"],
            args.genre, args.grade, args.is_required, "hantang/yuwen",
        ))
        count += 1
        if len(batch) >= 500:
            if args.db:
                placeholders = ",".join(["%s"] * len(columns))
                sql = "INSERT INTO `article` (%s) VALUES (%s)" % (",".join("`%s`" % c for c in columns), placeholders)
                with conn.cursor() as cur:
                    for r in batch:
                        cur.execute(sql, list(r))
            else:
                write_batch(f, "article", columns, batch)
            batch = []

    if batch:
        if args.db:
            placeholders = ",".join(["%s"] * len(columns))
            sql = "INSERT INTO `article` (%s) VALUES (%s)" % (",".join("`%s`" % c for c in columns), placeholders)
            with conn.cursor() as cur:
                for r in batch:
                    cur.execute(sql, list(r))
        else:
            write_batch(f, "article", columns, batch)

    if f:
        f.close()
    if conn:
        conn.close()
    print("导入完成, 共 %d 篇 -> %s" % (count, args.out if f else "数据库"))


if __name__ == "__main__":
    main()