package com.fenglin.springboottest.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fenglin.springboottest.entity.GreetLog;
import com.fenglin.springboottest.mapper.GreetLogMapper;
import com.fenglin.springboottest.service.GreetLogService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class GreetLogServiceImpl extends ServiceImpl<GreetLogMapper, GreetLog> implements GreetLogService {

    @Override
    public void record(String username, String memberName) {
        GreetLog log = new GreetLog();
        log.setUsername(username);
        log.setMemberName(memberName);
        log.setCreateTime(LocalDateTime.now());
        this.save(log);
    }

    @Override
    public long countByUser(String username) {
        return this.count(new QueryWrapper<GreetLog>().eq("username", username));
    }

    @Override
    public List<Map<String, Object>> topMembers(int limit) {
        QueryWrapper<GreetLog> qw = new QueryWrapper<>();
        qw.select("member_name AS member", "COUNT(*) AS cnt")
                .groupBy("member_name")
                .orderByDesc("cnt")
                .last("LIMIT " + Math.max(1, Math.min(limit, 50)));
        return this.listMaps(qw);
    }
}
