package com.fenglin.springboottest;

import com.fenglin.springboottest.entity.User;
import com.fenglin.springboottest.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UserService 集成测试：H2 内存库（见 src/test/resources/application.properties）
 */
@SpringBootTest
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Test
    void registerEncryptsPassword() {
        assertTrue(userService.register("alice", "secret123"));
        User user = userService.findByUsername("alice");
        assertNotNull(user);
        // 入库必须是 BCrypt 密文，而不是明文
        assertTrue(user.getPassword().startsWith("$2"));
        assertNotNull(user.getCreateTime());
    }

    @Test
    void duplicateRegisterFails() {
        assertTrue(userService.register("bob", "secret123"));
        assertFalse(userService.register("bob", "other456"));
    }

    @Test
    void loginSuccessAndWrongPassword() {
        userService.register("carol", "meow888");
        assertNotNull(userService.login("carol", "meow888"));
        assertNull(userService.login("carol", "wrong-pw"));
        assertNull(userService.login("nobody", "meow888"));
    }

    @Test
    void legacyPlaintextAutoUpgrades() {
        // 模拟历史明文数据：直接落一个明文密码的账号
        User legacy = new User();
        legacy.setUsername("dave");
        legacy.setPassword("plain123");
        legacy.setCreateTime(LocalDateTime.now());
        userService.save(legacy);

        // 明文能登录，且登录后被自动升级为 BCrypt 密文
        assertNotNull(userService.login("dave", "plain123"));
        User upgraded = userService.findByUsername("dave");
        assertTrue(upgraded.getPassword().startsWith("$2"));

        // 升级后再次登录仍正常
        assertNotNull(userService.login("dave", "plain123"));
    }
}
