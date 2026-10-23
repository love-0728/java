@echo off
chcp 936 >nul
rem ============================================================
rem  Hello Kitty 家族站 停止脚本（暂停服务）
rem  说明：只结束占用 3000 端口的后端进程，不影响其它程序。
rem ============================================================
title Hello Kitty 项目停止
set FOUND=0
for /f "tokens=5" %%p in ('netstat -ano ^| findstr ":3000" ^| findstr "LISTENING"') do (
    echo 正在停止后端进程 PID=%%p ...
    taskkill /F /PID %%p >nul 2>nul
    set FOUND=1
)
if "%FOUND%"=="0" (
    echo 后端当前没有在运行
) else (
    echo 已停止。
)
echo 提示：需要再次启动请运行 启动项目.bat
pause
