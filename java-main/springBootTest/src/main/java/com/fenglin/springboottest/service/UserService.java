package com.fenglin.springboottest.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fenglin.springboottest.entity.User;

public interface UserService extends IService<User> {

    /**
     * 根据用户名查询用户
     */
    User findByUsername(String username);

    /**
     * 注册新用户，返回 false 表示用户名已存在
     */
    boolean register(String username, String password);

    /**
     * 校验登录，成功返回用户，失败返回 null
     * （兼容早期明文密码：校验通过后会自动升级为 BCrypt 密文）
     */
    User login(String username, String password);
}
