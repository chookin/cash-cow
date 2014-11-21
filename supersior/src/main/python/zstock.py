#!/usr/bin/env python
#coding:utf-8
import optparse
import sys
import traceback

import z_logging
from tools.z_common_ex import *





# action commands
SETUP_ACTION = "setup"
START_ACTION = "start"
STOP_ACTION = "stop"


def init_parser(parser):
    parser.add_option('--database',
                      default=None,
                      help="Database to use postgres|oracle",
                      dest="dbms")
    parser.add_option('--databasehost',
                      default=None,
                      help="Hostname of database server",
                      dest="database_host")
    parser.add_option('--databaseport',
                      default=None,
                      help="Database port",
                      dest="database_port")
    parser.add_option('--databasename',
                      default=None,
                      help="Database/Schema/Service name or ServiceID",
                      dest="database_name")
    parser.add_option('--databaseusername',
                      default=None,
                      help="Database user login",
                      dest="database_username")
    parser.add_option('--databasepassword',
                      default=None,
                      help="Database user password",
                      dest="database_password")


# Set database properties to default values
def load_default_db_properties(args):
    args.dbms = "mysql"
    args.database_host = "localhost"
    args.database_port = "3306"
    args.database_username = "root"
    args.database_password = "root"
    pass

def setup(args):
    from tools.mysql_helper import MysqlHelper
    load_default_db_properties(args)
    MysqlHelper.execute_script(args, "../resources/stock-ddl-mysql-create.sql")

    pass


def start(args):
    pass


def stop(args):
    pass


def main():
    print 'OS VERSION:', OS, OS_VERSION

    z_logging.ZLogging().init()

    parser = optparse.OptionParser(
        usage="usage: python %prog [options] action",)
    init_parser(parser)
    (options, args) = parser.parse_args()
    if len(args) == 0:
        print parser.print_help()
        parser.error("No action entered")
        sys.exit(-1)

    action = args[0]
    options.exit_message = "ZStock Server '%s' completed successfully." % action
    try:
        if action == SETUP_ACTION:
            setup(options)
        elif action == START_ACTION:
            start(options)
        elif action == STOP_ACTION:
            stop(options)
    except:
        print traceback.format_exc()
        sys.exit(-1)

    print options.exit_message


if __name__ == '__main__':
    main()