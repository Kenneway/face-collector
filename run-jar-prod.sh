#! /usr/bin/env bash

# mvn clean install

# java -jar target/face-collector-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod

java \
-Djava.library.path=/home/czj/4-packages/opencv/versions/backup/opencv-2.4.13.6/build/lib \
-Dspring.profiles.active=dev \
-jar face-collector-0.0.1-SNAPSHOT.jar 
