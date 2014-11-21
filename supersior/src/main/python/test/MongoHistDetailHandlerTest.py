#!/usr/bin/env python
# coding:utf-8
import json
from hist_detail import MongoHistDetailHandler, SHistDetail


def test_insert_histdetail():
    handler = MongoHistDetailHandler()
    test_filename = '/home/chookin/stock/market.finance.sina.com.cn/2014-09-25/sz300397.dat'
    histdetail = SHistDetail.extract(test_filename)
    histdetail = MongoHistDetailHandler().get_json(histdetail)
    handler.insert(histdetail)


def test_query_histdetail():
    handler = MongoHistDetailHandler()
    print handler.query_by_date_string("300397", "2014-09-25")


def test_compress():
    """
    zlib多用于网络收发字符串的压缩与解压
    测试结果：len(raw_data)=13779, len(zb_data)=2485, compression ratio=0.18
    """
    test_filename = '/home/chookin/stock/market.finance.sina.com.cn/2014-09-25/sz300397.dat'
    hist_detail = SHistDetail.extract(test_filename)
    raw_data = MongoHistDetailHandler.get_json(hist_detail, False)
    zb_data = MongoHistDetailHandler.get_json(hist_detail)
    raw_data = json.dumps(raw_data)
    zb_data = json.dumps(zb_data)

    print "len(raw_data)=%d, len(zb_data)=%d, compression ratio=%.2f" \
          % (len(raw_data), len(zb_data), float(len(zb_data)) / len(raw_data))

    stock1 = MongoHistDetailHandler.decode_json(raw_data, False)
    stock2 = MongoHistDetailHandler.decode_json(zb_data)
    print stock1 == stock2
    print stock2

if __name__ == "__main__":
    # test_compress()
    # test_insert_histdetail()
    # test_query_histdetail()
    # MongoHistDetailHandler.save_hist_detail('/home/chookin/stock/market.finance.sina.com.cn/2014-09-24')
    # MongoHistDetailHandler.save_hist_detail('/home/chookin/stock/market.finance.sina.com.cn/2014-09-25/sh600157.dat')
    pass


