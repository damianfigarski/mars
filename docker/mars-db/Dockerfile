FROM postgres:9.6.11-alpine

EXPOSE 5432

COPY init-db.sql /docker-entrypoint-initdb.d
COPY csv /home/csv