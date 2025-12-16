FROM eclipse-temurin:21-jre-alpine

COPY ./build/libs/defi*.jar app.jar

ENV TZ Europe/Moscow

ENTRYPOINT java -jar app.jar -Dlog4j2.formatMsgNoLookups=true