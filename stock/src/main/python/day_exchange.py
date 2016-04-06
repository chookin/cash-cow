#!/usr/bin/env python
# coding:utf-8
import os

from mongo_handler import DayDetailDAO
from tools import date_utils
from tools import file_utils


class Exchange():
    """
    交易记录
    """

    def __init__(self):
        self.time = None  # 成交时间
        self.price = None  # 成交价
        self.trade_hand = None  # 成交量(手)
        self.trade_value = None  # 成交额(元)
        self.sell = None  # 性质，是否卖盘

    def __repr__(self):
        """
        implement __repr__, or else can not clearly print list of exchange
        """
        return "{time:%s, price:%s, trade_hand:%s, trade_value:%s, sell:%s}" % (
            self.time, self.price, self.trade_hand, self.trade_value, self.sell)


class DayDetail():
    """
    Day exchange details.
    """
    def __init__(self):
        self.stock_code = None
        self.date = None  # datetime type
        self.exchanges = []
        pass

    def __repr__(self):
        return "{id:%s, exchanges:%s}" % (self.stock_code, self.exchanges)


class SinaDayDetail():
    def __init__(self):
        pass

    def _load_from_file(self, filename):
        record = self.extract(filename)
        DayDetailDAO.get_instance().save(record)
        return 1


    def _load_from_files(self, filenames):
        hist_details = []
        count = 0
        for item in filenames:
            record = SDayExchangeDetail.extract(item)
            count += 1
            json_record = DayDetailDAO.get_json(record)
            hist_details.append(json_record)
            if len(hist_details) == 50:
                self._insert(hist_details)
                print '%s records inserted' % count
                hist_details = []
        if len(hist_details) > 0:
            # If the list is empty, PyMongo raises an exception:
            #   pymongo.errors.InvalidOperation: cannot do an empty bulk insert
            self._insert(hist_details)
            print 'insert the end %s records' % len(hist_details)
        self.table.close()
        return count


    def extract_from_dir(self, dir_name):
        """
        Extract history detail data from files under this directory.
        :param dir_name: the directory name.
        :return: array of SHistDetail instances.
        """
        stocks = []

        files = file_utils.get_file_names(dir_name)
        for item in files:
            stock = self.extract(item)
            stocks.append(stock)
        return stocks

    def extract(self, filename):
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
        stock = DayDetail()
        index_last_slash = filename.rfind("/")
        index_next_last_slash = filename.rfind("/", None, index_last_slash)
        index_last_dot = filename.rfind(".")
        if index_last_slash == -1 or index_next_last_slash == -1:
            sample_file_name = '/home/chookin/stock/market.finance.sina.com.cn/2014-11-18/sz000001.dat'
            raise IOError("invalid hist detail file: %s, and file name is like %s" % (filename, sample_file_name))

        stock.stock_code = filename[index_last_slash + 3: index_last_dot]  # skip the sz or sh
        str_date = filename[index_next_last_slash + 1: index_last_slash]
        stock.date = date_utils.str2date(str_date)
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
            exchange = Exchange()
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