FROM 639855326882.dkr.ecr.eu-west-1.amazonaws.com/docker.io/library/maven:3.9.8-eclipse-temurin-21-alpine AS build

WORKDIR /app
COPY src ./src
COPY pom.xml settings2.xml ./
RUN mvn package --settings settings2.xml

# runtime image
FROM 639855326882.dkr.ecr.eu-west-1.amazonaws.com/docker.io/library/eclipse-temurin:21.0.4_7-jre-alpine as release

COPY --from=build /app/target/*.jar /app/application.jar
ENV JVM_OPTS="-XshowSettings:vm \
              -XX:+PrintFlagsFinal \
              -XX:MinRAMPercentage=30.0 \
			  -XX:MaxRAMPercentage=95.0 \
			  -XX:+ShowCodeDetailsInExceptionMessages \
			  -XX:+AlwaysActAsServerClassMachine \
              -XX:+UseStringDeduplication \
              -XX:+HeapDumpOnOutOfMemoryError"

CMD java $JVM_OPTS -jar /app/application.jar