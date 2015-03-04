#!/usr/bin/env python
# coding: utf-8
import json
from tools.z_table import Table


class JsonUtils():
    def __init__(self):
        self.data = None
        pass

    def load(self, filename):
        file = open(filename).read()
        self.data = json.loads(file)
        return self

    def to_csv(self, filename):
        Table.generate_table(self.data).write_csv(filename)
        return self


if __name__ == '__main__':
    JsonUtils().load("/tmp/brand.json").to_csv("/tmp/brand.csv")