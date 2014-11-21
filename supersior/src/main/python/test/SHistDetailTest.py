#!/usr/bin/env python
# coding:utf-8
import unittest
import datetime
from hist_detail import SHistDetail
import para


class SHistDetailTest(unittest.TestCase):
    # 初始化工作
    def setUp(self):
        pass

    # 退出清理工作
    def tearDown(self):
        pass

    # 具体的测试用例，一定要以test开头
    def test_hist_extract(self):
        filename = para.s_hist_data_file
        stock = SHistDetail.extract(filename)
        self.assertEqual(stock.date, datetime.datetime.strptime('2014-09-25', '%Y-%m-%d').date())
        self.assertEqual(stock.stock_code, '300397')
        last_exchange = stock.exchanges[len(stock.exchanges)-1]
        self.assertEqual(last_exchange.time, '09:30:03')
        self.assertEqual(last_exchange.price, '98.81')
        self.assertEqual(last_exchange.trade_hand, '228')


if __name__ == '__main__':
    unittest.main()