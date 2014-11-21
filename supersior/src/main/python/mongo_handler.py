#!/usr/bin/env python
# coding:utf-8
import datetime

"""
mongodb data store directory is controlled by config 'dbpath' in /etc/mongod.conf
"""
import pymongo

"""
Ultimately you can't store raw bytes in a JSON document, so you'll want to use some means of unambiguously encoding a sequence of arbitrary bytes as an ASCII string - such as base64.

use base 64 of avoid:
JSONDecoder UnicodeDecodeError: 'utf8' codec can't decode byte
"""


class MongoHandler():
    def __init__(self):
        pass

    conn = None
    server = "mongodb://localhost:27017"

    def get_conn(self):
        if self.conn is None:
            self.conn = pymongo.Connection(self.server)
        return self.conn

    def close(self):
        if self.conn is None:
            return
        self.conn.close()
        self.conn = None


def test_mongo_conn():
    handler = MongoHandler()
    # 列出server_info信息
    conn = handler.get_conn()
    print conn.server_info()

    # 列出全部数据库
    databases = conn.database_names()
    print "全部数据库", databases


if __name__ == "__main__":
    test_mongo_conn()
    pass
