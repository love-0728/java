@echo off
chcp 936 >nul
rem ============================================================
rem  Hello Kitty 家族站 一键启动脚本
rem  说明：电脑每次重置后 node_modules 会被删除，本脚本不依赖
rem        node_modules，发现依赖缺失会自动先安装再启动。
rem  用法：双击本文件即可（脚本内全部使用相对路径）
rem ============================================================
cd /d "%~dp0"
title Hello Kitty 项目启动

echo ============================================
echo    Hello Kitty 家族站 一键启动
echo ============================================

rem ---- 检查 node 是否可用（系统级依赖，与 node_modules 无关）----
where node >nul 2>nul
if errorlevel 1 (
    echo [错误] 找不到 node 命令，请先安装 Node.js
    pause
    exit /b 1
)

rem ---- 检查关键依赖是否缺失（重置后 node_modules 会没了，属正常现象）----
set NEED_INSTALL=0
for %%p in (express nodemailer dotenv) do if not exist "node_modules\%%p" set NEED_INSTALL=1

if "%NEED_INSTALL%"=="1" (
    echo [1/3] 依赖缺失（电脑重置后正常），正在安装，请稍候...
    call npm install --prefer-offline --no-audit --no-fund
    if errorlevel 1 (
        echo [提示] 缓存安装失败，尝试联网安装...
        call npm install --no-audit --no-fund
        if errorlevel 1 (
            echo [错误] 依赖安装失败：请检查网络后重新运行本脚本
            pause
            exit /b 1
        )
    )
) else (
    echo [1/3] 依赖已就绪，跳过安装
)

rem ---- 已经在跑就不再重复启动 ----
netstat -ano | findstr ":3000" | findstr "LISTENING" >nul
if not errorlevel 1 (
    echo [2/3] 服务已经在运行了
    goto ready
)

echo [2/3] 正在后台启动服务（窗口标题 kitty-server）...
start "kitty-server" /min node server.js

rem ---- 最多等 20 秒直到端口 3000 就绪 ----
set /a tries=0
:wait
timeout /t 1 /nobreak >nul
netstat -ano | findstr ":3000" | findstr "LISTENING" >nul
if not errorlevel 1 goto ready
set /a tries+=1
if %tries% lss 20 goto wait
echo [警告] 等待超时，服务可能没起来，请打开 kitty-server 窗口查看报错
goto end

:ready
echo [3/3] 启动完成，正在打开浏览器...
start "" http://localhost:3000/hello-kitty-register.html

:end
echo.
echo    注册页： http://localhost:3000/hello-kitty-register.html
echo    登录页： http://localhost:3000/hello-kitty-login.html
echo    家族页： http://localhost:3000/family.html
echo.
echo    停止服务请运行： 停止项目.bat
pause
