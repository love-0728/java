package com.fenglin.springboottest;

import com.fenglin.springboottest.service.CaptchaService;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * CaptchaService 纯单元测试（不启动 Spring 容器）
 */
class CaptchaServiceTest {

    private final CaptchaService captchaService = new CaptchaService();

    @Test
    void generateReturnsIdAndPngImage() {
        Map<String, String> cap = captchaService.generate();
        assertNotNull(cap.get("captchaId"));
        assertTrue(cap.get("image").startsWith("data:image/png;base64,"));
        // base64 内容不为空
        assertTrue(cap.get("image").length() > 100);
    }

    @Test
    void wrongCodeIsRejected() {
        Map<String, String> cap = captchaService.generate();
        assertFalse(captchaService.verify(cap.get("captchaId"), "XXXX"));
    }

    @Test
    void captchaIsOneTimeOnly() {
        Map<String, String> cap = captchaService.generate();
        // 第一次校验（无论对错）后即销毁，第二次必然失败
        captchaService.verify(cap.get("captchaId"), "XXXX");
        assertFalse(captchaService.verify(cap.get("captchaId"), "XXXX"));
    }

    @Test
    void unknownOrBlankIdIsRejected() {
        assertFalse(captchaService.verify("not-exist", "ABCD"));
        assertFalse(captchaService.verify(null, "ABCD"));
        assertFalse(captchaService.verify("some-id", null));
    }
}
