FROM postgres:alpine3.18

COPY ./PL8S/src/main/database/festival-schema.sql /docker-entrypoint-initdb.d/init.sql
COPY ./PL8S/src/main/database/festival-insert.sql /docker-entrypoint-initdb.d/insert.sql
COPY ./PL8S/src/main/database/festival-query.sql /docker-entrypoint-initdb.d/query.sql
COPY ./PL8S/src/main/database/users.sql /docker-entrypoint-initdb.d/users.sql

# ARG UID=1080
# ARG GID=1080

# RUN addgroup -g "${GID}" pl8sdbadmin
# RUN adduser -u "${UID}" -G pl8sdbadmin -D pl8sdbadmin

# USER pl8sdbadmin

USER root

EXPOSE 5432
