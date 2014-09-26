-- psql -h 10.210.3.30 -U ambari ambari
-- pwd default is bigdata

-- 将查询结果写入文件
\o /tmp/ambari-global.txt

select btrim(config_data, ' ') as config_data from clusterconfig where type_name = 'global' and create_timestamp = (select max(create_timestamp) from clusterconfig where type_name = 'global');