FROM  tomcat:9-jdk11-adoptopenjdk-hotspot

ADD ./target/demo-0.0.1-SNAPSHOT.war $CATALINA_HOME/webapps/ROOT.war

RUN mkdir /usr/local/tomcat/conf/ssl/
RUN sed -i "/<\/Service>/ i \
<Connector port=\"8443\" protocol=\"org.apache.coyote.http11.Http11NioProtocol\" \n\
          maxThreads=\"150\" SSLEnabled=\"true\" scheme=\"https\" secure=\"true\" \n\
          clientAuth=\"false\" sslProtocol=\"TLS\" \n\
          keystoreFile=\"\${catalina.home}/conf/ssl/server.p12\" keystoreType=\"pkcs12\" keystorePass=\"changeit\" \n\
/>" $CATALINA_HOME/conf/server.xml

CMD ["catalina.sh", "run"]