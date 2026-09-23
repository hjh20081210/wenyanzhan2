# -*- coding: utf-8 -*-
import re, os
BASE = "/Coze/Drive/文言斩/wenyanzhan/data_repos/chinabook_tog/"
OUT = "/Coze/Drive/文言斩/wenyanzhan/data_import/body_parts/"
os.makedirs(OUT, exist_ok=True)

def read_lines(book):
    with open(BASE + book + ".txt", encoding="utf-8") as f:
        return [l.rstrip("\n") for l in f]

PAGE_MARK = re.compile(r'^\s*\d+\s*语文\s*(必修|选择性必修)\S*\s*(上|下)?[册]?\s*$|^\s*第.单元\s*\d+\s*$')

def extract(lines, start, end):
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
        if re.match(r'^[a-z]\s*(选自|〔)', stripped):
            in_fn = True
            continue
        if re.match(r'^学习提示|^单元学习任务|^单元研习任务|^注：|^后\s*记|^插图|^题图', stripped):
            in_fn = True
            continue
        line = re.sub(r'(?<![a-zA-Z])[a-zA-Z](?![a-zA-Z])', '', line)
        line = re.sub(r'[@#]\d+', '', line)
        line = line.replace('　','')
        if line.strip():
            out.append(line.strip())
    return "\n".join(out)

# (book, title, start, end)  -- title only for output filename
PIECES = [
    # 必修上
    ("必修上","短歌行",2335,2378),
    ("必修上","归园田居其一",2383,2406),
    ("必修上","梦游天姥吟留别",2428,2491),
    ("必修上","登高",2496,2530),
    ("必修上","念奴娇赤壁怀古",2676,2723),
    ("必修上","永遇乐京口北固亭怀古",2727,2765),
    ("必修上","声声慢",2769,2804),
    ("必修上","赤壁赋",4766,4870),
    ("必修上","静女",5745,5758),
    ("必修上","涉江采芙蓉",5787,5799),
    ("必修上","虞美人",5812,5827),
    ("必修上","鹊桥仙",5842,5862),
    # 必修下
    ("必修下","登岳阳楼",7647,7684),
    ("必修下","桂枝香金陵怀古",7686,7731),
    ("必修下","念奴娇过洞庭",7733,7781),
    ("必修下","游园",7785,7835),
    # 选必修上
    ("选必修上","论语十二章",1705,1783),
    ("选必修上","大学之道",1785,1825),
    ("选必修上","人皆有不忍人之心",1827,1868),
    ("选必修上","老子四章",1890,1953),
    ("选必修上","五石之瓠",1955,2000),
    ("选必修上","兼爱",2040,2090),
    ("选必修上","无衣",3978,4000),
    ("选必修上","春江花月夜",4012,4065),
    ("选必修上","将进酒",4077,4118),
    ("选必修上","江城子",4120,4155),
    # 选必修中
    ("选必修中","屈原列传",3356,3558),
    ("选必修中","苏武传",3560,3814),
    ("选必修中","过秦论",3816,3960),
    ("选必修中","五代史伶官传序",3964,4048),
    ("选必修中","燕歌行并序",5116,5190),
    ("选必修中","李凭箜篌引",5193,5240),
    ("选必修中","锦瑟",5241,5282),
    ("选必修中","书愤",5268,5278),
    # 选必修下
    ("选必修下","氓",215,300),
    ("选必修下","离骚节选",307,475),
    ("选必修下","孔雀东南飞并序",517,858),
    ("选必修下","蜀道难",901,1026),
    ("选必修下","蜀相",1030,1055),
    ("选必修下","望海潮",1060,1110),
    ("选必修下","扬州慢",1112,1160),
    ("选必修下","陈情表",3538,3663),
    ("选必修下","项脊轩志",3665,3774),
    ("选必修下","兰亭集序",3806,3907),
    ("选必修下","归去来兮辞并序",3909,4105),
    ("选必修下","种树郭橐驼传",4106,4222),
    ("选必修下","石钟山记",4224,4297),
    ("选必修下","拟行路难其四",5514,5560),
    ("选必修下","客至",5562,5595),
    ("选必修下","登快阁",5596,5641),
    ("选必修下","临安春雨初霁",5643,5690),
]

for book, title, s, e in PIECES:
    txt = extract(read_lines(book), s, e)
    fn = OUT + f"{book}_{title}.txt"
    with open(fn, "w", encoding="utf-8") as f:
        f.write(txt)
    print("="*20, book, title, "len", len(txt))
    print(txt[:180])

print("\nDONE. files:", len(os.listdir(OUT)))