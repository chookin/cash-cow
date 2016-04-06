#!/usr/bin/env python
# coding:utf-8

import getpass
import re

SILENT = False
PG_DEFAULT_PASSWORD = "bigdata"
USERNAME_PATTERN = "^[a-zA-Z_][a-zA-Z0-9_\-]*$"
PASSWORD_PATTERN = "^[a-zA-Z0-9_-]*$"


def read_password(passwordDefault=PG_DEFAULT_PASSWORD,
                  passwordPattern=PASSWORD_PATTERN,
                  passwordPrompt=None,
                  passwordDescr=None):
    # setup password
    if passwordPrompt is None:
        passwordPrompt = 'Password (' + passwordDefault + '): '

    if passwordDescr is None:
        passwordDescr = "Invalid characters in password. Use only alphanumeric or " \
                        "_ or - characters"

    password = get_validated_string_input(passwordPrompt, passwordDefault,
                                          passwordPattern, passwordDescr, True)

    if not password:
        print 'Password cannot be blank.'
        return read_password(passwordDefault, passwordPattern, passwordPrompt,
                             passwordDescr)

    if password != passwordDefault:
        password1 = get_validated_string_input("Re-enter password: ",
                                               passwordDefault, passwordPattern, passwordDescr, True)
        if password != password1:
            print "Passwords do not match"
            return read_password(passwordDefault, passwordPattern, passwordPrompt,
                                 passwordDescr)

    return password


def get_validated_string_input(prompt, default, pattern, description,
                               is_pass, allowEmpty=True, validatorFunction=None):
    input = ""
    while not input:
        if SILENT:
            print (prompt)
            input = default
        elif is_pass:
            input = getpass.getpass(prompt)
        else:
            input = raw_input(prompt)
        if not input.strip():
            # Empty input - if default available use default
            if not allowEmpty and not default:
                print 'Property cannot be blank.'
                input = ""
                continue
            else:
                input = default
                if validatorFunction:
                    if not validatorFunction(input):
                        input = ""
                        continue
                break  # done here and picking up default
        else:
            if not pattern is None and not re.search(pattern, input.strip()):
                print description
                input = ""

            if validatorFunction:
                if not validatorFunction(input):
                    input = ""
                    continue
    return input