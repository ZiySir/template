#!/bin/bash
./gradlew check -x test
./gradlew checkstyleMain -x test

./gradlew spotlessCheck
result=$?
# shellcheck disable=SC2059
printf "the spotlessCheck result code is $result"
if [[ "$result" = 0 ]] ; then
    # shellcheck disable=SC2028
    echo "\033[32m
    ....
    ....
    SpotlessCheck Pass!!
    ....
    ....
    \033[0m"
    exit 0
else
    ./gradlew spotlessApply
    # shellcheck disable=SC2028
    echo "\033[31m
    ....
    ....
    SpotlessCheck Failed!!
    代码格式有问题;
    ....
    已经自动调整格式,review代码后再git add . && git commit
    ....
    ....
    \033[0m"
    exit 1
fi
