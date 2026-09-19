# -*- coding: utf-8 -*-
"""导入 chinese-xinhua word.json -> dictionary 表。

数据源：https://github.com/pwxcoo/chinese-xinhua
- data/word.json：单字，字段 word/pinyin/radicals/strokes/explanation/oldword/cixing
- 可选：data/idiom.json 成语 -> type=2 词组

用法：
  python import_dict.py --word path/to/word.json [--idiom path/to/idiom.json] [--out out.sql]

默认输出 dictionary.sql，也可直接写库（--db 开启）。
"""
import argparse
import json
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from common import Snowflake, get_conn, write_batch, batch_iter, sql_str, load_json


def parse_explanation(explanation):
    """chinese-xinhua 的 explanation 是字符串(含<br>分隔多个义项)。
    转成 JSON 数组：[{"def":"...","example":"..."}]"""
    if not explanation:
        return None
    text = str(explanation)
    # 按 <br> 分段，去掉 html 标签
    parts = [p.strip() for p in text.replace("<br>", "\n").split("\n") if p.strip()]
    parts = [p.replace("<br/>", "") for p in parts]
    sane = []
    for p in parts:
        def_part = p
        example = None
        if "例：" in p or "例句" in p:
            for mark in ["例：", "例句："]:
                if mark in def_part:
                    idx = def_part.index(mark)
                    example = def_part[idx + len(mark):].strip()
                    def_part = def_part[:idx].strip()
                    break
        sane.append({"def": def_part, "example": example})
    return json.dumps(sane, ensure_ascii=False)


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--word", required=True, help="word.json 路径")
    ap.add_argument("--idiom", help="idiom.json 路径(可选,导入成语为词组)")
    ap.add_argument("--out", default="dictionary.sql", help="输出SQL路径")
    ap.add_argument("--db", action="store_true", help="直接写数据库(需MySQL运行)")
    ap.add_argument("--limit", type=int, default=0, help="限制条数(测试用,0=全部)")
    args = ap.parse_args()

    sf = Snowflake()
    columns = ["dict_id", "entry", "type", "pinyin", "radical", "stroke", "wubi", "explain", "source", "hit_count"]
    conn = get_conn() if args.db else None
    f = open(args.out, "w", encoding="utf-8") if not args.db else None
    if f:
        f.write("-- chinese-xinhua 字典导入\nSET NAMES utf8mb4;\n")

    count = 0
    batch = []

    def flush(table):
        if args.db:
            placeholders = ",".join(["%s"] * len(columns))
            sql = "INSERT INTO `%s` (%s) VALUES (%s)" % (table, ",".join("`%s`" % c for c in columns), placeholders)
            with conn.cursor() as cur:
                for r in batch:
                    cur.execute(sql, list(r))
            batch.clear()
        else:
            write_batch(f, table, columns, batch)
            batch.clear()

    words = load_json(args.word)
    seen = set()  # 去重
    for w in words:
        if args.limit and count >= args.limit:
            break
        entry = w.get("word")
        if not entry:
            continue
        if entry in seen:
            continue
        seen.add(entry)
        explain = parse_explanation(w.get("explanation"))
        row = (
            sf.next_id(), entry, 1,
            w.get("pinyin"), w.get("radicals"), w.get("strokes"), w.get("wubi"),
            explain, "chinese-xinhua", 0,
        )
        batch.append(row)
        count += 1
        if len(batch) >= 500:
            flush("dictionary")

    # 成语/词组
    if args.idiom and os.path.exists(args.idiom):
        idioms = load_json(args.idiom)
        for w in idioms:
            entry = w.get("word")
            if not entry:
                continue
            explain = parse_explanation(w.get("explanation"))
            row = (
                sf.next_id(), entry, 2,
                w.get("pinyin"), w.get("radicals"), w.get("strokes"), None,
                explain, "chinese-xinhua-idiom", 0,
            )
            batch.append(row)
            if len(batch) >= 500:
                flush("dictionary")

    flush("dictionary")
    if f:
        f.close()
    if conn:
        conn.close()
    print("导入完成, 共 %d 条 -> %s" % (count, args.out if f else "数据库"))


if __name__ == "__main__":
    main()