@echo off
title kitty_db Database Viewer
chcp 65001 >nul 2>&1

REM --- MySQL client path (adjust if your install path differs) ---
set MYSQL="C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
if not exist %MYSQL% set MYSQL=mysql.exe

set HOST=localhost
set PORT=3306
set USER=APT
set PASS=123456
set DB=kitty_db

echo ===================================================
echo  kitty_db Database Viewer
echo  Host : %HOST%:%PORT%
echo  User : %USER%
echo  DB   : %DB%
echo  Tip  : type 'exit;' or 'quit;' to leave
echo ===================================================

%MYSQL% -h %HOST% -P %PORT% -u %USER% -p%PASS% --default-character-set=utf8mb4 %DB%

echo.
echo [closed] press any key to close this window
pause >nul
