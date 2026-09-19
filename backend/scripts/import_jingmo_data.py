# -*- coding: utf-8 -*-
"""导入 hefengbao/jingmo-data(+data3) -> article / word_lib / dictionary。

数据源（gh-pages 分支，api/*.json）：
  jingmo-data      : 经典诗文、汉字(characters)、字典(dict/chinese_dict)、词语(expression)、成语(idiom)
  jingmo-data3     : 海量文言文章(classical_literature_writings_v2_*.json)

格式：
  多数文件顶层 {"data":[...]}, 诗文类某几类为裸数组。
  经典诗文字段: dynasty/writer/title/content/translation/annotation/comment
  文言文章字段: Dynasty/Author/Title/Clauses/Note/Comments
  汉字字段: id/char/strokes/pinyin/radicals/...(characters) 或 id/char/wubi/radical/stroke/pinyin/simple_explanation(dict)
  词语: 含 word/expression 等
  成语: idiom

用法：
  python import_jingmo.py --poems-dir /path/jingmo-data/api --writings-dir /path/jingmo-data3/api \
      --dict-dir /path/jingmo-data/api --out jingmo.sql [--db]
"""
import argparse
import json
import os
import re
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from common import Snowflake, get_conn, write_batch


def iter_data(path):
    """兼容 {"data":[...]} 与裸数组，逐个 item 产出。"""
    with open(path, "r", encoding="utf-8") as f:
        raw = json.load(f)
    if isinstance(raw, dict):
        items = raw.get("data") or raw.get("poems") or []
    else:
        items = raw
    for it in items:
        if isinstance(it, dict):
            yield it


# ============ 经典诗文 -> article ============
def parse_classic_poem(o):
    title = o.get("title")
    if not title:
        return None
    author = o.get("writer")
    dynasty = o.get("dynasty")
    content = o.get("content")
    if isinstance(content, list):
        content = "\n".join(str(x) for x in content)
    translation = o.get("translation") or o.get("translate")
    annotation = o.get("annotation")
    comment = o.get("comment") or o.get("appreciate")
    if isinstance(annotation, (list, dict)):
        annotation = json.dumps(annotation, ensure_ascii=False)
    if isinstance(translation, list):
        translation = "\n".join(str(x) for x in translation)
    if isinstance(comment, list):
        comment = "\n".join(str(x) for x in comment)
    return dict(title=str(title)[:128], author=author, dynasty=dynasty,
                content=content, translate=translation, appreciate=comment,
                notes=annotation, genre=o.get("category") or "诗词",
                source="hefengbao/jingmo(classic)")

# ============ 文言文章 -> article ============
def parse_writing(o):
    title = o.get("Title")
    if not title:
        return None
    author = o.get("Author")
    dynasty = o.get("Dynasty")
    clauses = o.get("Clauses")
    if isinstance(clauses, list):
        content = "\n".join(str(c) for c in clauses)
    else:
        content = clauses
    note = o.get("Note")
    comments = o.get("Comments")
    if isinstance(note, (list, dict)):
        note = json.dumps(note, ensure_ascii=False)
    if isinstance(comments, list):
        comments = "\n".join(str(c) for c in comments)
    return dict(title=str(title)[:128], author=author, dynasty=dynasty,
                content=content, translate=None, appreciate=str(comments or ""),
                notes=note, genre=o.get("Type") or "古文",
                source="hefengbao/jingmo-data3")

# ============ 汉字/字典 -> dictionary ============
def parse_hanzi(o):
    """兼容 characters(旧) 与 dict/chinese_dict(新)。entry 取 char。"""
    entry = o.get("char")
    if not entry:
        entry = o.get("word")
    if not entry:
        return None
    # 笔画
    stroke = o.get("strokes") or o.get("stroke")
    # 部首
    radical = o.get("radicals") or o.get("radical")
    # 拼音(可能为列表)
    py = o.get("pinyin")
    pinyin = None
    if isinstance(py, list):
        pinyin = "/".join(str(p) for p in py if p)
    elif py:
        pinyin = str(py)
    # 释义
    explain_raw = o.get("simple_explanation") or o.get("explanation") or o.get("definition")
    explain = None
    if isinstance(explain_raw, list):
        explain_raw = "\n".join(str(x) for x in explain_raw)
    if explain_raw:
        explain = json.dumps([{"def": str(explain_raw)[:2000], "example": None}], ensure_ascii=False)
    # 五笔
    wubi = o.get("wubi")
    return dict(entry=entry, type=1, pinyin=pinyin, radical=radical,
                stroke=stroke, wubi=wubi, explain=explain, source="jingmo")

def parse_expr(o):
    """词语 expression -> word_lib。字段兼容多种。"""
    word = o.get("word") or o.get("expression") or o.get("content") or o.get("name")
    if not word:
        return None
    explain_raw = o.get("explanation") or o.get("explain") or o.get("definition") or o.get("simple_explanation")
    if isinstance(explain_raw, list):
        explain_raw = "\n".join(str(x) for x in explain_raw)
    return dict(word=str(word)[:64], word_type=1, pos=o.get("pos") or o.get("cixing"),
                explain=str(explain_raw or "")[:2000], example=o.get("example"),
                difficulty=2, source="jingmo")

