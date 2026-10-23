package com.fenglin.springboottest.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * 简易限流拦截器：按 IP 滑动窗口，60 秒内最多 10 次
 * （演示项目用内存实现；集群部署需换 Redis 等集中式方案）
 */
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private static final long WINDOW_MILLIS = 60_000L;
    private static final int MAX_REQUESTS = 10;

    private final Map<String, Deque<Long>> hits = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String ip = request.getRemoteAddr();
        long now = System.currentTimeMillis();

        Deque<Long> queue = hits.computeIfAbsent(ip, k -> new ConcurrentLinkedDeque<>());
        synchronized (queue) {
            // 滑出窗口外的旧记录
            while (!queue.isEmpty() && now - queue.peekFirst() > WINDOW_MILLIS) {
                queue.pollFirst();
            }
            if (queue.size() >= MAX_REQUESTS) {
                response.setStatus(429);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(
                        "{\"code\":429,\"msg\":\"操作太频繁啦，请一分钟后再试\",\"data\":null}");
                return false;
            }
            queue.addLast(now);
        }
        return true;
    }
}
