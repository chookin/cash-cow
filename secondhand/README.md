# 部署

首先启动mongo

```shel
# for mac
mongod --config /usr/local/etc/mongod.conf &
```

之后运行爬虫程序

# 查询示例

```js
> use goods
switched to db goods

> db.topic.find({title:{$regex:"mac"}},{_id:0}).sort({createTime:-1})

// 查询mac pro mf839ch
// $options的可选值: i 忽略大小写
> db.topic.find({title:{$regex:"mf839ch", $options:'i'}}).sort({createTime:-1})
{ "_id" : "newsmth_二手电脑市场_2102495", "author" : "stevin", "commentCount" : 0, "createTime" : ISODate("2016-11-25T16:00:00Z"), "id" : "2102495", "lastCommentTime" : ISODate("2016-11-25T16:00:00Z"), "title" : "转全新未拆封mac book 128 mf839ch", "url" : "http://newsmth.net/nForum/article/SecondComputer/2102495" }
{ "_id" : "newsmth_二手电脑市场_2088057", "author" : "zzm811206", "commentCount" : 8, "createTime" : ISODate("2016-08-19T16:00:00Z"), "id" : "2088057", "lastCommentTime" : ISODate("2016-08-19T16:00:00Z"), "title" : "6000元出一台macbook pro MF839CH", "url" : "http://newsmth.net/nForum/article/SecondComputer/2088057" }

// 查询荣耀手机
> db.topic.find({title:{$regex:"荣耀"}}).sort({createTime:-1})

// 查询荣耀7手机
> db.topic.find({title:{$regex:"荣耀.*[7七]+"}}).sort({createTime:-1})
{ "_id" : "newsmth_二手数码产品_1911536", "author" : "afra", "commentCount" : 3, "createTime" : ISODate("2016-09-30T16:00:00Z"), "id" : "1911536", "lastCommentTime" : ISODate("2016-10-04T16:00:00Z"), "title" : "[转让]华为荣耀7移动4G版 3G RAM+32G ROM 800元", "url" : "http://www.newsmth.net/nForum/article/SecondDigi/1911536" }
{ "_id" : "newsmth_二手数码产品_1909996", "author" : "原帖已删除", "commentCount" : 8, "createTime" : ISODate("2016-09-25T16:00:00Z"), "id" : "1909996", "lastCommentTime" : ISODate("2016-09-25T16:00:00Z"), "title" : "Re:[转让]华为荣耀7移动版3G+32G银色800元", "url" : "http://www.newsmth.net/nForum/article/SecondDigi/1909996" }
{ "_id" : "newsmth_二手数码产品_1905910", "author" : "原帖已删除", "commentCount" : 2, "createTime" : ISODate("2016-09-13T16:00:00Z"), "id" : "1905910", "lastCommentTime" : ISODate("2016-09-13T16:00:00Z"), "title" : "Re: 出一个华为荣耀7", "url" : "http://www.newsmth.net/nForum/article/SecondDigi/1905910" }


// 看看京东e卡
> db.topic.find({title:{$regex:"京东.*卡"}}).sort({createTime:-1}).limit(10)

// 家乐福
> db.topic.find({title:{$regex:"家乐福"}}).sort({createTime:-1})

// 商通卡
db.topic.find({title:{$regex:"商通卡"}}).sort({createTime:-1})
```
