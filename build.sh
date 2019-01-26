mvn clean install -P mars-db,mars-mail-server;
docker build -t mars-server:build ./docker/mars-server/;
docker build -t mars-db:build ./docker/mars-db/;
docker build -t mars-web:build ./docker/mars-web/;