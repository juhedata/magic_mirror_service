# docker下的mysql环境搭建

## 下载
- 下载：docker pull mysql:5.7

## 运行
docker run -d --name mysql -e MYSQL_ROOT_PASSWORD=123 -p 3306:3306 mysql:5.7

