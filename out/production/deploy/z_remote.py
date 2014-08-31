#!/usr/bin/env python
#coding:utf-8

import logging
import traceback
import paramiko
import os
import re

LOG = logging.getLogger()


class Remote(object):
    def __init__(self,hostname, username, password):
        self.hostname = hostname
        self.username = username
        self.password = password
        self.ssh_client = None
        self.sftp = None

    def _get_sshclient(self):
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
        if self.sftp is None:
            t = paramiko.Transport((self.hostname, 22))
            t.connect(username=self.username, password=self.password)
            self.sftp = paramiko.SFTPClient.from_transport(t)
        else:
            pass
        return self.sftp

    def close(self):
        if self.ssh_client is not None:
            self.ssh_client.close()
            self.ssh_client = None
        if self.sftp is not None:
            self.sftp.sock.close()
            self.sftp = None

    def exec_commands(self, commands):
        """
        batch executing commands
        :param commands: commands that need to execute, separated by '\n' or ';'
        ;return: True only if all the commands are success
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
        Executing a command on remote host
        :param command: the command need executing
        :return: True if success to execute command
        """
        command = command.strip()
        if command:
            s = self._get_sshclient()
            LOG.info('remote %s "%s" ...' % (self.hostname, command))
            stdin, stdout, stderr = s.exec_command(command)
            myread = stdout.read()
            if myread:
                LOG.info('%s, %s' % (self.hostname, myread))
            myerr = stderr.read()
            if myerr:
                ignored_err = 'tput: No value for $TERM and no -T specified'
                if myerr.startswith(ignored_err) \
                        or myerr.startswith('info') or myerr.startswith('INFO') \
                        or myerr.startswith('warn') or myerr.startswith('WARN'):
                    LOG.warn('%s, %s' % (self.hostname, myerr))
                    return True
                else:
                    LOG.error('%s, %s' % (self.hostname, myerr))
                    return False
            else:
                return True

    def _sftp_put(self, sftp, local_file, remote_path):
        # the destination path should include the file name
        local_file = os.path.abspath(local_file)
        filename = os.path.split(local_file)[1].strip()
        if not filename:
            LOG.error('"%s" is not a file' % local_file)
            return False
        dst_file_name = os.path.split(remote_path)[1].strip()
        if not dst_file_name:
            remote_path = os.path.join(remote_path, filename)
            
        LOG.info('remote %s put "%s" to "%s"' % (self.hostname, local_file, remote_path))
        if os.path.isfile(local_file):
            sftp.put(local_file,remote_path)
            return True
        else:
            LOG.error('source file "%s" does not exist' % local_file)
            return False

    def put(self, local_file, remote_path):
        sftp = self._get_sftp()
        return self._sftp_put(sftp, local_file, remote_path)

    def put_dir(self, local_path, remote_path):
        """
        recursively upload a full directory
        :param local_path:
        :param remote_path:
        :return: True only if all the files are uploaded success.
        """
        local_path = os.path.abspath(local_path)
        LOG.info('remote %s put_dir "%s" to "%s"' % (self.hostname, local_path, remote_path))
        sftp = self._get_sftp()
        try:
            if self.exists(sftp, remote_path):
                pass
            else:
                self.sftp.mkdir(remote_path)
        except:
            LOG.error('failed to make dir %s to host: %s. %s'
                      % (remote_path, self.hostname, str(traceback.format_exc())))
        result = True
        for walker in os.walk(local_path):
            print 'walker' + str(walker)
            for file in walker[2]:
                local_file = os.path.join(walker[0], file)
                remote_file = os.path.join(remote_path, file)
                result = result and self._sftp_put(sftp, local_file, remote_file)

            for subdir in walker[1]:
                local_dir = os.path.join(walker[0], subdir)
                remote_dir = os.path.join(remote_path, subdir)
                result = result and self.put_dir(local_dir, remote_dir)
            break
        return result

    @staticmethod
    def exists(sftp, path):
        """os.path.exists for paramiko's SCP object
        """
        try:
            sftp.stat(path)
        except IOError, e:
            if e[0] == 2:
                return False
            raise
        else:
            return True

if __name__ == '__main__':
    remote = Remote('bch1.local.novalocal', 'root', 'hadoop')
    remote.put_dir('/home/chookin/Desktop/test', '/test')