package com.fenglin.springboottest.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fenglin.springboottest.entity.GreetLog;

import java.util.List;
import java.util.Map;

public interface GreetLogService extends IService<GreetLog> {

    /**
     * 记录一次打招呼
     */
    void record(String username, String memberName);

    /**
     * 某用户累计打招呼次数
     */
    long countByUser(String username);

    /**
     * 成员人气榜：[{member, cnt}] 按被打招呼次数降序
     */
    List<Map<String, Object>> topMembers(int limit);
}
