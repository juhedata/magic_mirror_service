# docker下的kibana环境搭建

## 下载
- 下载：docker pull kibana:5.5.1

## 运行
docker run  --link elasticsearch容器的name或者ID:elasticsearch -d --name kibana -p 5601:5601  kibana:5.5.1

例如：
docker run  --link elasticsearch -d --name kibana -p 5601:5601  kibana:5.5.1
