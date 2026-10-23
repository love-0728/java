# AGENTS.md — 给 AI 助手的快速上下文（先读这个，避免重复摸索）

> 本文件是持久记忆：这台电脑每次重置后，AI 会话记忆会丢失，但本文件随项目保留。
> **每次接手本项目，先读完本文件再动手，不要重新探索。**
> 最后更新：2026-10-12

## 一、这是什么项目（30 秒版）

Hello Kitty 主题家族网站，**Node.js 全栈**（不是 Java/Spring）：

- 路径：`C:\Users\Administrator\Desktop\java-main\hello-kitty-family-site\`
- 前端：纯静态 HTML（注册/登录/家族/过渡页），fetch 相对路径 `/api/*`
- 后端：`server.js` = Express + nodemailer，端口 **3000**
- 数据：`users.json` 落盘（无数据库！不要找 MySQL）
- 邮件：没配 `.env` 时自动用 Ethereal 演示邮箱（验证码只在控制台打预览链接，不真发）

## 二、怎么启动（重置后每次都要这样）

**用户侧：双击 `启动项目.bat`（会自动装依赖→起服务→开浏览器）。**

AI 侧手动步骤（bat 做的就是这些）：

```bash
cd "C:/Users/Administrator/Desktop/java-main/hello-kitty-family-site"
# 电脑重置后 node_modules 会被删掉，先检查再装：
ls node_modules/express node_modules/nodemailer node_modules/dotenv 2>/dev/null
npm install --prefer-offline --no-audit --no-fund   # 缺就装（npm 缓存在 AppData\Local\npm-cache）
node server.js                                       # 启动，端口 3000
```

验证：`curl -s --noproxy "*" http://localhost:3000/api/login -H "Content-Type: application/json" -d '{"username":"x","password":"x"}'`
（**注意必须加 `--noproxy "*"`**，否则 curl 走代理返回 502，会误判服务挂了）

停止：双击 `停止项目.bat`，或 kill 占用 3000 端口的 PID。

## 三、电脑重置规则（重要！）

- 每次开机重置会**删除 node_modules 和 target 文件夹**，其它文件（源码、users.json、本文件）保留
- 所以：任何脚本/流程**不能假设 node_modules 存在**，必须先检查再安装
- npm 缓存（`C:\Users\Administrator\AppData\Local\npm-cache`）目前是保留的，离线 `npm install --prefer-offline` 可用
- 2026-10-12 曾发现 node_modules 处于半删除状态（express 在、dotenv 没了），所以脚本要**逐个检查**三个关键包，不能只查一个

## 四、2026-10-12 我修了什么

1. **`server.js` 第 6 行 `require('dotenv')` 崩溃**：package.json 从未声明 dotenv，重装依赖也救不了。已修：
   - dotenv 加入 package.json 并安装
   - `require('dotenv')` 改为 try/catch 容错（依赖再残缺也能启动）
2. 新增 `启动项目.bat` / `停止项目.bat`（GBK 编码、相对路径 `%~dp0`、不依赖 node_modules）
3. 新增本文件 AGENTS.md
4. **修复启动脚本闪退**：第一版 bat 是 LF（Unix）行尾，Windows cmd 下 `goto` 找不到标签 → 批处理直接中止退出。已重新生成为 **CRLF 行尾 + GBK 编码**，并加 `chcp 936` 保险行。两个脚本均已字节级校验（CRLF 无残留、GBK 可解码、无危险尾字节）

## 四点五、生成 .bat 的铁律（AI 必读，防闪退）

给这台电脑写任何 .bat 脚本时**必须同时满足**：

1. **CRLF 行尾**（LF 会让 goto/标签失效 → 闪退）。生成后用 python 校验：`d.count(b'\r\n') == d.count(b'\n')`
2. **GBK 编码**（不是 UTF-8！）。用 python：`text.replace('\n','\r\n').encode('gbk')` 写入
3. 中文文案里不能出现 GBK 尾字节为 `0x5E(^)` `0x7C(|)` 的字 → 会被 cmd 当特殊符号，整行语法崩
4. 脚本开头加 `chcp 936 >nul` 防系统代码页被改成 UTF-8

## 五、API 一览（都在 3000 端口，返回 `{ok, msg}` 风格）

- `POST /api/send-code` `{email}` — 发注册验证码（5 分钟有效）
- `POST /api/register` `{username,email,code,password}` — 用户名 3-20 位字母数字下划线，密码≥6
- `POST /api/login` `{username,password}` — 用户名或邮箱均可登录
- `POST /api/forgot-code` `{email}` — 发重置密码验证码
- `POST /api/reset-password` `{email,code,password}`
- 页面：`/hello-kitty-register.html`（注册/根路径）、`/hello-kitty-login.html`、`/home.html`（登录后首页：导航栏+搜索栏，纯前端）、`/family.html`、`/transition.html`（穿越动画，重定向到 home.html）、`/welcome.html`
- **登录跳转链**：`login → transition.html（动画）→ home.html?user=...`；`welcome.html` 的"进入主页"也指向 `home.html`

## 六、容易踩的坑

| 坑 | 说明 |
|---|---|
| curl 502 | 沙箱 curl 走代理，必须 `--noproxy "*"` |
| 后台 node 被回收 | bash 会话结束后台 node 会被杀，测试用 run_in_background 或让用户跑 bat |
| `Desktop\AA\222` | 是本项目的**旧副本**（早期版本），别在那上面干活；那边说明.txt 描述的 Spring Boot 后端从未存在过 |
| `D:\workSpace\springBootTest` | 用户另一个 Spring Boot 学习项目（端口 8081），与本站无关，别混淆 |
| node 路径 | 系统 node 在 `D:\Program Files\nodejs`，PATH 里有，`where node` 可查 |
| 邮箱验证码 | 未配 `.env` 时走 Ethereal，验证码看服务控制台输出的预览链接 |

## 七、配置真实发信（用户想做时）

复制 `.env.example` → `.env`，填 SMTP（QQ 邮箱：smtp.qq.com / 465 / 授权码），重启即可。当前 `.env` 不存在 = 演示模式。

## 八、修改本项目后

- 改了 server.js / 前端 → 重启：`停止项目.bat` 再 `启动项目.bat`
- 改了依赖 → 重跑 `npm install --save <包>`
- **有新发现/新坑 → 更新本文件对应小节**，这是唯一的跨会话记忆
