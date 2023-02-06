FROM eclipse-temurin:17
RUN echo "networkaddress.cache.ttl=60" >> /opt/java/openjdk/conf/security/java.security
COPY target/rsstotwitter-0.0.1-SNAPSHOT.jar /opt/rsstotwitter-0.0.1-SNAPSHOT.jar
CMD ["java","-XshowSettings:vm", "-XX:+PrintCommandLineFlags", "-jar","/opt/rsstotwitter-0.0.1-SNAPSHOT.jar", "--spring.config.location=/opt/rsstotwitter/conf/rsstotwitter.properties"]
