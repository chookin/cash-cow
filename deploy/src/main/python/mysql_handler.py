#!/usr/bin/env python
#coding:utf-8
import MySQLdb

class MySQLHandler():
    def __init__(self, host, user, passwd, db, port):
        self.host = host
        self.user = user
        self.passwd = passwd
        self.db = db
        self.port = port
        pass

    def getconn(self):
        return MySQLdb.connect(host=self.host, user=self.user, passwd = self.passwd, db=self.db, port=self.port)


if __name__ == '__main__':
    try:
        conn = MySQLHandler("localhost", "root", "root", "stock", 3306).getconn()
        cursor = conn.cursor()
        cursor.execute("select count(*) as count from stock")
        for row in cursor.fetchall():
            print row[0]
        conn.close()
    except Exception as e:
        print e
        pass



