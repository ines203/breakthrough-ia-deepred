FROM bellsoft/liberica-runtime-container:jdk-21-musl
COPY target/breakthrough_backend-0.0.1-SNAPSHOT.jar breakthrough_backend-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/breakthrough_backend-0.0.1-SNAPSHOT.jar"]
