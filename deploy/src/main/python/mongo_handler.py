#!/usr/bin/env python
#coding:utf-8
import datetime

"""
mongodb data store directory is controlled by config 'dbpath' in /etc/mongod.conf
"""
import os
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


class MongoHandler():
    conn = None
    server = "mongodb://localhost:27017"

    def getConn(self):
        if self.conn is None:
            self.conn = pymongo.Connection(self.server)
        return self.conn

    def close(self):
        if self.conn is None:
            return
        self.conn.close()
        self.conn = None

    def getHistDetail(self):
        return self.getConn().stock.hist_detail

class MHistDetail():
    def __init__(self):
        self.handler = MongoHandler()

    @staticmethod
    def getId(stock_code, date):
        return stock_code + datetime.date.strftime(date, '%Y%m%d')

    @staticmethod
    def getJson(histdetail, compress=True):
        """
        get the json object that can direct insert into mongodb
        :param histdetail: the HistDetail object
        :param compress: whether compress exchange detail
        :return: a json object, not a json string
        """
        stock = {'_id': MHistDetail.getId(histdetail.stock_code, histdetail.date)}
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
    def decodeJson(encoding, compress=True):
        """
        decode the json object that fetching from mongodb
        :param encoding: the source json object
        :param compress: whether the source json object is compressed
        :return:
        """
        stock = encoding
        if compress:
            tmp = b64decode(stock['v'])
            tmp = zlib.decompress(tmp)
            stock['v'] = json.JSONDecoder().decode(tmp)
        return stock

    def insertHistDetail(self, histdetail):
        collection = self.handler.getHistDetail()
        collection.insert(histdetail)

    def queryHistDetailByDateString(self, stock_code, strdate):
        strId = MHistDetail.getId(stock_code, datetime.datetime.strptime(strdate, '%Y-%m-%d').date())
        record = self.handler.getHistDetail().find_one({"_id": strId})
        return MHistDetail.decodeJson(record)



test_filename = '/home/chookin/stock/market.finance.sina.com.cn/2014-09-25/sz300397.dat'
dboper = MHistDetail()

def test_mongod_conn():
    handler = MongoHandler()
    handler.connect()
    #列出server_info信息
    conn = handler.getConn()
    print conn.server_info()

    #列出全部数据库
    databases = conn.database_names()
    print "全部数据库", databases


def test_insert_histdetail():
    histdetail = stock_oper.HistDetail.extract(test_filename)
    histdetail = MHistDetail.getJson(histdetail)
    dboper.insertHistDetail(histdetail)


def test_query_histdetail():
    print dboper.queryHistDetailByDateString("300397", "2014-09-25")


def test_compress():
    """
    zlib多用于网络收发字符串的压缩与解压
    测试结果：len(raw_data)=13779, len(zb_data)=2485, compression ratio=0.18
    """
    histdetail = stock_oper.HistDetail.extract(test_filename)
    raw_data = MHistDetail.getJson(histdetail, False)
    zb_data = MHistDetail.getJson(histdetail)
    raw_data = json.dumps(raw_data)
    zb_data = json.dumps(zb_data)

    print "len(raw_data)=%d, len(zb_data)=%d, compression ratio=%.2f" \
          % (len(raw_data), len(zb_data), float(len(zb_data))/len(raw_data))

    stock1 = MHistDetail.decodeJson(raw_data, False)
    stock2 = MHistDetail.decodeJson(zb_data)
    print stock1 == stock2
    print stock2

def saveHistDetail(path):
    """
    save hist detail int the file of path
    :param path: name of the path, the path could be a directory or a file
    """
    if os.path.isfile(path):
        record = stock_oper.HistDetail.extract(path)
        record = MHistDetail.getJson(record)
        dboper.insertHistDetail(record)
        dboper.handler.close()
        return

    import file_utils
    filenames = file_utils.getfilenames(path)
    histdetails = []
    count = 0
    for item in filenames:
        count += 1
        record = stock_oper.HistDetail.extract(item)
        record = MHistDetail.getJson(record)
        histdetails.append(record)
        if len(histdetails) == 50:
            dboper.insertHistDetail(histdetails)
            print '%s reords inserted' % count
            histdetails = []
    if len(histdetails) > 0:
        # If the list is empty, PyMongo raises an exception:
        #   pymongo.errors.InvalidOperation: cannot do an empty bulk insert
        dboper.insertHistDetail(histdetails)
        print 'insert %s reords' % len(histdetails)
    print 'total insert %s records for %s' % (count, path)
    dboper.handler.close()

if __name__ == "__main__":
    # test_mongod_conn()
    # test_compress()
    # test_insert_histdetail()
    # test_query_histdetail()
    saveHistDetail('/home/chookin/stock/market.finance.sina.com.cn/2014-09-24')
    # saveHistDetail('/home/chookin/stock/market.finance.sina.com.cn/2014-09-25/sh600157.dat')
    pass
