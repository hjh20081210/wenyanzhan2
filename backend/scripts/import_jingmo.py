# -*- coding: utf-8 -*-
"""导入 hefengbao/jingmo 古诗文 -> article 表。

数据源：https://github.com/hefengbao/jingmo （GPL-3.0，仅提取数据内容供学习）
典型目录结构（需先 clone）：
  poems/shi/          存世诗词
  poems/shiwen/
  sentences/
  shuicheng/ 等
字段映射：title/author/dynasty/content -> article(title,author,dynasty,content)
可选 translate/appreciate 字段若数据中有则一并导入。

用法：
  python import_jingmo.py --data /path/to/jingmo [--out article.sql] [--genre 诗词]
  --data 指向含 JSON 文件的目录(会递归扫描)。
"""
import argparse
import json
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from common import Snowflake, get_conn, write_batch, batch_iter


def _first(d, keys):
    for k in keys:
        if d.get(k):
            return d[k]
    return None


def parse_article(obj):
    title = _first(obj, ["title", "Title", "name"])
    if not title:
        return None
    author = _first(obj, ["author", "Author"])
    dynasty = _first(obj, ["dynasty", "Dynasty", "chaodai"])
    content = _first(obj, ["content", "Content", "poem", "text", "chapter", "paragraphs"])
    if isinstance(content, (list, tuple)):
        content = "\n".join(content)
    translate = _first(obj, ["translate", "translation", "yiwen"])
    if isinstance(translate, (list, tuple)):
        translate = "\n".join(translate)
    appreciate = _first(obj, ["appreciate", "appreciation", "shangxi", "comment"])
    if isinstance(appreciate, (list, tuple)):
        appreciate = "\n".join(appreciate)
    return dict(title=str(title)[:128], author=author, dynasty=dynasty,
                content=content, translate=translate, appreciate=appreciate)


def walk_json_files(root):
    for dirpath, _, files in os.walk(root):
        for fn in files:
            if fn.endswith(".json") and not fn.startswith("."):
                yield os.path.join(dirpath, fn)


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--data", required=True, help="jingmo 仓库根目录")
    ap.add_argument("--out", default="article.sql", help="输出SQL路径")
    ap.add_argument("--db", action="store_true", help="直接写库")
    ap.add_argument("--genre", default="诗词", help="体裁")
    ap.add_argument("--grade", type=int, default=0, help="学段")
    ap.add_argument("--is_required", type=int, default=0, help="是否必背")
    ap.add_argument("--limit", type=int, default=0, help="限制条数")
    args = ap.parse_args()

    sf = Snowflake()
    columns = ["article_id", "title", "author", "dynasty", "content", "translate",
               "appreciate", "genre", "grade", "is_required", "source"]
    conn = get_conn() if args.db else None
    f = open(args.out, "w", encoding="utf-8") if not args.db else None
    if f:
        f.write("-- jingmo 篇目导入\nSET NAMES utf8mb4;\n")

    batch = []
    count = 0

    def flush():
        if args.db:
            placeholders = ",".join(["%s"] * len(columns))
            sql = "INSERT INTO `article` (%s) VALUES (%s)" % (",".join("`%s`" % c for c in columns), placeholders)
            with conn.cursor() as cur:
                for r in batch:
                    cur.execute(sql, list(r))
            batch.clear()
        else:
            write_batch(f, "article", columns, batch)
            batch.clear()

    for fp in walk_json_files(args.data):
        if args.limit and count >= args.limit:
            break
        try:
            with open(fp, "r", encoding="utf-8") as fh:
                data = json.load(fh)
        except Exception:
            continue
        objs = data if isinstance(data, list) else [data]
        for obj in objs:
            if args.limit and count >= args.limit:
                break
            a = parse_article(obj)
            if not a:
                continue
            batch.append((
                sf.next_id(), a["title"], a["author"], a["dynasty"], a["content"],
                a["translate"], a["appreciate"], args.genre, args.grade,
                args.is_required, "hefengbao/jingmo",
            ))
            count += 1
            if len(batch) >= 500:
                flush()

    flush()
    if f:
        f.close()
    if conn:
        conn.close()
    print("导入完成, 共 %d 篇 -> %s" % (count, args.out if f else "数据库"))


if __name__ == "__main__":
    main()