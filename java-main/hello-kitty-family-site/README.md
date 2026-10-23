# Hello Kitty 家族站 · 注册/登录/找回 全套

一套粉色 Hello Kitty 风格的家族站点，含：
- `family.html` 家族成员页（含登录态识别 + 退出登录）
- `hello-kitty-login.html` 登录页（支持用户名/邮箱登录、忘记密码弹窗）
- `hello-kitty-register.html` 注册页（邮箱验证码、密码显隐、表单校验）
- `transition.html` / `welcome.html` 过渡页
- `server.js` + `config.js` 轻量 Node 后端（发信 + 账号系统）

## 一键启动

项目已自带 `node_modules`（express / nodemailer / dotenv），无需联网安装。

```bash
cd 项目目录
node server.js
```

启动后访问：
- 注册页： http://localhost:3000/hello-kitty-register.html
- 登录页： http://localhost:3000/hello-kitty-login.html
- 家族页： http://localhost:3000/family.html

## 开启「真实发信」（重要）

默认是 Ethereal 演示邮箱，验证码**不会真发到你邮箱**，只在服务端日志打印预览地址。
要真正发信，按下面做：

1. 复制 `.env.example` 为 `.env`：
   ```bash
   cp .env.example .env
   ```
2. 编辑 `.env`，填入你的邮箱 SMTP（以 QQ 邮箱为例）：
   ```
   SMTP_HOST=smtp.qq.com
   SMTP_PORT=465
   SMTP_SECURE=true
   SMTP_USER=你的QQ邮箱@qq.com
   SMTP_PASS=你的QQ邮箱授权码   # 不是登录密码！在 QQ 邮箱设置→账户→开启POP3/SMTP 后获取
   ```
3. 重启 `node server.js`，控制台出现 `[发信后端] 已使用配置的 SMTP：smtp.qq.com` 即生效。
4. 在注册页点「获取验证码」，验证码会**真发到你填的邮箱**。

> 其它邮箱：163 用 `smtp.163.com`；Gmail 用 `smtp.gmail.com`（需应用专用密码）。

## 接口一览

- `POST /api/send-code`     注册发验证码 `{email}`
- `POST /api/register`      注册 `{username,email,code,password}`
- `POST /api/login`         登录（用户名或邮箱）`{username,password}`
- `POST /api/forgot-code`   发重置验证码 `{email}`
- `POST /api/reset-password`重置密码 `{email,code,password}`

账号保存在 `users.json`（演示用，密码 sha256 存储）。
