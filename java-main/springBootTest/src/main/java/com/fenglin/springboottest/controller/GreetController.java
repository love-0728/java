package com.fenglin.springboottest.controller;

import com.fenglin.springboottest.common.Result;
import com.fenglin.springboottest.entity.GreetLog;
import com.fenglin.springboottest.service.TokenService;
import com.fenglin.springboottest.service.GreetLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 家族成员打招呼接口（需要登录态）
 */
@RestController
@RequestMapping("/api/greet")
public class GreetController {

    @Autowired
    private GreetLogService greetLogService;

    @Autowired
    private TokenService tokenService;

    /**
     * 打招呼：{"token": "...", "member": "Kitty"}
     * 返回该用户累计打招呼次数
     */
    @PostMapping
    public Result<Map<String, Object>> greet(@RequestBody Map<String, String> body) {
        String username = tokenService.getUsername(body.get("token"));
        if (username == null) {
            return new Result<>(401, "登录已过期，请重新登录", null);
        }
        String member = body.get("member");
        if (!StringUtils.hasText(member)) {
            return Result.fail("成员名不能为空");
        }

        greetLogService.record(username, member.trim());
        long total = greetLogService.countByUser(username);

        Map<String, Object> data = new HashMap<>();
        data.put("member", member.trim());
        data.put("total", total);
        return Result.success("打招呼成功", data);
    }

    /**
     * 查询当前用户累计打招呼次数：GET /api/greet/count?token=...
     */
    @GetMapping("/count")
    public Result<Map<String, Object>> count(@RequestParam("token") String token) {
        String username = tokenService.getUsername(token);
        if (username == null) {
            return new Result<>(401, "登录已过期，请重新登录", null);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("total", greetLogService.countByUser(username));
        return Result.success("ok", data);
    }

    /**
     * 成员人气榜（公开）：GET /api/greet/leaderboard?limit=10
     */
    @GetMapping("/leaderboard")
    public Result<List<Map<String, Object>>> leaderboard(
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        return Result.success("ok", greetLogService.topMembers(limit));
    }
}
