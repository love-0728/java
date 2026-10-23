package com.fenglin.springboottest;

import com.fenglin.springboottest.service.TokenService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * TokenService 纯单元测试（不启动 Spring 容器）
 */
class TokenServiceTest {

    private final TokenService tokenService = new TokenService();

    @Test
    void createAndValidateToken() {
        String token = tokenService.createToken("kitty");
        assertNotNull(token);
        assertEquals("kitty", tokenService.getUsername(token));
    }

    @Test
    void unknownTokenReturnsNull() {
        assertNull(tokenService.getUsername("not-exist-token"));
    }

    @Test
    void blankTokenReturnsNull() {
        assertNull(tokenService.getUsername(null));
        assertNull(tokenService.getUsername(""));
        assertNull(tokenService.getUsername("   "));
    }

    @Test
    void logoutInvalidatesToken() {
        String token = tokenService.createToken("kitty");
        tokenService.remove(token);
        assertNull(tokenService.getUsername(token));
    }

    @Test
    void tokensAreIndependent() {
        String t1 = tokenService.createToken("alice");
        String t2 = tokenService.createToken("alice");
        tokenService.remove(t1);
        // 同一用户的两个 token 互不影响
        assertEquals("alice", tokenService.getUsername(t2));
    }
}
