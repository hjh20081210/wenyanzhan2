# -*- coding: utf-8 -*-
import re, sys

BASE = "/Coze/Drive/文言斩/wenyanzhan/data_repos/chinabook_tog/"

def read_lines(book):
    with open(BASE + book + ".txt", encoding="utf-8") as f:
        return [l.rstrip("\n") for l in f]

PAGE_MARK = re.compile(r'^\s*\d+\s*语文\s*(必修|选择性必修)\S*\s*(上|下)?册?\s*$|第.单元\s*\d+\s*$')

def extract(lines, start, end):
    """start/end are 1-based inclusive line numbers. Returns cleaned 正文 text."""
    out = []
    in_fn = False
    for i in range(start-1, min(end, len(lines))):
        line = lines[i]
        stripped = line.strip()
        if not stripped:
            continue
        if PAGE_MARK.match(line):
            in_fn = False
            continue
        if in_fn:
            continue
        # footnote start: line starts with single lower latin letter
        if re.match(r'^[a-z][\s(（]', stripped):
            in_fn = True
            continue
        # drop 学习提示 and surrounding headings, 单元任务, exercise-like headings
        if re.match(r'^学习提示$|^单元学习任务|^单元研习任务|^注：', stripped):
            in_fn = True
            continue
        # remove inline single latin letters (footnote refs) and @# digits
        line = re.sub(r'(?<![a-zA-Z])[a-zA-Z](?![a-zA-Z])', '', line)
        line = re.sub(r'[@#]\d+', '', line)
        # drop pure image-caption lines
        out.append(line.strip())
    return "\n".join(out)

def flow(text):
    """join wrapped lines into paragraphs for essays/prose."""
    # split by blank-med architectures: use original; here just return
    return text

if __name__ == "__main__":
    for book, start, end in [
        ("必修上", 2335, 2404),
        ("必修上", 4766, 4958),
    ]:
        print("#####", book, start, end, "#####")
        print(extract(read_lines(book), start, end))
        print("\n\n")