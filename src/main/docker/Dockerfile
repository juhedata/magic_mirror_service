#基于哪个镜像
FROM java:8
#将本地文件夹挂载到当前容器
#VOLUME /tmp
#复制文件到容器

ADD magic_mirror.jar app.jar
RUN bash -c 'touch /app.jar'

#申明需要暴露的端口
EXPOSE 8000

#配置容器启动后执行的命令
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
