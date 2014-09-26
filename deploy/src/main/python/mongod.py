#!/usr/bin/env python
#coding:utf-8

import pymongo

class MongodConn():
    conn = None
    server = "mongodb://localhost:27017"

    def connect(self):
        self.conn = pymongo.Connection(self.server)

    def close(self):
        self.conn.disconnect()

    def getConn(self):
        return self.conn

import stock_oper

class MStockOper():
    @staticmethod
    def insertHistDetail(histdetail):
        id = histdetail.stock_code+histdetail.date


if __name__ == "__main__":
    mongodConn = MongodConn()
    mongodConn.connect()
    #列出server_info信息
    conn = mongodConn.getConn()
    print conn.server_info()

    #列出全部数据库
    databases = conn.database_names()
    print "全部数据库", databases