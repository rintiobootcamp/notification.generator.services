FROM ibrahim/alpine
ADD target/notificationServices.jar ws_notificationServices_sf.jar
EXPOSE 8092
ENTRYPOINT ["java","-jar","ws_notificationServices_sf.jar"]