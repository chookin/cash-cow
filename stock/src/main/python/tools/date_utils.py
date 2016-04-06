#!/usr/bin/env python
# coding:utf-8
import datetime


def str2date(str_date, str_format='%Y-%m-%d'):
    return datetime.datetime.strptime(str_date, str_format).date()
