package com.fenglin.springboottest.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简易登录令牌服务：内存存储，2 小时过期
 * （演示项目够用；重启后令牌清空，生产环境可换 Redis / JWT）
 */
@Service
public class TokenService {

    private static final long EXPIRE_MILLIS = 2 * 60 * 60 * 1000L;

    private static class TokenInfo {
        final String username;
        final long expireAt;

        TokenInfo(String username, long expireAt) {
            this.username = username;
            this.expireAt = expireAt;
        }
    }

    private final Map<String, TokenInfo> tokens = new ConcurrentHashMap<>();

    /**
     * 登录成功后签发令牌
     */
    public String createToken(String username) {
        String token = UUID.randomUUID().toString().replace("-", "");
        tokens.put(token, new TokenInfo(username, System.currentTimeMillis() + EXPIRE_MILLIS));
        return token;
    }

    /**
     * 校验令牌，有效返回用户名，无效/过期返回 null
     */
    public String getUsername(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        TokenInfo info = tokens.get(token);
        if (info == null) {
            return null;
        }
        if (info.expireAt < System.currentTimeMillis()) {
            tokens.remove(token);
            return null;
        }
        return info.username;
    }

    /**
     * 注销令牌
     */
    public void remove(String token) {
        if (token != null) {
            tokens.remove(token);
        }
    }
}
