# sql工具

## 功能说明
    - 根据配置的实体类位置，生成对应的sql文件
    - 根据配置的实体类位置，初始化对应的数据库表
    - 根据配置的实体类位置, 针对实体类的变化更新数据库表

## 配置说明

``` 
sql.name=
// 实体类所在的路径, 例如: src/main/java/...
sql.entity.package=
// 是否启用生成sql文件
sql.file.enable=
// 是否启用初始化数据库
sql.initDB.enable=
//数据库连接信息
sql.jdbc.url=
sql.jdbc.username=
sql.jdbc.password=
sql.jdbc.driver=

## 使用说明
- 
