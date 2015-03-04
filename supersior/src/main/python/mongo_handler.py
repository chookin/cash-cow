#!/usr/bin/env python
# coding:utf-8
from base64 import b64encode, b64decode
import datetime
import json
import os
import zlib

from tools.mongo_helper import MongoHandler
import params
from tools import file_utils
from tools import date_utils
from tools import z_common


"""
解决UnicodeEncodeError: 'ascii' codec can't encode characters in position问题
"""
import sys
reload(sys)
sys.setdefaultencoding("utf-8")


class DayDetailDAO(MongoHandler):

    @staticmethod
    def get_instance():
        return DayDetailDAO()

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

    def _insert(self, hist_detail):
        self.table.insert(hist_detail)

    def save(self, stock):
        json_record = DayDetailDAO.get_json(stock)
        self._insert(json_record)
        self.table.close()

    def save_all(self, stocks):
        if len(stocks) <= 0:
            return
        records = []
        for stock in stocks:
            records.append(DayDetailDAO.get_json(stock))
        # If the list is empty, PyMongo raises an exception:
        #   pymongo.errors.InvalidOperation: cannot do an empty bulk insert
        self._insert(records)
        self.table.close()

    def query_by_date(self, stock_code, date):
        str_id = DayDetailDAO.get_id(stock_code, date)
        record = self.table.find_one({"_id": str_id})
        return DayDetailDAO.decode_json(record)

    def query_by_date_string(self, stock_code, str_date):
        return self.query_by_date(stock_code, date_utils.str2date(str_date))



    def load_from_path(self, path):
        """
        Save hist details that stored in files under a path to mongo.

        :param path: name of the path, the path could be a directory or a file
        """
        if os.path.isfile(path):
            count = self._load_from_file(path)
        else:
            filenames = file_utils.get_file_names(path, recursive=True)
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
        stock = {'_id': DayDetailDAO.get_id(hist_detail.stock_code, hist_detail.date)}
        exchanges = {}
        for exchange in hist_detail.exchanges:
            value = "%s,%s,%s" % (exchange.price, exchange.trade_hand, exchange.sell)
            exchanges[exchange.time] = value
        stock['v'] = exchanges
        if compress:
            DayDetailDAO.compress(stock)
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
            DayDetailDAO.decompress(stock)
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


def action_load_remove():
    DayDetailDAO().load_from_path(params.s_hist_data_path)
    z_common.execute_command('rm -rf %s/*' % params.s_hist_data_path)

if __name__ == "__main__":
    # print "脚本名：", sys.argv[0]
    if len(sys.argv) > 1:
        if sys.argv[1] == 'load_remove':
            action_load_remove()
    pass
