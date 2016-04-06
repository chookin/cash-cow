#!/usr/bin/env python
# coding:utf-8
import unittest
from mysql_handler import MySQLHandler


class TestMysqlHandler(unittest.TestCase):
    def test_table_stock(self):
        try:
            conn = MySQLHandler("localhost", "root", "root", "stock", 3306).getconn()
            cursor = conn.cursor()
            cursor.execute("SELECT count(*) AS count FROM stock")
            row_count = -1
            for row in cursor.fetchall():
                row_count = row[0]
            conn.close()
            self.assertTrue(row_count > -1)
        except Exception as e:
            print e
            self.assertEqual(1, 0)


if __name__ == '__main__':
    unittest.main()