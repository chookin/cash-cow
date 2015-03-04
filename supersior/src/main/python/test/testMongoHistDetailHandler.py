#!/usr/bin/env python
# coding:utf-8
import json
import unittest
from mongo_handler import DayDetailDAO, SDayExchangeDetail
import params
from tools import debug_utils


class TestMongoHistDetailHandler(unittest.TestCase):
    def setUp(self):
        self.handler = DayDetailDAO()

    def test_insert_histdetail(self):
        histdetail = SDayExchangeDetail.extract(params.s_hist_data_file)
        str_id = self.handler.get_id(histdetail.stock_code, histdetail.date)
        json_histdetail = DayDetailDAO.get_json(histdetail)
        self.handler.table.remove({'_id': str_id})
        self.handler._insert(json_histdetail)
        ret = self.handler.query_by_date(histdetail.stock_code, histdetail.date)
        DayDetailDAO.decompress(json_histdetail)
        self.assertEqual(json_histdetail == ret, True)
        print debug_utils.get_current_function_name(), 'success.'

    def test_compress(self):
        """
        zlib多用于网络收发字符串的压缩与解压
        测试结果：len(raw_data)=13779, len(zb_data)=2485, compression ratio=0.18
        """
        hist_detail = SDayExchangeDetail.extract(params.s_hist_data_file)
        raw_data = DayDetailDAO.get_json(hist_detail, False)
        zb_data = DayDetailDAO.get_json(hist_detail)
        str_raw_data = json.dumps(raw_data)
        str_zb_data = json.dumps(zb_data)

        print "len(raw_data)=%d, len(zb_data)=%d, compression ratio=%.2f" \
              % (len(raw_data), len(zb_data), float(len(str_zb_data)) / len(str_raw_data))

        stock1 = DayDetailDAO.decode_json(raw_data, False)
        stock2 = DayDetailDAO.decode_json(zb_data)
        self.assertEqual(stock1, stock2)
        print debug_utils.get_current_function_name(), 'success.'

if __name__ == "__main__":
    unittest.main()
    # MongoHistDetailHandler.load_from_path('/home/chookin/stock/market.finance.sina.com.cn/2014-09-24')
    # MongoHistDetailHandler.load_from_path('/home/chookin/stock/market.finance.sina.com.cn/2014-09-25/sh600157.dat')
    pass


