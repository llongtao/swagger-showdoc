FROM 10.2.47.234:8888/flydiy-base/flydiy-oracle-8-jre-alpine:20200707_02
ENV APP_NAME=app.jar
COPY swagger-showdoc-server-1.1.0-RELEASE.jar /flydiy/app.jar
