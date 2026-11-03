FROM bellsoft/liberica-runtime-container:jdk-21-musl
COPY target/ia-service-0.0.1-SNAPSHOT.jar ia-service-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/ia-service-0.0.1-SNAPSHOT.jar"]
