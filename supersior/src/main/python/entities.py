#!/usr/bin/env python
# coding:utf-8


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


