@echo off
title 一键启动 java-main（springBootTest + family-site）
cd /d %~dp0

REM --- MySQL 客户端（系统路径，绝对必要，非项目相对路径）---
set MYSQL="C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
if not exist %MYSQL% set MYSQL=mysql.exe
set ROOTPASS=123456
set SQLFILE=%~dp0springBootTest\sql\kitty_db.sql
set HOMESQL=%~dp0springBootTest\sql\home.sql

echo ============================================
echo  步骤 1/4  检查 MySQL 服务（端口 3306）
echo ============================================
netstat -an | findstr ":3306" >nul
if errorlevel 1 (
  echo MySQL 未运行，尝试启动服务...
  net start MySQL80 2>nul || net start MySQL 2>nul || echo [警告] 自动启动失败，请手动启动 MySQL 后重试
  timeout /t 6 >nul
)

echo ============================================
echo  步骤 2/4  初始化数据库 kitty_db（幂等，可重复执行）
echo ============================================
%MYSQL% -h localhost -u root -p%ROOTPASS% < "%SQLFILE%" 2>nul
%MYSQL% -h localhost -u root -p%ROOTPASS% < "%HOMESQL%" 2>nul
%MYSQL% -h localhost -u root -p%ROOTPASS% -e "CREATE USER IF NOT EXISTS 'APT'@'localhost' IDENTIFIED BY '123456'; GRANT ALL PRIVILEGES ON kitty_db.* TO 'APT'@'localhost'; FLUSH PRIVILEGES;" 2>nul
echo 数据库已就绪（库 kitty_db，用户 APT/123456；含首页表 home_category/home_item）。

echo ============================================
echo  步骤 3/4  启动 Spring Boot 后端（端口 8081）
echo            target 被删，mvnw 会重新编译
echo ============================================
start "SpringBootTest-8081" cmd /k "chcp 65001 >nul && cd /d %~dp0springBootTest && mvnw spring-boot:run"

echo ============================================
echo  步骤 4/4  启动 Node 注册后端（端口 3000）
echo            node_modules 被删，先 npm install
echo ============================================
start "KittyFamilySite-3000" cmd /k "chcp 65001 >nul && cd /d %~dp0hello-kitty-family-site && npm install && npm start"

echo.
echo 全部已启动，请等待各窗口输出启动日志...
echo   Spring Boot 后端: http://localhost:8081
echo   Node 站点:        http://localhost:3000
echo.
pause
