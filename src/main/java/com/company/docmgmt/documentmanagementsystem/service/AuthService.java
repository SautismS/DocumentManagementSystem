package com.company.docmgmt.documentmanagementsystem.service;

import com.company.docmgmt.documentmanagementsystem.dao.PermissionDao;
import com.company.docmgmt.documentmanagementsystem.dao.UserDao;
import com.company.docmgmt.documentmanagementsystem.model.User;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

/**
 * 认证和授权服务 (Authentication and Authorization Service)
 * 采用单例模式，确保整个应用中只有一个用户会话状态。
 */
public class AuthService {

    // ----------- 单例模式实现 -----------
    private static AuthService instance;

    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }
    // ------------------------------------


    private final UserDao userDao = new UserDao();
    private final PermissionDao permissionDao = new PermissionDao();

    private User currentUser;
    private Set<String> currentUserPermissions = Collections.emptySet();

    // 私有构造函数，防止外部直接实例化
    private AuthService() {
    }

    /**
     * 用户登录逻辑
     * @param username 用户名
     * @param password 密码 (明文)
     * @return 如果登录成功返回true，否则返回false
     */
    public boolean login(String username, String password) {
        // 1. 根据用户名从数据库查找用户
        Optional<User> userOpt = userDao.findByUsername(username);

        // 2. 如果用户存在，则校验密码
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // 演示项目：直接比较明文密码
            if (user.getPassword().equals(password)) {
                // 3. 密码正确，设置当前用户状态
                this.currentUser = user;
                // 4. 加载并缓存该用户的所有权限
                this.currentUserPermissions = permissionDao.findPermissionsByUserId(user.getId());
                System.out.println("登录成功! 用户: " + currentUser.getUsername() + ", 权限: " + currentUserPermissions);
                return true;
            }
        }
        // 用户不存在或密码错误
        System.out.println("登录失败: 用户名或密码错误。");
        return false;
    }

    /**
     * 用户登出
     */
    public void logout() {
        this.currentUser = null;
        this.currentUserPermissions = Collections.emptySet();
        System.out.println("用户已登出。");
    }

    /**
     * 获取当前登录的用户对象
     * @return 当前用户，如果未登录则返回null
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * 检查当前用户是否已登录
     * @return 如果已登录返回true
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * 检查当前登录的用户是否拥有指定的权限
     * 这是整个RBAC系统的核心检查点
     * @param permission 需要检查的权限字符串，例如 "document:create"
     * @return 如果拥有该权限返回true
     */
    public boolean hasPermission(String permission) {
        if (!isLoggedIn()) {
            return false; // 未登录用户没有任何权限
        }
        return currentUserPermissions.contains(permission);
    }
}