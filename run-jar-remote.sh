#! /usr/bin/env bash

REMOTE_IP=172.18.2.107
REMOTE_USER=lab

scp target/face-collector-0.0.1-SNAPSHOT.jar ${REMOTE_USER}@${REMOTE_IP}:/data/face-collector
ssh ${REMOTE_USER}@${REMOTE_IP} << remotessh 
cd /data/face-collector
ls
exit
remotessh


















