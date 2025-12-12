#!/bin/bash

ROOT_PATH="/home/ec2-user/app/step2/zip"
# jar 파일 경로를 와일드카드로 잡거나 정확한 이름으로 지정
JAR_NAME=$(ls $ROOT_PATH/build/libs/*.jar | tail -n 1)

echo "> 새 애플리케이션 배포"
echo "> JAR Name: $JAR_NAME"

echo "> $JAR_NAME 에 실행권한 추가"
chmod +x $JAR_NAME

echo "> 애플리케이션 실행"
# nohup을 써야 터미널이 끊겨도 서버가 계속 돕니다
nohup java -jar $JAR_NAME > $ROOT_PATH/nohup.out 2>&1 &