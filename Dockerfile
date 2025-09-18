FROM maven:3-eclipse-temurin-17 AS builder
# Multi stage build - https://docs.docker.com/engine/userguide/eng-image/multistage-build/

# install maven and unzip
RUN apt-get update && apt-get install -y --no-install-recommends unzip

# prepare folders
RUN mkdir /build && mkdir /target
COPY ./ /build/

# build and extract deegree
RUN cd /build/ && \
  mvn clean install -B -DskipTests && \
  cp /build/deegree-ogcapi-webapp/deegree-ogcapi-webapp-postgres/target/deegree-ogcapi-webapp-postgres-*.war /build/deegree-ogcapi.war && \
  unzip -o /build/deegree-ogcapi.war -d /target

# add to image...
FROM tomcat:10-jdk17-temurin-noble

ENV LANG=en_US.UTF-8

# add build info labels that can be set on build
# see also https://github.com/opencontainers/image-spec/blob/master/annotations.md
ARG BUILD_DATE
ARG VCS_REF
ARG VCS_URL
LABEL org.opencontainers.image.created=$BUILD_DATE \
  org.opencontainers.image.source=$VCS_URL \
  org.opencontainers.image.revision=$VCS_REF

# copy webapp
COPY --from=builder /target /webapp

# default folder for workspace root
ENV DEEGREE_WORKSPACE_ROOT=/workspaces

# create default workspace root and delete any existing ROOT webapp
RUN mkdir /workspaces && \
  (rm -r /usr/local/tomcat/webapps/ROOT || true)

# copy health check script and make it executable
COPY ./docker/ /docker-scripts/
RUN chmod a+x /docker-scripts/*.sh

# health check (get dataset list)
HEALTHCHECK \
  --interval=60s \
  --timeout=15s \
  --start-period=2m \
  --retries=3 \
  CMD /docker-scripts/liveness-check.sh

# context path to deploy webapp at; defaults to ROOT, can be overridden for container
ENV DEEGREE_CONTEXT_PATH=ROOT

# API key to use; if empty will not change API key
ENV DEEGREE_API_KEY=

# tomcat port
EXPOSE 8080

# Good article on possibilities to control context path:
# https://octopus.com/blog/defining-tomcat-context-paths
CMD (rm /usr/local/tomcat/conf/Catalina/localhost/* || true) \
  && mkdir -p /usr/local/tomcat/conf/Catalina/localhost \
  && echo '<Context docBase="/webapp"/>' > /usr/local/tomcat/conf/Catalina/localhost/$DEEGREE_CONTEXT_PATH.xml \
  && ([ -z "$DEEGREE_API_KEY" ] || (echo $DEEGREE_API_KEY > $DEEGREE_WORKSPACE_ROOT/config.apikey)) \
  && /usr/local/tomcat/bin/catalina.sh run
