#!/usr/bin/env python
#coding:utf-8

'''
修改ambari数据库以更换oozie元数据库为oracle的具体操作为，依次如下三步：
psql -h 10.210.3.30 -U ambari ambari -f ambari-get-global-config.sql 
python update_ambari.py oozie
psql -h 10.210.3.30 -U ambari ambari -f /tmp/ambari-global-updated.sql
其中，10.210.3.30为ambari-server的ip，需根据具体情况调整；ambari数据库的默认登录密码是bigdata
'''

def get_config_data(filename):
    i = 0
    for line in open(filename):
        i += 1
        if i == 3:
            return line

def update_oozie_db(config_data):
    print 'source global', config_data
    import json
    target = json.JSONDecoder().decode(config_data)
    target['oozie_database_type']='oracle'
    target['oozie_database']='Existing Oracle Database'
    target['oozie_jdbc_driver']='oracle.jdbc.driver.OracleDriver'
    if 'oozie_derby_database' in target.keys():
        target.pop('oozie_derby_database')
    if 'oozie_existing_mysql_database' in target.keys():
        target.pop('oozie_existing_mysql_database')
    target['oozie_existing_oracle_database']='Oracle'
    updated_config_data = json.JSONEncoder().encode(target)
    print 'updated config data', updated_config_data
    return updated_config_data

def update_hive_db(config_data):
    print 'source global', config_data
    import json
    target = json.JSONDecoder().decode(config_data)
    target['hive_database_type']='oracle'
    target['hive_database']='Existing Oracle Database'
    target['hive_jdbc_driver']='oracle.jdbc.driver.OracleDriver'
    if 'hive_existing_mysql_database' in target.keys():
        target.pop('hive_existing_mysql_database')
    target['hive_existing_oracle_database']='Oracle'
    updated_config_data = json.JSONEncoder().encode(target)
    print 'updated global', updated_config_data
    return updated_config_data

def get_update_sql(config_data):
    return "update clusterconfig set config_data='%s' where type_name = 'global' and create_timestamp = (select max(create_timestamp) from clusterconfig where type_name = 'global');" % config_data

def save(filename, strdata):
    output = open(filename, 'w')
    output.write(strdata)
    output.close()


if __name__ == '__main__':
    config_data = get_config_data('/tmp/ambari-global.txt')
    import sys
    if len(sys.argv) is not 2:
        print 'Usage:'
        print '\t', 'python update_ambari.py hive'
        print '\t\t', '-- to update hive meta db to oracle'
        print '\t', 'python update_ambari.py oozie'
        print '\t\t', '-- to update oozie meta db to oracle'
        exit(-1)

    updated_config_data = None
    updated_type = sys.argv[1]

    if updated_type == 'oozie':
        updated_config_data = update_oozie_db(config_data)
    elif updated_type == 'hive':
        updated_config_data = update_hive_db(config_data)

    if updated_config_data is None:
        exit(0)
    sql = get_update_sql(updated_config_data) 
    sql_file = '/tmp/ambari-global-updated.sql'
    print
    print 'save the update sql command to %s' % sql_file
    save(sql_file, sql)
