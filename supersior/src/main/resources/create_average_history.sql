/*因为MySQL默认是以分号作为SQL语句的结束符的，而函数体内部要用到分号，所以会跟默认的SQL结束符发生冲突，所以需要先定义一个其他的符号作为SQL的结束符；*/
/*查看数据库中有哪些存储过程，可以使用SHOW PROCEDURE STATUS命令*/
delimiter $$
drop procedure if exists average_history $$
drop table if exists price_potential $$
create table if not exists price_potential(
	id bigint not null auto_increment comment 'record id',
	stockCode char(6) not null comment '股票代码',
	startDay date comment '开始日期',
	endDay date comment '开始日期',
	startLowPrice double,
	endHighPrice double,
	incrRatio double comment '涨幅',
	primary key (stockCode, startDay, endDay),
	unique (id),
	foreign key (stockCode) references stock(code)
) comment '价格势'$$
create procedure average_history(IN start_day date, IN end_day date)
	begin
		DECLARE stock_code CHAR(6);
		declare start_low_price, end_high_price, incr_ratio double;
		DECLARE  done INT DEFAULT 0;
		DECLARE rec_count INT DEFAULT 0;
		DECLARE cur_stock CURSOR FOR SELECT code FROM stock;  /*First: Delcare a cursor,首先这里对游标进行定义*/
		DECLARE  CONTINUE HANDLER FOR SQLSTATE '02000' SET done=1; /*when "not found" occur,just continue,这个是个条件处理,针对NOT FOUND的条件*/
		OPEN cur_stock; /*Second: Open the cursor 接着使用OPEN打开游标*/
		REPEAT
			FETCH cur_stock INTO stock_code; /*Third: now you can Fetch the row 把第一行数据写入变量中,游标也随之指向了记录的第一行*/
			select IFNULL ((select lowPrice from history where day = start_day and stockCode = stock_code limit 1), 0) into start_low_price;
			select IFNULL ((select highPrice from history where day = end_day and stockCode = stock_code limit 1), 0) into end_high_price;
			if start_low_price != 0 and end_high_price != 0
			then
				set incr_ratio = (end_high_price - start_low_price) / start_low_price * 100;
				replace into price_potential(stockCode, startDay,  endDay, startLowPrice, endHighPrice, incrRatio) values(stock_code, start_day, end_day, start_low_price, end_high_price, incr_ratio);
			end if;
			set rec_count = rec_count +1;
		UNTIL done=1 END REPEAT;
		CLOSE  cur_stock;  /*Finally: cursor need be closed 用完后记得用CLOSE把资源释放掉*/
		select rec_count;
	end;


$$
delimiter ;


# delete from price_potential;
# call average_history('2015-1-5', '2015-5-5');
#
# select pp.stockCode, cm.companyName, pp.startLowPrice, pp.endHighPrice, pp.incrRatio from price_potential as pp, company as cm
# where pp.stockCode = cm.stockCode and cm.companyName is not null and pp.incrRatio < 15 and pp.incrRatio > 5
# order by pp.incrRatio desc;