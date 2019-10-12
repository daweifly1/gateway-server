FROM 10.100.2.92:5000/f00lish/centos7_jdk8_base:v1
VOLUME /tmp
WORKDIR /home
ADD  ./target/gateway-server-0.0.3-SNAPSHOT.jar app.jar

ENTRYPOINT exec java -server -Xms1024m -Xmx1024m -Djava.security.egd=file:/dev/./urandom $JAVA_OPTS -jar app.jar  --server.session.timeout=300 --spring.cloud.config.profile=dev

EXPOSE 9003
