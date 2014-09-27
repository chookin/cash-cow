#!/usr/bin/env python
#coding:utf-8

import os

"""
os.walk(top,topdown=True,onerror=None)
函数声明：
1>参数top表示需要遍历的目录树的路径
2>参数topdown的默认值是"True",表示首先返回目录树下的文件，然后在遍历目录树的子目录.Topdown的值为"False"时，则表示先遍历目录树的子目录，返回子目录下的文件，最后返回根目录下的文件
3>参数onerror的默认值是"None",表示忽略文件遍历时产生的错误.如果不为空，则提供一个自定义函数提示错误信息后继续遍历或抛出异常中止遍历
4>该函数返回一个元组，该元组有3个元素，这3个元素分别表示每次遍历的路径名，目录列表和文件列表
"""
def getsubdirs(dirname):
    for parent, subdirnames, filenames in os.walk(dirname):
        return subdirnames
    return []


def getfilenames(dirname):
    rst = []
    for parent, subdirnames, filenames in os.walk(dirname):
        for item in filenames:
            rst.append(os.path.join(parent, item))
        rst.sort()
        return rst
    return rst


def save_to_unicode(filename, data, append=False, encoding='utf-8'):
    import codecs  # Python核心库的open函数是按照ascii设计的,读取unicode文件可采用codecs
    if append:
        mode = 'w+'
    else:
        mode = 'w'
    print 'save file ', filename
    output = codecs.open(filename, mode, encoding)
    output.write(data)
    output.close()


def save_json_to_csv(filename, obj, mode='w+'):
    import csv
    f = csv.writer(open(filename, mode))
    # ...
    f.close()







if __name__ == '__main__':
    filenames = getfilenames("/home/chookin")
    print filenames