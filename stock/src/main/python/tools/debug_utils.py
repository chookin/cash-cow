#!/usr/bin/env python
# coding:utf-8
import inspect


def get_current_function_name():
    """
    使用inspect模块动态获取当前运行的函数名

    :return: current function name
    """
    return inspect.stack()[1][3]