FROM openjdk:11-jre
COPY target/rsstotwitter-0.0.1-SNAPSHOT.jar /opt/rsstotwitter-0.0.1-SNAPSHOT.jar
CMD ["java","-XshowSettings:vm", "-XX:+PrintCommandLineFlags", "-jar","/opt/rsstotwitter-0.0.1-SNAPSHOT.jar", "--spring.config.location=/opt/rsstotwitter/conf/rsstotwitter.properties"]
