#!/usr/bin/env python
# coding:utf-8
from collections import OrderedDict

"""
A very easy way around all the "'ascii' codec can't encode character…" issues with csvwriter is to instead use unicodecsv, a drop-in replacement for csvwriter.
"""
import unicodecsv as csv
import operator


class Table():
    def __init__(self, fields=None, rows=None):
        self.fields = fields
        self.rows = rows

    def __str__(self):
        if self.fields is None:
            return ''

        strtb = ','.join(self.fields)
        return strtb

    @staticmethod
    def generate_table_fields(obj, path=None):
        """

        :param obj:
        :param path:
        :return:
        """
        fields = []
        if isinstance(obj, dict):
            for key, value in obj.items():
                if path is None:
                    my_path = key
                else:
                    my_path = ".".join((path, key))
                if isinstance(value, dict):
                    sub_fields = Table.generate_table_fields(value, my_path)
                    fields += sub_fields
                else:
                    fields.append(my_path)
        if isinstance(obj, list) or isinstance(obj, tuple):
            for item in obj:
                sub_fields = Table.generate_table_fields(item)
                fields += sub_fields
                break  # only use the first item's field if list
        return fields

    @staticmethod
    def generate_table(obj, fields=None):
        if fields is None:
            fields = Table.generate_table_fields(obj)
        if len(fields) is 0:
            return None

        key_map = OrderedDict()
        for item in fields:
            splits = item.split('.')
            splits = [int(s) if s.isdigit() else s for s in splits]
            key_map[item] = splits

        rows = []
        for item in obj:
            row = {}
            for header, keys in key_map.items():
                try:
                    """
                    reduce(function, sequence, starting_value)：对sequence中的item顺序迭代调用function，如果有starting_value，还可以作为初始值调用，例如可以用来对List求和：
                            >>> def add(x,y): return x + y
                            >>> reduce(add, range(1, 11))
                            55 （注：1+2+3+4+5+6+7+8+9+10）
                            >>> reduce(add, range(1, 11), 20)
                            75 （注：1+2+3+4+5+6+7+8+9+10+20）
                    """
                    # item[key0][key1][key2][...]
                    row[header] = reduce(operator.getitem, keys, item)
                except (KeyError, TypeError):
                    raise IOError("failed to process row %s" % row)
            rows.append(row)
        table = Table(key_map.keys(), rows)
        return table

    def write_csv(self, filename='output.csv', mode='wb'):
        """
        Write the table data to the given file

        :param filename:
        :param mode:
        :raise AttributeError:
        """
        if len(self.rows) <= 0:
            raise AttributeError('No rows were loaded')
        with open(filename, mode) as f:
            # With Python's csv module, you can write a UTF-8 file that Excel will read correctly if you place a BOM at the beginning of the file.
            f.write(u'\ufeff'.encode('utf8'))

            writer = csv.DictWriter(f, fieldnames=self.fields, delimiter=',', quoting=csv.QUOTE_ALL)
            writer.writeheader()  # only valid for python version 2.7+
            writer.writerows(self.rows)
