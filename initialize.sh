#!/bin/bash
read -p "是否进行项目初始化?(y/n): " confirm

if [[ $confirm != [yY] ]]; then
  exit 0;
fi

./gradlew cleanIdea
# 运行必要的初始化
./gradlew baselineUpdateConfig
./gradlew idea