def parse_idiom(o):
    word = o.get("word") or o.get("name") or o.get("content")
    if not word:
        return None
    explain_raw = o.get("explanation") or o.get("explain") or o.get("meaning")
    if isinstance(explain_raw, list):
        explain_raw = "\n".join(str(x) for x in explain_raw)
    return dict(word=str(word)[:64], word_type=2, pos=o.get("char") or "",
                explain=str(explain_raw or "")[:2000], example=o.get("example") or o.get("case"),
                difficulty=2, source="jingmo")


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--poems-dir", help="jingmo-data api 目录(经典诗文/字词/成语)")
    ap.add_argument("--writings-dir", help="jingmo-data3 api 目录(海量文言文章)")
    ap.add_argument("--out", default="jingmo.sql")
    ap.add_argument("--db", action="store_true")
    ap.add_argument("--with-dict", action="store_true", help="同时导入汉字/字典/词语/成语")
    ap.add_argument("--limit", type=int, default=0, help="每类限制条数")
    args = ap.parse_args()

    sf = Snowflake()
    conn = get_conn() if args.db else None
    f = open(args.out, "w", encoding="utf-8") if not args.db else None
    if f:
        f.write("-- jingmo 数据导入\nSET NAMES utf8mb4;\n")

    art_cols = ["article_id", "title", "author", "dynasty", "content", "translate",
                "appreciate", "notes", "genre", "grade", "is_required", "source"]
    wl_cols = ["word_id", "word", "word_type", "pos", "explain", "example", "difficulty", "source"]
    dict_cols = ["dict_id", "entry", "type", "pinyin", "radical", "stroke", "wubi", "explain", "source", "hit_count"]

    art_rows, wl_rows, dict_rows = [], [], []
    count = {"article": 0, "word": 0, "dict": 0}

    def flush():
        if args.db:
            def _exec(table, cols, rows):
                if not rows: return
                ph = ",".join(["%s"] * len(cols))
                sql = "INSERT INTO `%s` (%s) VALUES (%s)" % (table, ",".join("`%s`" % c for c in cols), ph)
                with conn.cursor() as cur:
                    for r in rows:
                        try: cur.execute(sql, list(r))
                        except Exception: pass
            if art_rows: _exec("article", art_cols, art_rows)
            if wl_rows: _exec("word_lib", wl_cols, wl_rows)
            if dict_rows: _exec("dictionary", dict_cols, dict_rows)
        else:
            if art_rows: write_batch(f, "article", art_cols, art_rows)
            if wl_rows: write_batch(f, "word_lib", wl_cols, wl_rows)
            if dict_rows: write_batch(f, "dictionary", dict_cols, dict_rows)
        art_rows.clear(); wl_rows.clear(); dict_rows.clear()

    def add_article(o):
        a = o
        art_rows.append((sf.next_id(), a["title"], a["author"], a["dynasty"], a["content"],
                         a["translate"], a["appreciate"], a["notes"], a["genre"],
                         0, 0, a["source"]))
        count["article"] += 1
        if len(art_rows) >= 300: flush()

    # 1. 经典诗文
    if args.poems_dir:
        for fn in sorted(os.listdir(args.poems_dir)):
            if args.limit and count["article"] >= args.limit: break
            if not (re.search(r"classic.*poem|poem.*classic|classic_poem", fn, re.I)):
                continue
            # 同一批经典诗文在多个副本文件中重复经典诗三份(classic_poems.json/..._v3.json/..._v3_1.json)，
            # 仅保留主文件，避免重复录入
            low = fn.lower()
            if "classicpoem_v3_1" in low:
                continue
            if low == "classic_poems.json" and os.path.exists(os.path.join(args.poems_dir, "classical_literature_classic_poems_v3.json")):
                continue
            p = os.path.join(args.poems_dir, fn)
            for o in iter_data(p):
                if args.limit and count["article"] >= args.limit: break
                a = parse_classic_poem(o)
                if a: add_article(a)
        # 2. 文言文章
        if args.writings_dir and args.poems_dir != args.writings_dir:
            for fn in sorted(os.listdir(args.writings_dir)):
                if args.limit and count["article"] >= args.limit: break
                if not fn.startswith("classical_literature_writings"): continue
                for o in iter_data(os.path.join(args.writings_dir, fn)):
                    if args.limit and count["article"] >= args.limit: break
                    a = parse_writing(o)
                    if a: add_article(a)

    # 3. 字典/字词(optional)
    if args.with_dict and args.poems_dir:
        seen_dict = set()
        seen_wl = set()
        for fn in sorted(os.listdir(args.poems_dir)):
            if args.limit and count["dict"] >= args.limit: break
            low = fn.lower()
            if "character" in low or "chinese_dict" in low or re.match(r"dict_", fn):
                for o in iter_data(os.path.join(args.poems_dir, fn)):
                    if args.limit and count["dict"] >= args.limit: break
                    h = parse_hanzi(o)
                    if h and h["entry"] not in seen_dict:
                        seen_dict.add(h["entry"])
                        dict_rows.append((sf.next_id(), h["entry"], h["type"], h["pinyin"],
                                          h["radical"], h["stroke"], h["wubi"], h["explain"], h["source"], 0))
                        count["dict"] += 1
                        if len(dict_rows) >= 300: flush()
            # 词语/成语 -> word_lib
            if "expression" in low or "idiom" in low:
                is_idiom = "idiom" in low
                for o in iter_data(os.path.join(args.poems_dir, fn)):
                    w = parse_idiom(o) if is_idiom else parse_expr(o)
                    if w and w["word"] not in seen_wl:
                        seen_wl.add(w["word"])
                        wl_rows.append((sf.next_id(), w["word"], w["word_type"], w["pos"],
                                        w["explain"], w["example"], w["difficulty"], w["source"]))
                        count["word"] += 1
                        if len(wl_rows) >= 300: flush()

    flush()
    if f: f.close()
    if conn: conn.close()
    print("完成: article=%d word=%d dict=%d -> %s" % (count["article"], count["word"], count["dict"], args.out if f else "DB"))


if __name__ == "__main__":
    main()