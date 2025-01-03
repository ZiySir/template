@echo off
setlocal

:confirm
set /p confirm=是否进行项目初始化?(y/n):

if /i "%confirm%" neq "y" (
    exit /b 0
)

call gradlew.bat cleanIdea
REM 运行必要的初始化
call gradlew.bat baselineUpdateConfig
call gradlew.bat idea

endlocal