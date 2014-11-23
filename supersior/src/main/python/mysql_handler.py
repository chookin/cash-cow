#!/usr/bin/env python
# coding:utf-8
import optparse
import traceback
import MySQLdb
import sys
import params
from tools.mysql_helper import MysqlHelper


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


# Set database properties to default values
def load_default_db_properties(args):
    args.dbms = "mysql"
    args.database_host = "localhost"
    args.database_port = "3306"
    args.database_username = "root"
    args.database_password = "root"
    pass


class MysqlDBManager():
    def __init__(self):
        self.parser = optparse.OptionParser(usage="",)

    def init_parser(self):
        parser = self.parser
        parser.add_option('--dbms',
                          default='mysql',
                          help="Database to use mysql|postgres|oracle",
                          dest="dbms")
        parser.add_option('--host',
                          default='localhost',
                          help="Hostname of database server",
                          dest="database_host")
        parser.add_option('--port',
                          default='3306',
                          help="Database port",
                          dest="database_port")
        parser.add_option('--dbname',
                          default=None,
                          help="Database/Schema/Service name or ServiceID",
                          dest="database_name")
        parser.add_option('--user',
                          default='root',
                          help="Database user login",
                          dest="database_username")
        parser.add_option('--passwd',
                          default='root',
                          help="Database user password",
                          dest="database_password")

    def create_db(self, args):
        load_default_db_properties(args)
        MysqlHelper.execute_sql_script(args, params.mysql_db_create_script)

    def action(self):
        self.init_parser()
        (options, args) = self.parser.parse_args()
        action = sys.argv[1]
        print 'mysql handler executes action`%s`' % action
        if action == 'create_db':
            self.create_db(options)
if __name__ == '__main__':
    MysqlDBManager().action()

