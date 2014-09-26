#!/usr/bin/env python
#coding:utf-8

"""
解决UnicodeEncodeError: 'ascii' codec can't encode characters in position问题
"""
import sys
reload(sys)
sys.setdefaultencoding("utf-8")

class Exchange():
    def __init__(self):
        self.time = None
        self.price = None
        self.trade_hand = None
        self.trade_value = None
        self.is_sell = None

    def __repr__(self):
        """
        implement __repr__, or else can not clearly print list of exchange
        """
        return "{time:%s, price:%s, trade_hand:%s, trade_value:%s, sell:%s}" % (self.time, self.price, self.trade_hand, self.trade_value, self.is_sell)


class HistDetail():
    def __init__(self):
        self.stock_code = None
        self.date = None
        self.exchanges = []
        pass

    def __repr__(self):
        return "{id:%s, exchanges:%s}" % (self.stock_code, self.exchanges)

    @staticmethod
    def extract(filename):
        stock = HistDetail()
        indexLastSlash = filename.rfind("/")
        indexNextLastSlash = filename.rfind("/", end=indexLastSlash)
        indexLastDot = filename.rfind(".")
        if indexLastSlash == -1 or indexLastSlash == -1 or indexNextLastSlash == -1:
            raise IOError("invalid hist detail file: %s" % filename)

        stock.stock_code = filename[indexLastSlash+1: indexLastDot]
        stock.date =filename[indexNextLastSlash+1: indexLastSlash]
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
            exchange.time = items[0] #成交时间
            exchange.price = items[1] #成交价
            exchange.trade_hand = items[3] #成交量(手)
            exchange.trade_value = items[4] #成交额(元)
            exchange.is_sell = items[5] #性质，是否卖盘
            stock.exchanges.append(exchange)
        f.close()
        return stock

    @staticmethod
    def extract_from_dir(dirname):
        stocks = []
        import file_utils
        files = file_utils.FileUtils.getfilenames(dirname)
        for file in files:
            stock = HistDetail.extract(file)
            stocks.append(stock)
        return stocks

if __name__ == '__main__':
    filename = '/home/chookin/stock/market.finance.sina.com.cn/2014-09-25/sz300397.dat'
    print HistDetail.extract(filename)

