package com.fenglin.springboottest.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fenglin.springboottest.entity.User;
import com.fenglin.springboottest.mapper.UserMapper;
import com.fenglin.springboottest.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User findByUsername(String username) {
        return this.getOne(new QueryWrapper<User>().eq("username", username), false);
    }

    @Override
    public boolean register(String username, String password) {
        if (findByUsername(username) != null) {
            return false;
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password)); // BCrypt 加密存储
        user.setCreateTime(LocalDateTime.now());
        return this.save(user);
    }

    @Override
    public User login(String username, String password) {
        User user = findByUsername(username);
        if (user == null) {
            return null;
        }
        String stored = user.getPassword();
        if (stored != null && stored.startsWith("$2")) {
            // 正常路径：BCrypt 密文校验
            return passwordEncoder.matches(password, stored) ? user : null;
        }
        // 兼容路径：早期明文数据，校验通过后自动升级为密文
        if (stored != null && stored.equals(password)) {
            user.setPassword(passwordEncoder.encode(password));
            this.updateById(user);
            return user;
        }
        return null;
    }
}
