#!/usr/bin/env python
# coding: utf-8
import getpass

username = getpass.getuser()
s_hist_data_path = '/home/%s/stock/market.finance.sina.com.cn' % username

mysql_db_create_script = '/home/%s/project/myworks/cash-cow/supersior/src/main/resources/stock-ddl-mysql-create.sql' % username