#!/usr/bin/env python
#coding:utf-8
import MySQLdb


class MySQLHandler():
    def __init__(self, host='localhost', user='root', passwd='root', db='stock', port=3306, charset='utf8'):
        self.host = host
        self.user = user
        self.passwd = passwd
        self.db = db
        self.port = port
        self.charset = charset
        self.conn = None
        pass

    def getconn(self):
        if self.conn is None:
            self.conn = MySQLdb.connect(host=self.host,
                                        user=self.user, passwd=self.passwd,
                                        db=self.db, port=self.port,
                                        charset=self.charset)
        return self.conn

    def close(self):
        if self.conn is None:
            return
        self.conn.close()


if __name__ == '__main__':
    try:
        conn = MySQLHandler("localhost", "root", "root", "stock", 3306).getconn()
        cursor = conn.cursor()
        cursor.execute("SELECT count(*) AS count FROM stock")
        for row in cursor.fetchall():
            print row[0]
        conn.close()
    except Exception as e:
        print e
        pass



