#!/usr/bin/env python
# coding:utf-8
import datetime
import decimal
import json

import mysql_handler
from tools import file_utils, z_table

hmysql = mysql_handler.MySQLHandler()


class DateTimeEncoder(json.JSONEncoder):
    """
    TypeError: datetime.date(2014, 6, 6) is not JSON serializable
    """

    def default(self, obj):
        if hasattr(obj, 'isoformat'):
            return obj.isoformat()
        elif isinstance(obj, decimal.Decimal):
            return float(obj)
        else:
            return json.JSONEncoder.default(self, obj)


def get_query_string_set(obj):
    """
    Get the query set string for obj.

    :param obj: could be basestring, int, list or tuple
    :return: If obj is basestring, then just return ('obj').
    """
    if isinstance(obj, basestring) or isinstance(obj, int):
        str_set = "('%s')" % obj
    elif isinstance(obj, list) or isinstance(obj, tuple):
        str_set = "','".join(obj)
        str_set = "('%s')" % str_set
    else:
        str_set = "()"
    return str_set


def query_records(sql, fields, key_field=None):
    """

    :param sql: the query sql
    :param fields: the query filed, use which to build the queried result key value format data.
    :param key_field: use which to build the result dist.
    :return: if key_filed is None, then return records as array, else as dist of [key_filed: record]
    :raise IOError: if key_filed is not None, and not in the queried record.
    """
    print sql
    cursor = hmysql.getconn().cursor()
    cursor.execute(sql)
    if key_field:
        records = {}
        for row in cursor.fetchall():
            rec = dict(zip(fields, row))  # map two lists into a dist
            if key_field in rec:
                key = rec[key_field]
                records[key] = rec
            else:
                raise IOError("%s not contains %s" % (rec, key_field))
    else:
        records = []
        for row in cursor.fetchall():
            rec = dict(zip(fields, row))  # map two lists into a dist
            records.append(rec)
    cursor.close()
    return records


def get_stock_codes():
    """
    Get all the stock_code from db
    """
    sql = 'SELECT stock_code FROM stock'
    cursor = hmysql.getconn().cursor()
    cursor.execute(sql)
    stock_codes = []
    for row in cursor.fetchall():
        stock_codes.append(row[0])
    cursor.close()
    return stock_codes


def get_stock_info(stock_codes):
    stock_codes = get_query_string_set(stock_codes)
    fields = ('stock_code', 'exchange_center', 'company_name', 'stock_num',
              'tradable', 'eps', 'net_asset', 'cash_flow',
              'profit', 'equity', 'growth', 'gross', 'tags', 'company_profile')
    sql = "select %s from company_info " \
          "where stock_code in %s " \
          "order by stock_code" \
          % (",".join(fields), stock_codes)
    return query_records(sql, fields, 'stock_code')


def get_stock_ids(stock_codes):
    """
    Get stock_id for these stock_codes

    :param stock_codes:
    :return: dict of {stock_code:stock_id}
    """
    str_codes = get_query_string_set(stock_codes)
    sql = "select s.stock_code, s.stock_id from stock as s " \
          "where s.stock_code in %s " \
          "order by s.stock_code" \
          % str_codes

    cursor = hmysql.getconn().cursor()
    cursor.execute(sql)
    stock_code_map = {}
    for row in cursor.fetchall():
        stock_code_map[row[0]] = row[1]
    cursor.close()
    return stock_code_map


def get_hist_data(stock_id, dateobj):
    """
    Get history data by stock_id from database
    :param stock_id: stock_id, not stock_code
    :param dateobj: it's type could be: datetime.date, datetime.datetime, string of date, list of dates
    """
    if isinstance(dateobj, datetime.date):
        dateobj = datetime.date.strftime(dateobj, '%Y-%m-%d')
    elif isinstance(dateobj, datetime.datetime):
        dateobj = datetime.datetime.strftime(dateobj, '%Y-%m-%d')
    dateobj = get_query_string_set(dateobj)

    fields = ('stock_id', 'time', 'open_price', 'close_price',
              'high_price', 'low_price',
              'trade_hand', 'trade_value')
    sql = "select %s " \
          "from history_data as h " \
          "where h.stock_id = '%s' " \
          "and h.time in %s " \
          "order by h.time" \
          % (','.join(fields), stock_id, dateobj)

    return query_records(sql, fields)


def get_stock_incr(stock_id, start_date, end_date):
    """
    Get stock price increment by history data

    :param stock_id:
    :param start_date:
    :param end_date:
    :return: dict of [stock_id, incr, incr_ratio, old_deal, new_deal]
    """
    date = [start_date, end_date]
    stock_deals = get_hist_data(stock_id, date)
    record = None
    if len(stock_deals) is 2:
        incr = stock_deals[1]['close_price'] - stock_deals[0]['close_price']
        incr_ratio = incr / stock_deals[0]['close_price'] * 100
        for item in stock_deals:
            del item['stock_id']  # add stock_id field in the root level, so delete this one.

        record = dict(stock_id=stock_id, incr=incr, incr_ratio=incr_ratio, old_deal=stock_deals[0], new_deal=stock_deals[1])
    return record


def get_stocks_incr(stock_ids, start_date, end_date):
    stocks_deal = []
    if isinstance(stock_ids, basestring):
        stock_ids = [stock_ids]
    for item in stock_ids:
        record = get_stock_incr(item, start_date, end_date)
        if record:
            stocks_deal.append(record)
    return stocks_deal


def cmp_stock_field(field1, field2):
    """
    Ensure stock_code is first.

    :param field1:
    :param field2:
    :return: negative if filed1 less than filed2.
    """
    if field1 == 'stock_code':
        return -1
    if field2 == 'stock_code':
        return 1
    return cmp(field1, field2)


def export_stocks_incr(stock_codes=None, start_date=datetime.datetime.now().date() + datetime.timedelta(days=-1), end_date=datetime.datetime.now(), file_format='json'):
    """
    If stock_codes is None, then export all the stocks' increment.

    :param stock_codes:
    :param start_date:
    :param end_date:
    :param file_format:
    """
    if stock_codes is None:
        stock_codes = get_stock_codes()
    stock_code_map = get_stock_ids(stock_codes)

    stocks_info = get_stock_info(stock_codes)
    stocks_deal = []
    for stock_code, stock_id in stock_code_map.items():
        record = get_stock_incr(stock_id, start_date, end_date)
        if record:
            stock_info = stocks_info[stock_code]
            record.update(stock_info)
            stocks_deal.append(record)

    filename = '/home/chookin/stock/stat.%s' % file_format
    if file_format is 'json':
        # json.dumps在默认情况下，对于非ascii字符生成的是相对应的字符编码，而非原始字符. so, add ensure_ascii=False
        strdata = json.dumps(stocks_deal, ensure_ascii=False, cls=DateTimeEncoder)
        file_utils.save_to_unicode(filename, strdata)
    elif file_format is 'csv':
        fields = z_table.Table.generate_table_fields(stocks_deal)
        fields = sorted(fields, cmp=cmp_stock_field)
        table = z_table.Table.generate_table(stocks_deal, fields)
        table.write_csv(filename)
        pass


if __name__ == "__main__":
    print
    stock_code = ['601989', '601991', '601992']
    stock_code = '601991'
    # compute_stocks_incr(stock_code, '2014-06-6', '2014-09-19')
    export_stocks_incr(('601989', '601991'), '2014-06-6', '2014-09-19', file_format='csv')
    # export_stocks_incr('2014-06-6', '2014-09-19', file_format='csv')
