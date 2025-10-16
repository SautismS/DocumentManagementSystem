package com.company.docmgmt.documentmanagementsystem.dao;

import com.company.docmgmt.documentmanagementsystem.model.Document;
import com.company.docmgmt.documentmanagementsystem.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DocumentDao {

    /**
     * 查找所有文档
     * @return 文档列表
     */
    public List<Document> findAll() {
        List<Document> documents = new ArrayList<>();
        String sql = "SELECT * FROM documents ORDER BY created_at DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                documents.add(mapRowToDocument(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return documents;
    }

    /**
     * 根据创建者ID查找文档
     * @param ownerId 创建者的用户ID
     * @return 该用户创建的文档列表
     */
    public List<Document> findByOwnerId(int ownerId) {
        List<Document> documents = new ArrayList<>();
        String sql = "SELECT * FROM documents WHERE owner_user_id = ? ORDER BY created_at DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ownerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                documents.add(mapRowToDocument(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return documents;
    }

    /**
     * 根据文档ID查找文档
     * @param id 文档ID
     * @return 包含文档对象的Optional
     */
    public Optional<Document> findById(int id) {
        String sql = "SELECT * FROM documents WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToDocument(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }


    /**
     * 将ResultSet的当前行映射到一个Document对象
     * @param rs ResultSet
     * @return Document对象
     * @throws SQLException
     */
    private Document mapRowToDocument(ResultSet rs) throws SQLException {
        Document doc = new Document();
        doc.setId(rs.getInt("id"));
        doc.setTitle(rs.getString("title"));
        doc.setContent(rs.getString("content"));
        doc.setOwnerUserId(rs.getInt("owner_user_id"));
        doc.setStatus(rs.getString("status"));
        doc.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        doc.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return doc;
    }

    /**
     * 保存一个新文档到数据库
     * @param document 要保存的文档对象 (ID应为空)
     * @return 保存成功返回true
     */
    public boolean save(Document document) {
        String sql = "INSERT INTO documents (title, content, owner_user_id, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, document.getTitle());
            stmt.setString(2, document.getContent());
            stmt.setInt(3, document.getOwnerUserId());
            stmt.setString(4, document.getStatus());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 更新一个已存在的文档
     * @param document 要更新的文档对象 (必须包含ID)
     * @return 更新成功返回true
     */
    public boolean update(Document document) {
        String sql = "UPDATE documents SET title = ?, content = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, document.getTitle());
            stmt.setString(2, document.getContent());
            stmt.setString(3, document.getStatus());
            stmt.setInt(4, document.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据ID删除一个文档
     * @param id 要删除的文档ID
     * @return 删除成功返回true
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM documents WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}