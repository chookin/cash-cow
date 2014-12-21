#!/usr/bin/env python
# coding:utf-8

import logging
import traceback
import paramiko
import os
import re

LOG = logging.getLogger()


class Remote(object):
    def __init__(self, hostname, username, password, invoke_shell=False):
        """
        Set invoke_shell to True when happens error: 'sudo: sorry, you must have a tty to run sudo'.
        """
        self.hostname = hostname
        self.username = username
        self.password = password
        self.ssh_client = None
        self.sftp = None
        self.local_host_helper = LocalHostHelper()
        self.invoke_shell = invoke_shell

    def _get_sshclient(self):
        """
        Get the ssh handler for executing remote commands.
        """
        if self.ssh_client is None:
            self.ssh_client = paramiko.SSHClient()
            self.ssh_client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
            self.ssh_client.connect(
                self.hostname,
                username=self.username,
                password=self.password,
                allow_agent=False,
                look_for_keys=False)
        else:
            pass
        return self.ssh_client

    def _get_sftp(self):
        """
        Get the sftp handler for copying files to remote hosts.
        """
        if self.sftp is None:
            t = paramiko.Transport((self.hostname, 22))
            t.connect(username=self.username, password=self.password)
            self.sftp = paramiko.SFTPClient.from_transport(t)
        else:
            pass
        return self.sftp

    def close(self):
        """
        Close all the got handlers.
        """
        if self.ssh_client is not None:
            self.ssh_client.close()
            self.ssh_client = None
        if self.sftp is not None:
            self.sftp.sock.close()
            self.sftp = None

    def exec_commands(self, commands):
        """
        Batch executing commands.
        :param commands: commands that need to execute, separated by '\n' or ';'.
        ;return: True only if all the commands are success.
        """
        result = True
        if isinstance(commands, basestring):
            commands = re.split(r"\n|;", commands)
        else:  # Array
            pass
        for cmd in commands:
            me = self.exec_command(cmd)
            result = result and me
        return result

    def exec_command(self, command):
        """
        Executing a command on remote host.
        :param command: the command need executing.
        :return: True if success to execute command.
        """
        command = command.strip()
        if not command:
            return True
        s = self._get_sshclient()
        LOG.info('remote %s `%s` ...' % (self.hostname, command))
        is_success = True

        if self.invoke_shell:
            tran = s.get_transport()
            chan = tran.open_session()
            # chan.set_combine_stderr(True)
            chan.get_pty()
            stdout = chan.makefile()
            stderr = chan.makefile_stderr('rb')
            chan.exec_command(command)
        else:
            stdin, stdout, stderr = s.exec_command(command)
        for line in stdout:
            if line.strip():
                if 'try using --skip-broken to work around' in line:
                    is_success = False
                    LOG.error('%s, %s' % (self.hostname, line))
                else:
                    LOG.info('%s, %s' % (self.hostname, line))

        myerr = stderr.read()
        if myerr:
            ignored_err = 'tput: No value for $TERM and no -T specified'
            if myerr.startswith(ignored_err) \
                    or myerr.startswith('Unable to read consumer identity') \
                    or myerr.startswith('info') or myerr.startswith('INFO') \
                    or myerr.startswith('warn') or myerr.startswith('WARN'):
                LOG.warn('%s, %s' % (self.hostname, myerr))
                return is_success
            else:
                LOG.error('%s, failed to execute `%s`, %s' % (self.hostname, command, myerr))
                return False
        else:
            return is_success

    def _sftp_put(self, local_file, remote_path):
        para_remote_path = remote_path
        # the destination path should include the file name
        local_file = os.path.abspath(local_file)
        filename = os.path.split(local_file)[1].strip()
        if not filename:
            LOG.error('"%s" is not a file' % local_file)
            return False
        dst_file_name = os.path.split(remote_path)[1].strip()
        if not dst_file_name:
            remote_path = os.path.join(remote_path, filename)

        remote_path = os.path.abspath(remote_path)
        if local_file == remote_path:
            if self.hostname in self.local_host_helper.getHostnameIPSet():
                print "no need to remotely copy local file %s to %s of %s" \
                      % (local_file, para_remote_path, self.hostname)
                return True

        LOG.info('remote %s put "%s" to "%s"' % (self.hostname, local_file, remote_path))
        if os.path.isfile(local_file):
            try:
                self._get_sftp().put(local_file, remote_path)
            except:
                raise IOError("failed to remotely copy %s to %s: %s"
                              % (local_file, remote_path, str(traceback.format_exc())))
            return True
        else:
            LOG.error('source file "%s" does not exist' % local_file)
            return False

    def put(self, local_file, remote_path):
        """
        Copy local file to remote host.
        If remote_path is a directory, it must end with a left slash.
        Returns True if success, or else False.
        """
        local_file = local_file.strip()
        if os.path.islink(local_file):
            LOG.warn('copy %s though it is a link' % local_file)
            return True

        remote_path = remote_path.strip()
        remote_dir = os.path.split(remote_path)[0]
        if not self.make_remote_dir(remote_dir):
            return False

        return self._sftp_put(local_file, remote_path)

    def put_dir(self, local_path, remote_path):
        """
        Recursively upload a full directory: copy files under local_path to under remote_path.
        :param local_path: the local directory path.
        :param remote_path: if not exist, create it.
        :return: True only if all the files are uploaded success.
        """
        local_path = local_path.strip()
        remote_path = remote_path.strip()

        local_path = os.path.abspath(local_path)
        LOG.info('remote %s put_dir "%s" to "%s"' % (self.hostname, local_path, remote_path))
        if os.path.islink(local_path):
            LOG.warn('%s is a link, ignore it' % local_path)
            return True

        items = re.split(r"/|\\", local_path)
        remote_path = os.path.join(remote_path, items[len(items) - 1])
        sftp = self._get_sftp()

        if not self.make_remote_dir(remote_path):
            return False

        result = True
        for walker in os.walk(local_path):
            print 'walker' + str(walker)
            for file in walker[2]:
                local_file = os.path.join(walker[0], file)
                remote_file = os.path.join(remote_path, file)
                result = result and self._sftp_put(local_file, remote_file)

            for subdir in walker[1]:
                local_dir = os.path.join(walker[0], subdir)
                result = result and self.put_dir(local_dir, remote_path)
            break
        return result

    def exists(self, path):
        """
        Check remote path whether exists.
        Returns True if exists, or else False.
        """
        try:
            self._get_sftp().stat(path)
        except IOError, e:
            if e[0] == 2:
                return False
            raise
        else:
            return True

    def _make_remote_dir(self, remote_path):
        """
        Make the remote path directly, no check, no recursively.
        """
        try:
            self._get_sftp().mkdir(remote_path)
        except:
            LOG.error('failed to make dir %s to host: %s. %s'
                      % (remote_path, self.hostname, str(traceback.format_exc())))
            return False
        return True

    def make_remote_dir(self, remote_path, recursive=True):
        if self.exists(remote_path):
            return True
        if not recursive:
            return self._make_remote_dir(remote_path)

        items = re.split(r"/|\\", remote_path)
        mydir = ''
        for item in items:  # make dir recursively
            if not item:
                continue
            mydir = '%s/%s' % (mydir, item)
            if self.exists(mydir):
                continue
            if not self._make_remote_dir(mydir):
                return False
        return True


