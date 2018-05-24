#! /usr/bin/env bash

scp target/face-collector-0.0.1-SNAPSHOT.jar lab@172.18.2.107:/data/face-collector

cp target/face-collector-0.0.1-SNAPSHOT.jar ./

ssh lab@172.18.2.107 "cd /data/face-collector"

