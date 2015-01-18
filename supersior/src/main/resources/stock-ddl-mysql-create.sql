-- DROP DATABASE IF EXISTS `stock`;
-- DROP USER `chookin`;

-- 告诉mysql解释器，该段命令是否已经结束了，mysql是否可以执行了，默认情况下，delimiter是分号
-- mysql data base's location: /var/lib/mysql

delimiter ;

create database if not exists `stock` default character set utf8;

create user 'chookin' identified by 'winwin';
grant all on *.* to 'chookin'@'localhost' identified by 'winwin';
grant all on *.* to 'chookin'@'%' identified by 'winwin';

use 'stock';

create table if not exists holiday(
  day date comment '日期',
  descr varchar(32) COMMENT '描述',
  PRIMARY KEY (day),
  UNIQUE (day)
);

create table if not exists stock(
  stock_id int not null auto_increment comment '股票id',
  stock_code varchar(6) not null comment '股票代码',
  stock_name varchar(16) not null comment '股票名称',
  exchange varchar(4) not null comment '交易所',
  discarded bool comment '是否废弃',
  update_time datetime comment '更新时间',
  primary key (stock_code),
  unique (stock_id)
);
create unique index i_stock_id on stock (stock_id);
create unique index i_stock_code on stock (stock_code);

-- http://qt.gtimg.cn/q=sz000858da
create table if not exists real_data(
  stock_code varchar(6) not null comment '股票代码',
  time datetime comment '时间',
  open double comment '今开',
  yclose double comment '昨收',
  price_change double comment '涨跌',
  change_ratio double comment '涨跌%',
  cur_price double comment '当前价格',
  high_price double comment '最高',
  low_price double comment '最低',
  market_value double comment '流通市值',
  total_value double comment '总市值',
  primary key (stock_code),
  foreign key (stock_code) references stock(stock_code)
) comment '实时交易';
create unique index i_real_data_stock_code on real_data (stock_code);

-- http://money.finance.sina.com.cn/corp/go.php/vMS_MarketHistory/stockid/000028.phtml?year=2014&jidu=1
create table if not exists history_data(
  id bigint not null auto_increment comment '记录id，自增',
  stock_id int not null comment '股票id',
  time date comment '日期',
  open_price double comment '开盘价',
  close_price double comment '收盘价',
  high_price double comment '最高价',
  low_price double comment '最低价',
  trade_hand bigint comment '成交量(手)',
  trade_value bigint comment '成交额(元)',
  primary key (stock_id, time),
  unique (id),
  foreign key (stock_id) references stock(stock_id)
) comment '历史交易';
create index i_history_data_stock_id on history_data (stock_id);
create unique index i_history_data_stock_id_time on history_data (stock_id, time);

-- http://vip.stock.finance.sina.com.cn/quotes_service/view/vMS_tradehistory.php?symbol=sz000028&date=2014-07-03
create table if not exists history_data_detail(
  id bigint not null auto_increment comment '记录id，自增',
  stock_id int not null comment '股票id',
  time datetime comment '时间',
  price double comment '成交价',
  price_change double comment '涨跌',
  change_ratio double comment '涨跌%',
  trade_hand bigint comment '成交量(手)',
  trade_value bigint comment '成交额(元)',
  is_sell bool comment '是否卖盘',
  primary key (stock_id, time),
  unique (id),
  foreign key (stock_id) references stock(stock_id)
) comment '历史成交明细';
create index i_history_deal_detail_stock_id on history_data_detail (stock_id);
create unique index i_history_deal_detail_stock_id_time on history_data_detail (stock_id, time);

--  http://vip.stock.finance.sina.com.cn/corp/go.php/vCI_CorpInfo/stockid/600030.phtml
create table if not exists company_info	(
  stock_code varchar(6) not null comment '股票代码',
  company_name varchar(256) comment '公司名称',
  company_en_name varchar(256) comment '公司英文名称',
  exchange_center varchar(256) comment '上市市场',
  listing_date date comment '上市日期',
  issure_price double comment '发行价格',
  lead_under_writer varchar(256) comment '主承销商',
  registration_date date comment '成立日期',
  registered_capital double comment '注册资本',
  insititution_type varchar(256) comment '机构类型',
  organizational_form varchar(256) comment '组织形式',
  phone varchar(256) comment '公司电话',
  fax varchar(256) comment '公司传真',
  email varchar(256) comment '公司电子邮箱',
  website varchar(256) comment '公司网址',
  zipcode varchar(256) comment '邮政编码',
  registered_address varchar(256) comment '注册地址',
  office_address varchar(256) comment '办公地址',
  company_profile varchar(4096) comment '公司简介',
  business_scope varchar(2048) comment '经营范围',
  tags varchar(128) comment '标签',

  stock_num double comment '总股本(亿)',
  tradable double comment 'tradable share, 流通股(亿)',
  eps double comment 'earnings per share, 每股收益(元)',
  net_asset double comment 'Net asset value per share, 每股净资产(元)',
  cash_flow double comment 'Cash flow per share, 每股现金流(元)',
  fund double comment 'Accumulation fund per share, 每股公积金(元)',
  profit double comment 'profit per share, 每股未分配利润',
  equity double comment 'return on equity, 净资产收益率(%)',
  growth double comment 'net profit growth rate, 净利润增长率(%)',
  gross double comment 'growth rate of gross operating income, 主营收入增长率(%)',

  invest_spot varchar(20480) comment '投资亮点',

  core_theme varchar(20480) comment '核心题材',

  primary key (stock_code),
  foreign key (stock_code) references stock(stock_code)
) comment '公司简介';
create index i_company_info_stock_code on company_info (stock_code);

-- insert company_info(stock_code, company_name) values(123, '经营范围');
-- 查询重复数据
-- select * from stock where stock_name in (select stock_name from stock GROUP BY stock_name having count(1) > 1) order by stock_name;

-- select * from stock where char_length(stock_name) > 4;