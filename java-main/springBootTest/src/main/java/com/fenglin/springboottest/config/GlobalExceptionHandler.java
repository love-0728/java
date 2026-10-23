package com.fenglin.springboottest.config;

import com.fenglin.springboottest.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理：兜底所有 /api 接口未捕获的异常，统一返回 JSON
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 唯一索引冲突（并发下两人同时注册同名时由数据库兜底）
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public Result<Void> handleDuplicateKey(DuplicateKeyException e) {
        log.warn("唯一索引冲突: {}", e.getMessage());
        return Result.fail("该用户名已被注册，换一个吧");
    }

    /**
     * 其他未预期异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("接口异常", e);
        return Result.fail("服务器开小差了，请稍后再试");
    }
}
