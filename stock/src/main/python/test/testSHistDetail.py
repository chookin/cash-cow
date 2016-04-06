#!/usr/bin/env python
# coding:utf-8
import datetime
import unittest
from mongo_handler import SDayExchangeDetail

import params
from src.main.python.tools import debug_utils


class TestSHistDetail(unittest.TestCase):
    # 初始化工作
    def setUp(self):
        pass

    # 退出清理工作
    def tearDown(self):
        pass

    # 具体的测试用例，一定要以test开头
    def test_hist_extract(self):
        filename = params.s_hist_data_file
        stock = SDayExchangeDetail.extract(filename)
        self.assertEqual(stock.date, datetime.datetime.strptime('2014-09-25', '%Y-%m-%d').date())
        self.assertEqual(stock.stock_code, '300397')
        last_exchange = stock.exchanges[len(stock.exchanges)-1]
        self.assertEqual(last_exchange.time, '09:30:03')
        self.assertEqual(last_exchange.price, '98.81')
        self.assertEqual(last_exchange.trade_hand, '228')
        print debug_utils.get_current_function_name(), 'success.'

if __name__ == '__main__':
    unittest.main()