FROM maven:3.8.1-openjdk-11-slim

WORKDIR /usr/file-storage-system
COPY pom.xml /usr/file-storage-system
RUN mvn dependency:go-offline
COPY . /usr/file-storage-system
RUN mvn install -DskipTests

WORKDIR /usr/file-storage-system/target
RUN cp /usr/file-storage-system/target/*.jar ./app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-server", "-Xms64M","-Xmx1024M","-XX:+UseG1GC","-jar", "app.jar"]