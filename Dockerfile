FROM maven:3.8.4-openjdk-17

COPY . .

RUN mvn clean package -Dmaven.test.skip

EXPOSE 8080

ENTRYPOINT ["java","-jar","core/target/quarkus-app/quarkus-run.jar"]
