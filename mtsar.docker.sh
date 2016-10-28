#!/bin/sh -e

# This script is intended to be runned in the Docker environment.
# Otherwise, you do not really have to care about it.

if [ -z "$POSTGRES_URL" ]; then
    POSTGRES_URL='postgres:5432'
fi

if [ -z "$POSTGRES_DATABASE" ]; then
    POSTGRES_DATABASE='mtsar'
fi

if [ -z "$POSTGRES_USER" ]; then
    POSTGRES_USER='mtsar'
fi

if [ -z "$POSTGRES_PASSWORD" ]; then
    POSTGRES_PASSWORD='mtsar'
fi

MTSAR_ROOT="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd -P)"

cat >mtsar.docker.yml <<YAML
# Beware! This file has been automatically generated by $0 at `date`.
database:
  driverClass: org.postgresql.Driver
  url: jdbc:postgresql://$POSTGRES_URL/$POSTGRES_DATABASE
  user: $POSTGRES_USER
  password: $POSTGRES_PASSWORD
  properties:
    charSet: UTF-8
  maxWaitForConnection: 1s
  validationQuery: "/* Mechanical Tsar Health Check */ SELECT 1"
  minSize: 8
  maxSize: 32
  checkConnectionWhileIdle: true
  evictionInterval: 10s
  minIdleTime: 1 minute
logging:
  level: INFO
  appenders:
    - type: file
      currentLogFilename: $MTSAR_ROOT/log/mtsar.log
      threshold: ALL
      archive: true
      archivedLogFilenamePattern: $MTSAR_ROOT/log/mtsar-%d.log
      archivedFileCount: 5
      timeZone: UTC
YAML

if [ -z "$1" ]; then
    exec java -jar mtsar.jar server mtsar.docker.yml 2>>$MTSAR_ROOT/log/stderr.log >>$MTSAR_ROOT/log/stdout.log
else
    exec java -jar mtsar.jar $@ mtsar.docker.yml
fi
