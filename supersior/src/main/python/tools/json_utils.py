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

    def to_csv(self, filename, fields=None):
        Table.generate_table(self.data, fields).write_csv(filename)
        return self


if __name__ == '__main__':
    fields = ["tag", "domain", "brands"]
    JsonUtils().load("/tmp/tag-brands.json").to_csv("/tmp/tag-brands.csv", fields)