class LocalHostHelper():
    def __init__(self):
        self.ip_addresses = None
        self.hostname_ip_map = None
        self.hostname_ip_set = None

    def refresh(self):
        self.ip_addresses = None
        self.hostname_ip_map = None
        self.hostname_ip_set = None

    def getLocalHostIPs(self):
        """
        get all the ip addresses of local host
        """
        if self.ip_addresses is not None:
            return self.ip_addresses

        ipstr = os.popen("/sbin/ifconfig | grep 'inet addr' | awk '{print $2}'").read()
        # retrieve local host's ipaddresses by ifconfig
        start = 0
        end = 0
        ips = set()
        while True:
            start = ipstr.find(':', end)
            if start is -1:
                break
            end = ipstr.find('\n', start)
            ip = ipstr[(start + 1):end]
            ips.add(ip)
        ips.add('127.0.0.1')
        self.ip_addresses = ips
        return self.ip_addresses

    def getHostnameIPMap(self):
        """
        get a <hostname, ip> map of local host's all the hostnames
        """
        if self.hostname_ip_map is not None:
            return self.hostname_ip_map

        hostIpMap = {}
        filename = '/etc/hosts'
        # get host_ip_map from /etc/hosts
        for line in open(filename):
            line = line.strip()
            if not line:
                continue

            if line.startswith('#'):
                continue

            items = re.split(r"\s", line)
            valid_items = []
            for item in items:
                if item.strip():
                    valid_items.append(item)
            size = len(valid_items)
            if len(valid_items) > 1:
                ip = valid_items[0]
                if ip not in self.getLocalHostIPs():
                    continue
                for i in range(1, size):
                    host = valid_items[i]
                    if host not in hostIpMap:
                        hostIpMap[host] = ip
        self.hostname_ip_map = hostIpMap
        return self.hostname_ip_map

    def getHostnameIPSet(self):
        """
        get a set which contains local host's all the hostnames and ip addresses
        """
        if self.hostname_ip_set is not None:
            return self.hostname_ip_set

        hostIpMap = self.getHostnameIPMap()
        hostIpSet = set()
        for k, v in hostIpMap.items():
            hostIpSet.add(k)
            hostIpSet.add(v)

        self.hostname_ip_set = hostIpSet
        return self.hostname_ip_set


if __name__ == '__main__':
    remote = Remote('localhost', 'root', 'hadoop')
    remote.put('/tmp/hosts', '/tmp/')
    remote.put(' /tmp/hosts ', ' /tmp//// ')
    remote.put_dir(' /etc/hadoop', '/tmp/hadoop')
    remote.put_dir('/etc/ssh', '/tmp/ssh')