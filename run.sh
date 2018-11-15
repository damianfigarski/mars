docker stop $(docker ps -aq);
docker rm $(docker ps -aq);
docker build -t mars . &&
docker run -i -t -d -p 8080:8080 -p 8000:8000 mars;