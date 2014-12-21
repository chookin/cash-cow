#!/usr/bin/env python
# coding:utf-8
import logging
import platform
import shlex
import subprocess
import re

LOG = logging.getLogger()


def _exec_command(command, shell, use_pipe):
    command = command.strip()
    if not command:
        return True
    LOG.info('exec: "%s"' % command)
    if shell:
        mycmd = command
    elif type(command) == str:
        mycmd = shlex.split(command)

    if use_pipe:
        p = subprocess.Popen(mycmd,
                             stdout=subprocess.PIPE,
                             stdin=subprocess.PIPE,
                             stderr=subprocess.PIPE,
                             shell=shell)
        (stdoutdata, stderrdata) = p.communicate()
        if stdoutdata:
            LOG.info('executing %s, %s' % (command, stdoutdata))
        if stderrdata:
            LOG.error('executing %s, return code(%s), %s'
                      % (command, str(p.returncode), stderrdata))
            return str(p.returncode) is '0'
        else:
            return True
    else:
        p = subprocess.Popen(mycmd, shell=shell)
        p.communicate()
        return str(p.returncode) is '0'


def _exec_commands(commands, shell, use_pipe):
    LOG.info('batch exec: \n"%s"\n==================' % commands)
    if isinstance(commands, basestring):
        commands = re.split(r"\n|;", commands)
    else:  # Array
        commands = commands

    for cmd in commands:
        _exec_command(cmd, shell, use_pipe)


def execute_commands(command, shell=True, use_pipe=False):
    """
    Batch execute commands.
    commands, a list or tuple of command.
    shell, whether executing in shell. If False, it maybe failed to execute for some commands
    use_pipe, set True when you want detailed running info.
    """
    _exec_commands(command, shell, use_pipe)


def execute_command(command, shell=True, use_pipe=False):
    """
    Execute a command.
    command, a string representation of shell command.
    shell, whether executing in shell. If False, it maybe failed to execute for some commands.
    use_pipe, set True when you want detailed running info.
    """
    _exec_command(command, shell, use_pipe)


class OSHelper():
    def __init__(self):
        self.os_name = None
        self.os_family = None
        self.app_manager = None

    def get_operating_system(self):
        """
        Returns the name of the OS, such as RedHat, SLES, OracleLinux.
        """
        if self.os_name is not None:
            return self.os_name
        dist = platform.linux_distribution()
        operatingSystem = dist[0].lower()

        if shlex.os.path.exists('/etc/oracle-release'):
            self.os_name = 'OracleLinux'
        elif operatingSystem.startswith('suse linux enterprise server'):
            self.os_name = 'SLES'
        elif operatingSystem.startswith('red hat enterprise linux server'):
            self.os_name = 'RedHat'
        elif operatingSystem != '':
            self.os_name = operatingSystem
        else:
            self.os_name = 'OS NOT SUPPORTED'
        return self.os_name

    def get_os_family(self):
        """
        Returns the operating system family, such as 'redhat', 'suse', 'debian'.
        Warn: Centos is regared as redhat family.
        """
        if self.os_family is not None:
            return self.os_family
        os_family = self.get_operating_system().lower()
        if os_family in ['redhat', 'fedora', 'centos', 'oraclelinux', 'ascendos',
                         'amazon', 'xenserver', 'oel', 'ovs', 'cloudlinux',
                         'slc', 'scientific', 'psbm']:
            os_family = 'rhel'
        elif os_family in ['ubuntu', 'debian']:
            os_family = 'debian'
        elif os_family in ['sles', 'sled', 'opensuse', 'suse']:
            os_family = 'suse'
        elif os_family == '':
            os_family = 'OS NOT SUPPORTED'
        else:
            os_family = self.get_operating_system()
        self.os_family = os_family
        return self.os_family

    def get_package_manager(self):
        """
        Returns the package manager, such as 'yum', 'zypper'.
        """
        if self.app_manager is not None:
            return self.app_manager

        if self.get_os_family() == 'rhel':
            self.app_manager = 'yum'
        elif self.get_os_family() == 'suse':
            self.app_manager = 'zypper'
        else:
            raise IOError("unsupported os version: %s" % self.os_family)
        return self.app_manager


if __name__ == '__main__':
    print "package manager:", OSHelper().get_package_manager()