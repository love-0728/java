const express = require('express');
const crypto = require('crypto');
const fs = require('fs');
const path = require('path');

const app = express();
app.use(express.json());
app.use(express.static(__dirname)); // 托管整个静态站点（注册页/登录页等）

const PORT = process.env.PORT || 3000;

// ---------- 用户存储（演示用，落盘到 users.json） ----------
const USERS_FILE = path.join(__dirname, 'users.json');
let users = [];
try { users = JSON.parse(fs.readFileSync(USERS_FILE, 'utf8')); } catch (e) { users = []; }
function saveUsers() {
  fs.writeFileSync(USERS_FILE, JSON.stringify(users, null, 2));
}

// ---------- 图形验证码存储（内存，5 分钟有效、一次性） ----------
const captchas = new Map(); // captchaId -> { code, expire }

// ---------- 工具函数 ----------
function hashPwd(p) { return crypto.createHash('sha256').update(p).digest('hex'); }

// ---------- 图形验证码 ----------
// 生成 4 位验证码（去掉了易混淆的 I O 0 1），SVG 图片 base64 返回，零依赖
const CAPTCHA_CHARS = 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789';
function genCaptcha() {
  let code = '';
  for (let i = 0; i < 4; i++) code += CAPTCHA_CHARS[Math.floor(Math.random() * CAPTCHA_CHARS.length)];

  const id = crypto.randomUUID();
  captchas.set(id, { code, expire: Date.now() + 5 * 60 * 1000 });

  // 顺手清理过期验证码
  const now = Date.now();
  for (const [k, v] of captchas) if (now > v.expire) captchas.delete(k);

  const colors = ['#e75a92', '#e8404f', '#d67ba5', '#c26a9a', '#ef8ab0'];
  const pick = () => colors[Math.floor(Math.random() * colors.length)];

  let texts = '';
  for (let i = 0; i < 4; i++) {
    const x = 16 + i * 27 + (Math.random() * 6 - 3);
    const y = 31 + (Math.random() * 6 - 3);
    const rot = Math.floor(Math.random() * 30 - 15);
    texts += `<text x="${x.toFixed(1)}" y="${y.toFixed(1)}" font-size="24" font-weight="bold" `
          +  `font-family="Arial,sans-serif" fill="${pick()}" `
          +  `transform="rotate(${rot} ${x.toFixed(1)} ${y.toFixed(1)})">${code[i]}</text>`;
  }
  let lines = '';
  for (let i = 0; i < 5; i++) {
    lines += `<line x1="${(Math.random() * 120).toFixed(0)}" y1="${(Math.random() * 44).toFixed(0)}" `
          +  `x2="${(Math.random() * 120).toFixed(0)}" y2="${(Math.random() * 44).toFixed(0)}" `
          +  `stroke="${pick()}" stroke-width="1" opacity="0.45"/>`;
  }
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="120" height="44" viewBox="0 0 120 44">`
            + `<rect width="120" height="44" rx="8" fill="#fff5f8"/>${lines}${texts}</svg>`;
  const image = 'data:image/svg+xml;base64,' + Buffer.from(svg).toString('base64');
  return { captchaId: id, image };
}

// 校验并消费验证码（一次性，无论对错都作废，防重放；忽略大小写）
function verifyCaptcha(id, input) {
  if (!id || !input) return false;
  const rec = captchas.get(id);
  captchas.delete(id);
  if (!rec) return false;
  if (Date.now() > rec.expire) return false;
  return rec.code.toLowerCase() === String(input).trim().toLowerCase();
}

// GET /api/captcha → {ok, captchaId, image}
app.get('/api/captcha', (req, res) => {
  res.json({ ok: true, ...genCaptcha() });
});

// ---------- 注册（账号 + 密码 + 图形验证码） ----------
app.post('/api/register', (req, res) => {
  const body = req.body || {};
  const uname = (body.username || '').trim();
  const pwd = body.password || '';

  if (!uname || !/^[A-Za-z0-9_]+$/.test(uname) || uname.length < 3 || uname.length > 20)
    return res.status(400).json({ ok: false, msg: '用户名格式不正确（3-20 位字母/数字/下划线）' });
  if (!pwd || pwd.length < 6) return res.status(400).json({ ok: false, msg: '密码至少要有 6 位' });

  if (!verifyCaptcha(body.captchaId, body.captchaCode))
    return res.status(400).json({ ok: false, msg: '验证码不正确或已过期' });

  if (users.some(u => u.username === uname))
    return res.status(400).json({ ok: false, msg: '该用户名已被注册' });

  users.push({ username: uname, password: hashPwd(pwd), joined: Date.now() });
  saveUsers();

  res.json({ ok: true });
});

// ---------- 登录（仅用户名 + 密码） ----------
app.post('/api/login', (req, res) => {
  const body = req.body || {};
  const uname = (body.username || '').trim();
  const pwd = body.password || '';
  if (!uname) return res.status(400).json({ ok: false, msg: '请输入用户名' });
  if (!pwd) return res.status(400).json({ ok: false, msg: '请输入密码' });

  const user = users.find(u => u.username === uname);
  if (!user) return res.status(400).json({ ok: false, msg: '账号不存在，请先注册' });
  if (user.password !== hashPwd(pwd)) return res.status(400).json({ ok: false, msg: '密码不正确' });

  res.json({ ok: true, username: user.username });
});

// 直接访问根路径打开注册页
app.get('/', (req, res) => res.sendFile(path.join(__dirname, 'hello-kitty-register.html')));

// ---------- 启动 ----------
app.listen(PORT, () => {
  console.log('[Kitty后端] 已启动： http://localhost:' + PORT);
  console.log('[Kitty后端] 注册页：   http://localhost:' + PORT + '/hello-kitty-register.html');
  console.log('[Kitty后端] 登录页：   http://localhost:' + PORT + '/hello-kitty-login.html');
});
