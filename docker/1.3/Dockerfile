# This file is available under the following license:
# under LGPL 2.1 (LICENSE.TXT) Copyright 2020 Torsten Friebe <tfr@users.sourceforge.net>
FROM tomcat:9.0-jdk11-temurin-jammy

ENV LANG en_US.UTF-8

# add build info labels that can be set on build
# see also https://github.com/opencontainers/image-spec/blob/master/annotations.md
ARG BUILD_DATE
ARG VCS_REF
ARG VCS_URL
LABEL org.opencontainers.image.created=$BUILD_DATE \
  org.opencontainers.image.source=$VCS_URL \
  org.opencontainers.image.revision=$VCS_REF
LABEL maintainer="deegree TMC <tmc@deegree.org>"

# set deegree OAF version
ENV DEEGREE_OAF_VERSION=1.3-SNAPSHOT

# tomcat port
EXPOSE 8080

# add deegree OGCAPI webapp
RUN curl https://repo.deegree.org/repository/public-ogcapi/org/deegree/deegree-ogcapi-webapp-postgres/${DEEGREE_OAF_VERSION}/deegree-ogcapi-webapp-postgres-${DEEGREE_OAF_VERSION}.war -o /usr/local/tomcat/webapps/deegree-ogcapi.war

# run tomcat
CMD ["catalina.sh", "run"]