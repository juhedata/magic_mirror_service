# docker下的logstash环境搭建

## 下载
- 下载：docker pull logstash:5.5.1

## 运行
docker run -d -p 4567:4567 -v E:\docker_config\logstash\logstash.yml:/etc/logstash/logstash.yml -v E:\docker_config\logstash\logstash.conf:/usr/share/logstash/pipeline/logstash.conf --name logstash logstash:5.5.1


