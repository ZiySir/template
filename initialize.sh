#!/bin/bash
read -p "是否进行项目初始化?(y/n): " confirm

if [[ $confirm != [yY] ]]; then
  exit 0;
fi

read -p "是否创建代码仓库?(y/n): " confirm

if [[ $confirm == [yY] ]]; then

  read -p "请输入代码仓库地址: " repoUrl
  rm -rf .git && git init && git remote add origin $repoUrl
fi


./gradlew cleanIdea && \
rm -rf .idea && \
./gradlew baselineUpdateConfig && \
./gradlew idea && \
./gradlew dependencies