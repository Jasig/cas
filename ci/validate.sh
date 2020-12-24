#!/bin/bash

java -jar app/build/libs/app.jar &
pid=$!
sleep 30
mkdir tmp
cd tmp
curl http://localhost:8080/starter.tgz | tar -xzvf -
kill -9 $pid

echo "Running CAS Overlay with bootRun"
./gradlew clean build bootRun --no-daemon -Dserver.ssl.enabled=false -Dserver.port=8090 &> /dev/null &
pid=$!
echo "Launched CAS with pid ${pid} using bootRun. Waiting for CAS server to come online..."
until curl -k -L --output /dev/null --silent --fail http://localhost:8090/cas/login; do
    echo -n '.'
    sleep 5
done
echo -e "\n\nReady!"
kill -9 $pid

echo "Building Overlay"
chmod -R 777 ./*.sh
./gradlew clean build jibDockerBuild --refresh-dependencies --no-daemon

echo "Downloading Shell"
./gradlew downloadShell

echo "Listing Views"
./gradlew listTemplateViews

echo "Configuration Metadata Export"
./gradlew exportConfigMetadata

echo "Exploding WAR"
./gradlew explodeWar


echo "Building Container Image via Spring Boot"
./gradlew bootBuildImage

echo "Build Container Image w/ Docker"
./docker-build.sh

echo "Build Container Image w/ Docker Compose"
docker-compose build
