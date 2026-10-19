FROM maven:3.9.11-eclipse-temurin-21 AS compilacao
WORKDIR /projeto
COPY pom.xml ./
RUN mvn -B -ntp dependency:go-offline
COPY src ./src
RUN mvn -B -ntp -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /aplicacao
RUN groupadd --system academico && useradd --system --gid academico academico
COPY --from=compilacao --chown=academico:academico /projeto/target/gestao-academica-1.0.0-beta.1.jar aplicacao.jar
USER academico
ENV SERVER_ADDRESS=0.0.0.0 APP_DEMO=true
EXPOSE 8080
ENTRYPOINT ["java","-jar","aplicacao.jar"]
