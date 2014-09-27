#!/usr/bin/env python
#coding:utf-8
import datetime

import pymongo
import json
import zlib

"""
Ultimately you can't store raw bytes in a JSON document, so you'll want to use some means of unambiguously encoding a sequence of arbitrary bytes as an ASCII string - such as base64.

use base 64 of avoid:
JSONDecoder UnicodeDecodeError: 'utf8' codec can't decode byte
"""
from base64 import b64encode, b64decode

import stock_oper


class MongodHandler():
    conn = None
    server = "mongodb://localhost:27017"

    def getConn(self):
        if self.conn is None:
            self.conn = pymongo.Connection(self.server)
        return self.conn

    def getHistDetail(self):
        return self.getConn().stock.hist_detail

class MStockOper():
    def __init__(self):
        self.handler = MongodHandler()

    @staticmethod
    def getId(stock_code, date):
        return stock_code + date

    @staticmethod
    def getJsonOfHistDetail(histdetail, compress=True):
        stock = {'_id': MStockOper.getId(histdetail.stock_code, datetime.date.strftime(histdetail.date, '%Y%m%d'))}
        exchanges = {}
        for exchange in histdetail.exchanges:
            value = "%s,%s,%s" % (exchange.price, exchange.trade_hand, exchange.sell)
            exchanges[exchange.time] = value
        if compress:
            exchanges = zlib.compress(json.dumps(exchanges), zlib.Z_BEST_COMPRESSION)
            exchanges = b64encode(exchanges)
        stock['v'] = exchanges
        return stock

    @staticmethod
    def decodeJsonOfHistDetail(encoding, compress=True):
        stock = json.JSONDecoder().decode(encoding)
        if compress:
            tmp = b64decode(stock['v'])
            tmp = zlib.decompress(tmp)
            stock['v'] = json.JSONDecoder().decode(tmp)
        return stock

    def insertHistDetail(self, histdetail):
        collection = self.handler.getHistDetail()
        collection.insert(histdetail)

    def queryHistDetail(self, stock_code, date):
        strId = MStockOper.getId(stock_code, date)
        return self.handler.getHistDetail().find_one({"_id": strId})



test_filename = '/home/chookin/stock/market.finance.sina.com.cn/2014-09-25/sz300397.dat'
dboper = MStockOper()

def test_mongod_conn():
    handler = MongodHandler()
    handler.connect()
    #列出server_info信息
    conn = handler.getConn()
    print conn.server_info()

    #列出全部数据库
    databases = conn.database_names()
    print "全部数据库", databases


def test_insert_histdetail():
    histdetail = stock_oper.HistDetail.extract(test_filename)
    histdetail = MStockOper.getJsonOfHistDetail(histdetail, False)
    dboper.insertHistDetail(histdetail)

def test_query_histdetail():
    print dboper.queryHistDetail("300397", "2014-09-25")


def test_compress():
    """
    zlib多用于网络收发字符串的压缩与解压
    测试结果：len(raw_data)=13779, len(zb_data)=2485, compression ratio=0.18
    """
    histdetail = stock_oper.HistDetail.extract(test_filename)
    raw_data = MStockOper.getJsonOfHistDetail(histdetail, False)
    zb_data = MStockOper.getJsonOfHistDetail(histdetail)
    raw_data = json.dumps(raw_data)
    zb_data = json.dumps(zb_data)

    print "len(raw_data)=%d, len(zb_data)=%d, compression ratio=%.2f" \
          % (len(raw_data), len(zb_data), float(len(zb_data))/len(raw_data))

    stock1 = MStockOper.decodeJsonOfHistDetail(raw_data, False)
    stock2 = MStockOper.decodeJsonOfHistDetail(zb_data)
    print stock1 == stock2
    print stock2


if __name__ == "__main__":
    # test_mongod_conn()
    # test_compress()
    # test_insert_histdetail()
    test_query_histdetail
    pass
