FROM openjdk:21-jdk-slim
ENV LANG C.UTF-8
WORKDIR /application
COPY ./*.jar application.jar
EXPOSE 3000
ENV TZ=Asia/Shanghai
ENTRYPOINT exec java $JAVA_OPTS -jar -Dfile.encoding=UTF-8 -Dio.netty.leakDetectionLevel=ADVANCED application.jar