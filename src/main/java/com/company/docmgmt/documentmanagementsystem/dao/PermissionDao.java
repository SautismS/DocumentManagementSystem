package com.company.docmgmt.documentmanagementsystem.dao;

import com.company.docmgmt.documentmanagementsystem.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class PermissionDao {

    /**
     * 根据用户ID查询该用户所拥有的所有权限字符串
     * 这是RBAC的核心查询
     * @param userId 用户ID
     * @return 一个包含权限名称字符串的Set集合
     */
    public Set<String> findPermissionsByUserId(int userId) {
        Set<String> permissions = new HashSet<>();
        // 这个SQL通过三表连接查询，从用户ID找到他所有角色下的所有权限
        String sql = "SELECT p.permission_name " +
                "FROM permissions p " +
                "JOIN role_permissions rp ON p.id = rp.permission_id " +
                "JOIN user_roles ur ON rp.role_id = ur.role_id " +
                "WHERE ur.user_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                permissions.add(rs.getString("permission_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return permissions;
    }
}