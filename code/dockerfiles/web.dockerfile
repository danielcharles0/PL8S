FROM openjdk:17-jdk-slim AS binary

RUN apt update

RUN apt install -y gpg curl

WORKDIR /home

RUN curl -O https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz

# CHECKSUM
RUN curl -O https://downloads.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz.sha512

# SIGNATURE
RUN curl -O https://downloads.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz.asc

# PUBLIC KEYS
RUN curl -O https://downloads.apache.org/maven/KEYS

# CHECKING CHECKSUM ( if any command return a non-zero code the build will fail )
RUN echo "$(cat apache-maven-3.9.6-bin.tar.gz.sha512) apache-maven-3.9.6-bin.tar.gz" | sha512sum --check --status

# RECEIVE THE PUBLIC KEY
RUN gpg --import KEYS

# VERIFY THE SIGNATURE
RUN gpg --verify apache-maven-3.9.6-bin.tar.gz.asc apache-maven-3.9.6-bin.tar.gz

# EXTRACT
RUN tar xzvf apache-maven-3.9.6-bin.tar.gz

FROM openjdk:17-jdk-slim AS maven

COPY --from=binary /home/apache-maven-3.9.6 /opt/apache-maven-3.9.6

# MAVEN SETTINGS
COPY ./services/pl8sweb/settings.xml /root/.m2/settings.xml

ENV PATH="$PATH:/opt/apache-maven-3.9.6/bin"

WORKDIR /home

FROM maven AS build

WORKDIR /home/PL8S

COPY ./PL8S/pom.xml ./pom.xml

COPY ./PL8S/src/main/webapp/WEB-INF/web.xml ./src/main/webapp/WEB-INF/web.xml

# To download the dependencies just once
# reference: https://stackoverflow.com/questions/8563960/maven-command-to-update-repository-after-adding-dependency-to-pom
RUN mvn install

COPY ./PL8S/src/main/resources ./src/main/resources

COPY ./PL8S/src/main/webapp ./src/main/webapp

COPY ./PL8S/src/main/java ./src/main/java

# BUILD
RUN mvn clean package

FROM tomcat:10.1.20

# Just for development
# reference: https://octopus.com/blog/deployable-tomcat-docker-containers
# RUN cp -r /usr/local/tomcat/webapps.dist/manager /usr/local/tomcat/webapps/manager
# COPY ./services/pl8sweb/tomcat-users.xml /usr/local/tomcat/conf/tomcat-users.xml
# COPY ./services/pl8sweb/context.xml /usr/local/tomcat/webapps/manager/META-INF/context.xml
# End just for development

COPY --from=build /home/PL8S/target/PL8S-1.0.war /usr/local/tomcat/webapps/pl8s.war

WORKDIR /usr/local/tomcat/webapps/pl8s

RUN jar -xf ../pl8s.war
RUN rm ../pl8s.war

WORKDIR /usr/local/tomcat

RUN ln -s /usr/local/tomcat/webapps/pl8s /usr/local/tomcat/webapps/ROOT

EXPOSE 8080
