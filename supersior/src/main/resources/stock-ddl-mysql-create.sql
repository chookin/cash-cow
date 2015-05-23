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
  code char(6) not null comment '股票代码',
  name varchar(16) not null comment '股票名称',
  exchange varchar(4) not null comment '交易所',
  discard bool comment '是否废弃',
  updateTime timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间', # 在创建新记录和修改现有记录的时候都对这个时间列刷新
  primary key (code)
);
create unique index i_stock_code on stock (code);

-- http://qt.gtimg.cn/q=sz000858da
create table if not exists real_data(
  stockCode char(6) not null comment '股票代码',
  time datetime comment '时间',
  open double comment '今开',
  yclose double comment '昨收',
  priceChange double comment '涨跌',
  changeRatio double comment '涨跌%',
  curPrice double comment '当前价格',
  highPrice double comment '最高',
  lowPrice double comment '最低',
  marketValue double comment '流通市值',
  totalValue double comment '总市值',
  primary key (stockCode),
  foreign key (stockCode) references stock(code)
) comment '实时交易';
create unique index i_real_data_stockCode on real_data (stockCode);

-- http://money.finance.sina.com.cn/corp/go.php/vMS_MarketHistory/stockid/000028.phtml?year=2014&jidu=1
create table if not exists history_data(
  id bigint not null auto_increment comment 'record id',
  stockCode char(6) not null comment '股票代码',
  day date comment '日期',
  openPrice double comment '开盘价',
  closePrice double comment '收盘价',
  highPrice double comment '最高价',
  lowPrice double comment '最低价',
  tradeHand bigint comment '成交量(手)',
  tradeValue bigint comment '成交额(元)',
  primary key (stockCode, day),
  unique (id),
  foreign key (stockCode) references stock(code)
) comment '历史交易';
create unique index i_history_data_stockCode_time on history_data (stockCode, day);

-- http://vip.stock.finance.sina.com.cn/quotes_service/view/vMS_tradehistory.php?symbol=sz000028&date=2014-07-03
create table if not exists trade(
  id bigint not null auto_increment comment 'record id',
  stockCode char(6) not null comment '股票代码',
  time datetime comment '时间',
  price double comment '成交价',
  priceChange double comment '涨跌',
  changeRatio double comment '涨跌%',
  tradeHand bigint comment '成交量(手)',
  tradeValue bigint comment '成交额(元)',
  sell bool comment '是否卖盘',
  primary key (stockCode, time),
  unique (id),
  foreign key (stockCode) references stock(stockCode)
) comment '历史成交明细';
create index i_trade_stock_code on trade (stockCode);
create unique index i_trade_stockCode_time on trade (stockCode, time);

--  http://vip.stock.finance.sina.com.cn/corp/go.php/vCI_CorpInfo/stockid/600030.phtml
create table if not exists company_info	(
  stockCode char(6) not null comment '股票代码',
  companyName varchar(256) comment '公司名称',
  companyEnName varchar(256) comment '公司英文名称',
  exchangeCenter varchar(256) comment '上市市场',
  listingDate date comment '上市日期',
  issuePrice double comment '发行价格',
  leadUnderWriter varchar(256) comment '主承销商',
  registrationDate date comment '成立日期',
  registeredCapital double comment '注册资本',
  institutionType varchar(256) comment '机构类型',
  organizationalForm varchar(256) comment '组织形式',
  phone varchar(256) comment '公司电话',
  fax varchar(256) comment '公司传真',
  email varchar(256) comment '公司电子邮箱',
  website varchar(256) comment '公司网址',
  zipcode varchar(256) comment '邮政编码',
  registeredAddress varchar(256) comment '注册地址',
  officeAddress varchar(256) comment '办公地址',
  companyProfile varchar(4096) comment '公司简介',
  businessScope varchar(2048) comment '经营范围',
  tags varchar(128) comment '标签',

  stockNum double comment '总股本(亿)',
  tradable double comment 'tradable share, 流通股(亿)',
  eps double comment 'earnings per share, 每股收益(元)',
  netAsset double comment 'Net asset value per share, 每股净资产(元)',
  cashFlow double comment 'Cash flow per share, 每股现金流(元)',
  fund double comment 'Accumulation fund per share, 每股公积金(元)',
  profit double comment 'profit per share, 每股未分配利润',
  equity double comment 'return on equity, 净资产收益率(%)',
  growth double comment 'net profit growth rate, 净利润增长率(%)',
  gross double comment 'growth rate of gross operating income, 主营收入增长率(%)',

  investSpot text comment '投资亮点',

  coreTheme text comment '核心题材',

  primary key (stockCode),
  foreign key (stockCode) references stock(stockCode)
) comment '公司简介';
create index i_company_info_stockCode on company_info (stockCode);

-- insert company_info(stockCode, company_name) values(123, '经营范围');
-- 查询重复数据
-- select * from stock where stock_name in (select stock_name from stock GROUP BY stock_name having count(1) > 1) order by stock_name;

-- select * from stock where char_length(stock_name) > 4;