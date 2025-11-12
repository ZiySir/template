 #!/bin/bash
read -p "是否进行项目初始化?(y/n): " confirm

if [[ $confirm != [yY] ]]; then
  exit 0;
fi

read -p "group: " group
export GROUP=$group
envsubst < .project/init/build.gradle.template > build.gradle

read -p "是否创建代码仓库?(y/n): " confirm

if [[ $confirm == [yY] ]]; then

  read -p "请输入代码仓库地址: " repoUrl
  rm -rf .git && git init && git remote add origin $repoUrl
fi


if ! ./gradlew cleanIdea baselineUpdateConfig idea dependencies; then
  echo "gradle 初始化失败" >&2
  exit 1
fi

# 清空999_palantir.txt文件 如果存在
if [ -f ".baseline/copyright/999_palantir.txt" ]; then
  echo "" > .baseline/copyright/999_palantir.txt
fi
