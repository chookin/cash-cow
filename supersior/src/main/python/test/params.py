#!/usr/bin/env python
# coding:utf-8
import getpass


username = getpass.getuser()
resource_base_dir = '/home/%s/project/myworks/cash-cow/supersior/src/main/python/test/resources' % username
s_hist_data_file = '%s/market.finance.sina.com.cn/2014-09-25/sz300397.dat' % resource_base_dir
