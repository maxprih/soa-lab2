FROM openjdk:21
COPY build/libs/soa-lab2-oscar-1.0-SNAPSHOT.jar soa-lab2-oscar-1.0-SNAPSHOT.jar

COPY deployment/oscar-service.jks oscar-service.jks
COPY deployment/movie-service.crt movie-service.crt
RUN keytool -import -trustcacerts -alias movie-service -file movie-service.crt -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit -noprompt

ENTRYPOINT ["java", "-jar", "soa-lab2-oscar-1.0-SNAPSHOT.jar"]