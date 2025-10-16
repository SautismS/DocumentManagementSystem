package com.company.docmgmt.documentmanagementsystem.dao;

import com.company.docmgmt.documentmanagementsystem.model.User;
import com.company.docmgmt.documentmanagementsystem.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDao {

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 包含用户对象的Optional，如果找不到则为空
     */
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRowToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // 在实际项目中应使用日志框架
        }
        return Optional.empty();
    }

    /**
     * 根据ID查找用户
     * @param id 用户ID
     * @return 包含用户对象的Optional，如果找不到则为空
     */
    public Optional<User> findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRowToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * 获取所有用户列表
     * @return 用户列表
     */
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY id";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * 保存用户（插入新用户或更新现有用户）
     * @param user 要保存的用户对象
     * @return 保存后的用户对象，包含可能更新的ID
     */
    public User save(User user) {
        if (user.getId() > 0) {
            // 更新现有用户
            return update(user);
        } else {
            // 插入新用户
            return insert(user);
        }
    }

    /**
     * 插入新用户
     * @param user 要插入的用户对象
     * @return 插入后的用户对象，包含生成的ID
     */
    private User insert(User user) {
        String sql = "INSERT INTO users (username, password, email, created_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setTimestamp(4, Timestamp.valueOf(user.getCreatedAt() != null ? user.getCreatedAt() : LocalDateTime.now()));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                    }
                }
            }
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to insert user: " + e.getMessage(), e);
        }
    }

    /**
     * 更新现有用户
     * @param user 要更新的用户对象（必须包含ID）
     * @return 更新后的用户对象
     */
    private User update(User user) {
        if (user.getId() <= 0) {
            throw new IllegalArgumentException("User ID must be greater than 0 for update operation");
        }

        String sql = "UPDATE users SET username = ?, password = ?, email = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setInt(4, user.getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No user found with ID: " + user.getId());
            }
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to update user: " + e.getMessage(), e);
        }
    }

    /**
     * 删除用户
     * @param id 要删除的用户ID
     * @return 如果成功删除返回true，否则返回false
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将ResultSet的当前行映射到一个User对象
     * @param rs ResultSet
     * @return User对象
     * @throws SQLException
     */
    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password")); // 演示用，加载明文密码
        user.setEmail(rs.getString("email"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return user;
    }
}