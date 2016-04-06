#!/usr/bin/env python
# coding:utf-8

import unittest

if __name__ == "__main__":
    # http://stackoverflow.com/questions/644821/python-how-to-run-unittest-main-for-all-source-files-in-a-subdirectory
    # I believe this is the simplest solution for writing several test cases in one directory. The solution requires Python 2.7 or Python 3.
    testsuite = unittest.TestLoader().discover('test')
    unittest.TextTestRunner(verbosity=1).run(testsuite)