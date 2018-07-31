FROM openjdk:8-jdk-alpine
ADD target/notificationServices.jar ws_notificationServices_sf.jar
EXPOSE 8092
ENV JAVA_OPTS="-XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap  -XX:MaxRAMFraction=1 -XshowSettings:vm "
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar ws_notificationServices_sf.jar" ]