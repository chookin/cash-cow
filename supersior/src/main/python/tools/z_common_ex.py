#!/usr/bin/env python
# coding:utf-8
import platform
import shlex
import subprocess

VERBOSE = True


class FatalException(Exception):
    def __init__(self, code, reason):
        self.code = code
        self.reason = reason

    def __str__(self):
        return repr("Fatal exception: %s, exit code %s" % (self.reason, self.code))

    def _get_message(self):
        return str(self)


class NonFatalException(Exception):
    def __init__(self, reason):
        self.reason = reason

    def __str__(self):
        return repr("NonFatal exception: %s" % self.reason)

    def _get_message(self):
        return str(self)


def is_root():
    '''
    Checks effective UUID
    Returns True if a program is running under root-level privileges.
    '''
    return shlex.os.geteuid() == 0


def get_exec_path(cmd):
    cmd = 'which {0}'.format(cmd)
    ret, out, err = run_in_shell(cmd)
    if ret == 0:
        return out.strip()
    else:
        return None


def run_in_shell(cmd):
    print_info_msg('about to run command: ' + str(cmd))
    process = subprocess.Popen(cmd,
                               stdout=subprocess.PIPE,
                               stdin=subprocess.PIPE,
                               stderr=subprocess.PIPE,
                               shell=True
    )
    (stdoutdata, stderrdata) = process.communicate()
    return process.returncode, stdoutdata, stderrdata


def run_os_command(cmd):
    print_info_msg('about to run command: ' + str(cmd))
    if type(cmd) == str:
        cmd = shlex.split(cmd)
    process = subprocess.Popen(cmd,
                               stdout=subprocess.PIPE,
                               stdin=subprocess.PIPE,
                               stderr=subprocess.PIPE
    )
    (stdoutdata, stderrdata) = process.communicate()
    return process.returncode, stdoutdata, stderrdata


def print_info_msg(msg):
    """
    Prints an "info" messsage.
    """
    if VERBOSE:
        print("INFO: " + msg)


def print_error_msg(msg):
    """
    Prints an "error" messsage.
    """
    print("ERROR: " + msg)


OS, OS_VERSION, _ = platform.linux_distribution()
OS = OS.lower().strip()