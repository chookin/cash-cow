#!/usr/bin/env bash
#coding:utf-8
import z_common

MYSQL_EXEC_ARGS_WO_USER_VARS = \
    "--force --host={0} --port={1} --user={2} --password={3} < {5}"

class MysqlHelper():
    def __init__(self):
        pass

    @staticmethod
    def execute_script(args, script):
        cmd = '{0} {1}'.format(args.dbms, MYSQL_EXEC_ARGS_WO_USER_VARS.format(
            args.database_host,
            args.database_port,
            args.database_username,
            args.database_password,
            args.database_name,
            script))

        z_common.execute_command(cmd)
        pass

