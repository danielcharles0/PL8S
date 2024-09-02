FROM dpage/pgadmin4:8.7

COPY ./services/pl8spgadmin/servers.json /pgadmin4/servers.json

# ARG UID=1080
# ARG GID=1080

# USER root

# RUN groupadd -g "${GID}" pl8spgadmin
# RUN useradd -u "${UID}" -g "${GID}" pl8spgadmin

# RUN chown -R pl8spgadmin:pl8spgadmin /var/lib/pgadmin /pgadmin4

# USER pl8spgadmin

USER root

EXPOSE 80
