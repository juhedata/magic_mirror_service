# docker下的elasticsearch环境搭建

## 下载
- 下载：docker pull elasticsearch:5.5.1

## 运行
docker run -d -p 9200:9200 -p 9300:9300 --name elasticsearch elasticsearch:5.5.1
