# -*- coding: utf-8 -*-
"""文言斩数据导入公共工具。
提供雪花ID(可用自增代替)、MySQL连接、JSON加载等公共能力。
所有导入脚本在部署环境具备 JDK/MySQL 时执行；本工具亦可输出 SQL 供手动导入。
"""
import json
import os
import random
import time
import sys

# ---------- 雪花ID ----------
class Snowflake:
    def __init__(self, worker_id=1):
        self.worker_id = worker_id & 0x3FF
        self.sequence = 0
        self.last_ms = -1
        self.twepoch = 1288834974657

    def next_id(self):
        ms = int(time.time() * 1000)
        if ms < self.last_ms:
            raise Exception("clock moved backwards")
        if ms == self.last_ms:
            self.sequence = (self.sequence + 1) & 0xFFF
            if self.sequence == 0:
                ms = self._wait_next_ms(self.last_ms)
        else:
            self.sequence = 0
        self.last_ms = ms
        return ((ms - self.twepoch) << 22) | (self.worker_id << 12) | self.sequence

    def _wait_next_ms(self, last_ms):
        ms = int(time.time() * 1000)
        while ms <= last_ms:
            ms = int(time.time() * 1000)
        return ms


# ---------- MySQL ----------
def get_conn():
    """返回MySQL连接。可用环境变量覆盖默认值。"""
    import pymysql
    return pymysql.connect(
        host=os.getenv("DB_HOST", "127.0.0.1"),
        port=int(os.getenv("DB_PORT", "3306")),
        user=os.getenv("DB_USER", "root"),
        password=os.getenv("DB_PASSWORD", "root"),
        database=os.getenv("DB_NAME", "wenyan_zhan"),
        charset="utf8mb4",
        autocommit=True,
    )


# ---------- JSON ----------
def load_json(path):
    with open(path, "r", encoding="utf-8") as f:
        return json.load(f)


def load_jsonl(path):
    rows = []
    with open(path, "r", encoding="utf-8") as f:
        for line in f:
            line = line.strip()
            if line:
                rows.append(json.loads(line))
    return rows


# ---------- SQL 导出（无DB时生成 .sql 便于手动导入） ----------
def sql_str(v, max_len=None):
    """安全转义字符串为SQL字面量。"""
    if v is None:
        return "NULL"
    s = str(v).replace("\\", "\\\\").replace("'", "\\'")
    if max_len:
        s = s[:max_len]
    return "'" + s + "'"


def write_batch(f, table, columns, rows):
    """rows: list of tuples, 与 columns 对齐。写入INSERT批量语句。"""
    if not rows:
        return
    col_sql = ",".join("`%s`" % c for c in columns)
    f.write("INSERT INTO `%s` (%s) VALUES\n" % (table, col_sql))
    lines = []
    for r in rows:
        lines.append("(" + ",".join(sql_str(v) for v in r) + ")")
    f.write(",\n".join(lines) + ";\n")


def batch_iter(seq, size=500):
    for i in range(0, len(seq), size):
        yield seq[i:i + size]