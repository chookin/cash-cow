#!/usr/bin/env python
# coding:utf-8
import urllib


def decoder(encoded_url):
    """
    被编码后的url地址，解码出原始url地址

    :param encoded_url:
    :return:
    """
    return urllib.unquote(encoded_url)


def encode_plus(url, use_plus=False):
    """
    将空格编码为加号’+'

    :param url:
    :param use_plus:
    :return:
    """
    if use_plus:
        return urllib.quote_plus(url)
    else:
        return urllib.quote(url)