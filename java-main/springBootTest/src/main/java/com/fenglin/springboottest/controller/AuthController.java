package com.fenglin.springboottest.controller;

import com.fenglin.springboottest.common.Result;
import com.fenglin.springboottest.entity.User;
import com.fenglin.springboottest.service.CaptchaService;
import com.fenglin.springboottest.service.TokenService;
import com.fenglin.springboottest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 登录 / 注册 / 登录态 接口，供前端页面调用
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private CaptchaService captchaService;

    /**
     * 获取图形验证码：GET /api/auth/captcha
     * 返回 captchaId 和 base64 图片（前端 <img> 可直接使用）
     */
    @GetMapping("/captcha")
    public Result<Map<String, String>> captcha() {
        return Result.success("ok", captchaService.generate());
    }

    /**
     * 注册：{"username": "...", "password": "...", "captchaId": "...", "captchaCode": "..."}
     */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        // 先校验验证码（一次性，错了就要换新的）
        if (!captchaService.verify(body.get("captchaId"), body.get("captchaCode"))) {
            return Result.fail("验证码错误或已过期");
        }

        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return Result.fail("用户名和密码不能为空");
        }
        if (username.length() < 3 || username.length() > 12) {
            return Result.fail("用户名需要 3-12 个字符");
        }
        if (password.length() < 6) {
            return Result.fail("密码至少 6 位");
        }
        if (!userService.register(username, password)) {
            return Result.fail("该用户名已被注册，换一个吧");
        }
        return Result.success("注册成功");
    }

    /**
     * 登录：{"username": "...", "password": "..."}
     * 成功返回用户信息和 token
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return Result.fail("用户名和密码不能为空");
        }
        if (userService.findByUsername(username) == null) {
            return Result.fail("用户不存在，请先注册");
        }
        User user = userService.login(username, password);
        if (user == null) {
            return Result.fail("密码错误");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("createTime", user.getCreateTime());
        data.put("token", tokenService.createToken(username));
        return Result.success("登录成功", data);
    }

    /**
     * 校验登录态：GET /api/auth/me?token=...
     * 有效返回用户名，无效返回 code=401
     */
    @GetMapping("/me")
    public Result<Map<String, Object>> me(@RequestParam("token") String token) {
        String username = tokenService.getUsername(token);
        if (username == null) {
            return new Result<>(401, "登录已过期，请重新登录", null);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("username", username);
        return Result.success("ok", data);
    }

    /**
     * 退出登录：{"token": "..."}
     */
    @PostMapping("/logout")
    public Result<Void> logout(@RequestBody Map<String, String> body) {
        tokenService.remove(body.get("token"));
        return Result.success("已退出登录");
    }
}
