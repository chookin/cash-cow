#!/usr/bin/env python
# coding:utf-8
from base64 import b64encode, b64decode
import datetime
import json
import os
import zlib

from mongo_handler import MongoHandler
import entities
from tools import file_utils
from tools import date_utils


"""
解决UnicodeEncodeError: 'ascii' codec can't encode characters in position问题
"""
import sys
reload(sys)
sys.setdefaultencoding("utf-8")


class SHistDetail():
    def __init__(self):
        self.stock_code = None
        self.date = None  # datetime type
        self.exchanges = []
        pass

    def __repr__(self):
        return "{id:%s, exchanges:%s}" % (self.stock_code, self.exchanges)

    @staticmethod
    def extract_from_dir(dir_name):
        """
        Extract history detail data from files under this directory.
        :param dir_name: the directory name.
        :return: array of SHistDetail instances.
        """
        stocks = []

        files = file_utils.getfilenames(dir_name)
        for item in files:
            stock = SHistDetail.extract(item)
            stocks.append(stock)
        return stocks

    @staticmethod
    def extract(filename):
        """
        the source file format such as:
            成交时间	成交价	价格变动	成交量(手)	成交额(元)	性质
            15:00:20	10.61	--	5250	5570419	买盘
            14:57:02	10.61	--	10	10610	卖盘
            14:56:59	10.61	0.01	107	113569	买盘

        :param filename: the name of history detail csv file
        :return: the retrieved SHistDetail instance.
        """
        # print 'extract hist detail from file', filename
        filename = os.path.abspath(filename)
        stock = SHistDetail()
        indexLastSlash = filename.rfind("/")
        indexNextLastSlash = filename.rfind("/", None, indexLastSlash)
        indexLastDot = filename.rfind(".")
        if indexLastSlash == -1 or indexNextLastSlash == -1:
            # file name format: /home/chookin/stock/market.finance.sina.com.cn/2014-11-18/sz000001.dat
            raise IOError("invalid hist detail file: %s" % filename)

        stock.stock_code = filename[indexLastSlash + 3: indexLastDot]  # remove the sz or sh
        strdate = filename[indexNextLastSlash + 1: indexLastSlash]
        stock.date = date_utils.str2date(strdate)
        f = open(filename)
        count = -1
        while True:
            line = f.readline().decode("gbk")  # 数据文件采用gbk编码
            if not line:
                break
            count += 1
            if count == 0:
                continue  # ignore the head line

            items = line.split()
            exchange = entities.Exchange()
            # exchange.time = datetime.datetime.strptime(items[0], '%H:%M:%S').time()
            exchange.time = items[0]
            exchange.price = items[1]
            exchange.trade_hand = items[3]
            exchange.trade_value = items[4]
            str_sell = items[5]
            if str_sell == '买盘':
                exchange.sell = 0
            else:
                exchange.sell = 1

            stock.exchanges.append(exchange)
        f.close()
        return stock


class MongoHistDetailHandler(MongoHandler):
    def __init__(self):
        MongoHandler.__init__(self)

    @property
    def table(self):
        """
        table schema:
        -------------------------------------------------
        _id             v
        -------------------------------------------------
        30039720140925  time:'price,trade_hand,sell',...
        -------------------------------------------------

        :return: the history detail table of mongo
        """
        return self.get_conn().stock.hist_detail

    def insert(self, hist_detail):
        self.table.insert(hist_detail)

    def query_by_date(self, stock_code, date):
        str_id = MongoHistDetailHandler.get_id(stock_code, date)
        record = self.table.find_one({"_id": str_id})
        return MongoHistDetailHandler.decode_json(record)

    def query_by_date_string(self, stock_code, str_date):
        return self.query_by_date(stock_code, date_utils.str2date(str_date))

    def _load_from_file(self, filename):
        record = SHistDetail.extract(filename)
        json_record = MongoHistDetailHandler.get_json(record)
        self.insert(json_record)
        self.table.close()
        return 1

    def _load_from_files(self, filenames):
        hist_details = []
        count = 0
        for item in filenames:
            record = SHistDetail.extract(item)
            count += 1
            json_record = MongoHistDetailHandler.get_json(record)
            hist_details.append(json_record)
            if len(hist_details) == 50:
                self.insert(hist_details)
                print '%s records inserted' % count
                hist_details = []
        if len(hist_details) > 0:
            # If the list is empty, PyMongo raises an exception:
            #   pymongo.errors.InvalidOperation: cannot do an empty bulk insert
            self.insert(hist_details)
            print 'insert the end %s records' % len(hist_details)
        self.table.close()
        return count

    def load_from_path(self, path):
        """
        Save hist details that stored in files under a path to mongo.

        :param path: name of the path, the path could be a directory or a file
        """
        if os.path.isfile(path):
            count = self._load_from_file(path)
        else:
            filenames = file_utils.getfilenames(path)
            count = self._load_from_files(filenames)
        print 'total insert %s records for %s' % (count, path)

    @staticmethod
    def get_id(stock_code, date):
        return stock_code + datetime.date.strftime(date, '%Y%m%d')

    @staticmethod
    def get_json(hist_detail, compress=True):
        """
        Get the json object that can direct insert into mongodb

        :param hist_detail: the HistDetail object
        :param compress: whether compress exchange detail
        :return: a json object, not a json string. Its format is {'_id':stock_code+date, 'v':time:'price,trade_hand,sell'}
        """
        stock = {'_id': MongoHistDetailHandler.get_id(hist_detail.stock_code, hist_detail.date)}
        exchanges = {}
        for exchange in hist_detail.exchanges:
            value = "%s,%s,%s" % (exchange.price, exchange.trade_hand, exchange.sell)
            exchanges[exchange.time] = value
        stock['v'] = exchanges
        if compress:
            MongoHistDetailHandler.compress(stock)
        return stock

    @staticmethod
    def decode_json(encoding, compress=True):
        """
        decode the json object that fetching from mongodb
        :param encoding: the source json object
        :param compress: whether the source json object is compressed
        :return: if compress=False, just return encoding; or else decompress and return.
        """
        stock = encoding
        if compress:
            MongoHistDetailHandler.decompress(stock)
        return stock

    @staticmethod
    def compress(hist_detail):
        exchanges = zlib.compress(json.dumps(hist_detail['v']), zlib.Z_BEST_COMPRESSION)
        hist_detail['v'] = b64encode(exchanges)

    @staticmethod
    def decompress(hist_detail):
        tmp = b64decode(hist_detail['v'])
        tmp = zlib.decompress(tmp)
        hist_detail['v'] = json.JSONDecoder().decode(tmp)


if __name__ == "__main__":

    pass
