#!/bin/sh bash

# init the host for call remote commands
# python-devel' # depenecy for paramiko
# python-setuptools python-setuptools-devel  #easy_install dependency

if [ "$2" = "-h" ] || [ "$2" = "--help" ]; then
	echo "Options:
	-h, --help  show this help message and exit"
	exit 0
fi

echo -e 'Are you sure to initialize?(press "y" for yes:)\c'
read ch
if [ "$ch" != "y" ]
then
  	echo -e 'quit for pressed key is' $ch
  	exit 0
fi

rpms_need_on_depoloyer_host=("python python-devel python-setuptools python-setuptools-devel")
echo install rpms: ${rpms_need_on_depoloyer_host[*]}
echo

# travesal array of shell
for rpm in ${rpms_need_on_depoloyer_host[@]}
	do
	if  rpm -qa | grep $rpm
	then
		echo
		echo "$rpm was installed"
		echo
	else
		echo
		echo "$rpm is no install"
		echo
		sudo yum -y install $rpm
	fi
done

# the newest python version of centos yum repository is 2.6.6
# so, if need to install new version, u can do:
# wget https://www.python.org/ftp/python/2.7.8/Python-2.7.8.tgz
# tar xvf Python*.tgz
# cd Python*
# ./configure; make; sudo make install

# install pip
# wget https://bootstrap.pypa.io/get-pip.py
# python get-pip.py

sudo easy_install MySQL-python
sudo easy_install PyMongo
sudo easy_install unicodecsv #json2csv, used for python 2.6.6
sudo easy_install argparse  #json2csv
# sudo easy_install http://argparse.googlecode.com/files/argparse-1.2.1.tar.gz

chkconfig mysqld on
chkconfig mongod on
