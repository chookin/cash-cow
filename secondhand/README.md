
# 查询

```js
db.topic.find({title:{$regex:"mac"}}).sort({createTime:-1})
// 查询mac pro mf839cha
// $options的可选值: i 忽略大小写
db.topic.find({title:{$regex:"mf839ch", $options:'i'}}).sort({createTime:-1})
// 查询荣耀手机
db.topic.find({title:{$regex:"荣耀"}}).sort({createTime:-1})
```
