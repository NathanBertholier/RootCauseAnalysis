FROM maven:3.8.4-openjdk-17

COPY rootcausecore ./rootcausecore
COPY rootcausefront ./rootcausefront
COPY pom.xml ./

RUN mvn package

EXPOSE 8080

ENTRYPOINT ["java","-jar","rootcausecore/backend/target/quarkus-app/quarkus-run.jar"]
