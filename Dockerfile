FROM openjdk:11-jre
RUN echo "networkaddress.cache.ttl=60" >> /usr/local/openjdk-11/conf/security/java.security
COPY target/rsstotwitter-0.0.1-SNAPSHOT.jar /opt/rsstotwitter-0.0.1-SNAPSHOT.jar
CMD ["java","-XshowSettings:vm", "-XX:+PrintCommandLineFlags", "-jar","/opt/rsstotwitter-0.0.1-SNAPSHOT.jar", "--spring.config.location=/opt/rsstotwitter/conf/rsstotwitter.properties"]
