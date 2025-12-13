#!/bin/bash

ROOT_PATH="/home/ec2-user/app/step2/zip"
JAR_FILE="$ROOT_PATH/build/libs/ureca-group1-backend-0.0.1-SNAPSHOT.jar" # 본인 프로젝트 jar 이름으로 수정 필요!

echo "> 현재 구동중인 애플리케이션 pid 확인"
CURRENT_PID=$(pgrep -f "java -jar")

if [ -z "$CURRENT_PID" ]; then
  echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> kill -15 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi