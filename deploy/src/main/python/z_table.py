#!/usr/bin/env python
#coding:utf-8
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

    @staticmethod
    def generate_table_fields(obj, path=None):
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
                    row[header] = reduce(operator.getitem, keys, item)
                except (KeyError, TypeError):
                    raise IOError("failed to process row %s" % row)
            rows.append(row)
        table = Table(key_map.keys(), rows)
        return table

    def write_csv(self, filename='output.csv', mode='wb'):
        """Write the processed rows to the given filename
        """
        if len(self.rows) <= 0:
            raise AttributeError('No rows were loaded')
        with open(filename, mode) as f:
            # With Python's csv module, you can write a UTF-8 file that Excel will read correctly if you place a BOM at the beginning of the file.
            f.write(u'\ufeff'.encode('utf8'))
            writer = csv.DictWriter(f, fieldnames=self.fields, delimiter=',', quoting=csv.QUOTE_ALL)
            writer.writeheader()  # only valid for python version 2.7+
            writer.writerows(self.rows)
