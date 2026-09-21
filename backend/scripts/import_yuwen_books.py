#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""hantang/yuwen 2024统编教材 导入 -> article
docs分支 text/ 下 md 文件: YAML front-matter(title/author/unit/index/page/desc) + 正文 + [^n]:脚注注释
只录入文言相关篇目(古代/文言/古诗词), 按书本整理(book=册名, grade=学段)
用法: python3 import_yuwen_books.py <yuwen_docs_path> <out.sql>
"""
import os, sys, re, glob

def parse_md(path, book, grade):
    raw = open(path, encoding='utf-8').read()
    # YAML front-matter
    m = re.match(r'^---\n(.*?)\n---\n(.*)$', raw, re.S)
    meta = {}
    body = raw
    if m:
        yml, body = m.group(1), m.group(2)
        for kv in re.finditer(r'^(\w+):\s*"(.*?)"\s*$', yml, re.M):
            meta[kv.group(1)] = kv.group(2)
        # desc 块
        dm = re.search(r'^desc:\s*\|\n((?:\s+\S.*\n?)+)', yml, re.M)
        if dm: meta['desc'] = ' '.join(l.strip() for l in dm.group(1).splitlines())
    title = meta.get('title','')
    author = meta.get('author','')
    # 收集脚注注释
    notes = {}
    for nm in re.finditer(r'^\[\^(.+?)\]:\s*(.+?)(?=^\[\^|\Z)', body, re.M | re.S):
        key, val = nm.group(1), nm.group(2).strip()
        notes[key] = re.sub(r'\s+', ' ', val)
    # 正文: 去掉脚注定义行
    content_lines = []
    for line in body.splitlines():
        if re.match(r'^\[\^.*\]:', line): continue
        content_lines.append(line)
    content = '\n'.join(content_lines).strip()
    notes_json = None
    if notes:
        import json
        ar = [{"k": k, "v": v} for k, v in notes.items()]
        notes_json = json.dumps(ar, ensure_ascii=False).replace("'", "''")
    return title, author, content, notes_json

def is_wenyan(filename, content):
    # 严格文言识别
    # 1) 标题/文件名明确文言标志
    kw = ['诫子','诫','观沧海','论语','世说新语','穿井','杞人','北冥有鱼','庄子与惠子','庄子','富贵不能','生于忧患','愚公','周亚夫','三峡','与朱元思','答谢中书','记承天寺','狼','咏雪','陈太丘','孙权劝学','木兰','卖油翁','陋室铭','爱莲说','河中石兽','短文两篇','与朱元思书','孟子','生于忧患死于安乐','两小儿辩日','学弈','伯牙鼓琴','书戴嵩画牛','古人谈读书','杨氏之子','自相矛盾','守株待兔','精卫填海','王戎不取道旁李','囊萤夜读','铁杵成针','司马光','曹冲称象','称象','所见','小儿垂钓','夜书所见','山行','赠刘景文','早发白帝城','望天门山','饮湖上初晴','望洞庭','绝句','惠崇春江','三衢道中','忆江南','宿新市徐公店','四时田园杂兴','清平乐','题临安邸','己亥杂诗','枫桥夜泊','长相思','山居秋暝','古诗词','课外古诗词','示儿','题西林壁','雪梅','江雪','寻隐者不遇','元日','春晓','咏柳','村居','赋得','咏鹅','游子吟','悯农','九月九日','静夜思','池上','小池','画鸡','塞下曲','芙蓉楼送辛','竹石','闻官军','千里莺啼','六月二十七日','惠崇','晓出净慈','卜算子','梅花','夜宿山寺','敕勒歌','古诗','西江月','天净沙','山坡羊','关雎','蒹葭','行路难','酬乐天','水调歌头','岳阳楼记','醉翁亭记','湖心亭看雪','满江红','出师表','鱼我所欲也','唐雎','送东阳马生序','曹刿论战','邹忌讽齐王','陈涉世家','隆中对','马说','马说','小石潭记','桃花源记','岳阳楼','隆中对','荆轲','邹忌','唐雎','送东阳']
    if any(k in filename for k in kw): return True
    # 2) 内容启发式: 含文言虚词密集 且 不含现代白话高频词
    if '[^' not in content: return False
    wenyan_char = sum(1 for c in '之乎者也矣焉哉曰' if c in content)
    baihua = sum(1 for w in ['的','了','我们','你们','他们','吗','呢','啊','被','把','这','那','是','很','和','个','说'] if w in content[:600])
    if wenyan_char >= 3 and baihua <= 5 and len(content) < 4000:
        return True
    return False

GRADE = {'1':'3','2':'3','3':'3','4':'3','5':'3','6':'3','7':'1','8':'1','9':'1'}  # 学段: 小3初1高2
BOOKGRADE = {'高中':'2'}

def main():
    src = sys.argv[1] if len(sys.argv)>1 else '/tmp/yuwen_t'
    out = sys.argv[2] if len(sys.argv)>2 else '/Coze/Drive/文言斩/wenyanzhan/data_import/yuwen_books.sql'
    aid = int(os.environ.get('START_ID', '8160000000000000000'))
    rows = []
    n = 0
    # 遍历册目录
    for bookdir in sorted(glob.glob(os.path.join(src, '*'))):
        base = os.path.basename(bookdir)
        if not os.path.isdir(bookdir) or 'archive' in base: continue
        # 册名如 07a-七年级上册
        mm = re.match(r'^(\d+)[abx].*?([一二三四五六七八九]+年级|高中[^ ]*)', base)
        book = base.split('-',1)[1] if '-' in base else base
        # 判断学段
        if '高中' in base: grade = '2'
        elif '年级' in base:
            g = base.split('-')[1][:1] if '-' in base else ''
            grade = GRADE.get(g, '1')
        else: grade = '1'
        for md in glob.glob(os.path.join(bookdir, 'text', '**', '*.md'), recursive=True):
            fn = os.path.basename(md)
            if fn == 'index.md': continue
            title, author, content, notes = parse_md(md, book, grade)
            if not content: continue
            if not is_wenyan(fn + title, content): continue
            a = aid + n
            genre = '文言文' if ('[' in content or len(content)<3000) else '古诗词'
            rows.append(f"({a},'{title.replace(chr(39),chr(39)*2)}','{author.replace(chr(39),chr(39)*2)}','','{content.replace(chr(39),chr(39)*2)}','{genre}','{book}','',{('NULL' if notes is None else chr(39)+notes+chr(39))},'{book}')")
            n += 1
    with open(out, 'w', encoding='utf-8') as f:
        f.write("-- hantang/yuwen 2024统编教材文言篇目(按书本)\n")
        f.write("SET NAMES utf8mb4;\n")
        f.write("INSERT INTO article (article_id,title,author,dynasty,content,genre,book,pinyin,notes,source) VALUES\n")
        f.write(",\n".join(rows) + ";\n")
    print(f"录入文言篇目: {n} 篇 -> {out}")

if __name__ == '__main__':
    main()