#!/usr/bin/env python
#coding:utf-8

import datetime

"""
解决UnicodeEncodeError: 'ascii' codec can't encode characters in position问题
"""
import sys
reload(sys)
sys.setdefaultencoding("utf-8")

class Exchange():
    def __init__(self):
        self.time = None #成交时间
        self.price = None #成交价
        self.trade_hand = None #成交量(手)
        self.trade_value = None #成交额(元)
        self.sell = None #性质，是否卖盘

    def __repr__(self):
        """
        implement __repr__, or else can not clearly print list of exchange
        """
        return "{time:%s, price:%s, trade_hand:%s, trade_value:%s, sell:%s}" % (self.time, self.price, self.trade_hand, self.trade_value, self.sell)


class HistDetail():
    def __init__(self):
        self.stock_code = None
        self.date = None
        self.exchanges = []
        pass

    def __repr__(self):
        return "{id:%s, exchanges:%s}" % (self.stock_code, self.exchanges)

    @staticmethod
    def extract_from_dir(dirname):
        stocks = []
        import file_utils
        files = file_utils.FileUtils.getfilenames(dirname)
        for file in files:
            stock = HistDetail.extract(file)
            stocks.append(stock)
        return stocks

    @staticmethod
    def extract(filename):
        stock = HistDetail()
        indexLastSlash = filename.rfind("/")
        indexNextLastSlash = filename.rfind("/", None, indexLastSlash)
        indexLastDot = filename.rfind(".")
        if indexLastSlash == -1 or indexLastSlash == -1 or indexNextLastSlash == -1:
            raise IOError("invalid hist detail file: %s" % filename)

        stock.stock_code = filename[indexLastSlash+3: indexLastDot] # remove the sz or sh
        strdate =filename[indexNextLastSlash+1: indexLastSlash]
        stock.date = datetime.datetime.strptime(strdate, '%Y-%m-%d').date()
        f = open(filename)
        count = -1
        while True:
            line = f.readline().decode("gbk")# 数据文件采用gbk编码
            if not line:
                break
            count += 1
            if count == 0:
                continue  # ignore the head line

            items = line.split()
            exchange = Exchange()
            #exchange.time = datetime.datetime.strptime(items[0], '%H:%M:%S').time()
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


if __name__ == '__main__':
    filename = '/home/chookin/stock/market.finance.sina.com.cn/2014-09-25/sz300397.dat'
    print HistDetail.extract(filename)

