package com.company.docmgmt.documentmanagementsystem.service;

import com.company.docmgmt.documentmanagementsystem.dao.DocumentDao;
import com.company.docmgmt.documentmanagementsystem.model.Document;
import com.company.docmgmt.documentmanagementsystem.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 文档相关的业务逻辑服务
 */
public class DocumentService {

    private final DocumentDao documentDao = new DocumentDao();
    // 依赖认证服务来获取当前用户信息和权限
    private final AuthService authService = AuthService.getInstance();

    /**
     * 获取当前用户有权查看的文档列表。
     * 这是一个很好的业务逻辑封装示例。
     * @return 文档列表
     */
    public List<Document> getVisibleDocuments() {
        if (!authService.isLoggedIn()) {
            return Collections.emptyList(); // 未登录用户看不到任何文档
        }

        User currentUser = authService.getCurrentUser();

        // 业务规则：如果用户有 "document:list:all" 权限 (如管理员、访客)，则返回所有文档
        if (authService.hasPermission("document:list:all")) {
            return documentDao.findAll();
        }
        // 否则，只返回该用户自己创建的文档
        else {
            return documentDao.findByOwnerId(currentUser.getId());
        }
    }

    /**
     * 根据ID获取文档，并进行权限检查
     * @param documentId 文档ID
     * @return 如果找到且有权查看，则返回文档对象
     */
    public Optional<Document> getDocumentById(int documentId) {
        if (!authService.isLoggedIn()) {
            return Optional.empty();
        }

        Optional<Document> docOpt = documentDao.findById(documentId);
        if (docOpt.isPresent()) {
            Document doc = docOpt.get();
            User currentUser = authService.getCurrentUser();

            // 业务规则：管理员可以看任何文档，普通用户只能看自己的文档
            if (authService.hasPermission("document:list:all") || doc.getOwnerUserId() == currentUser.getId()) {
                return Optional.of(doc);
            }
        }
        return Optional.empty();
    }

    /**
     * 创建新文档
     * @param title 标题
     * @param content 内容
     * @return 如果创建成功返回true
     */
    public boolean createDocument(String title, String content) {
        if (!authService.hasPermission("document:create")) {
            System.err.println("权限不足：无法创建文档。");
            return false;
        }

        User currentUser = authService.getCurrentUser();
        Document newDoc = new Document();
        newDoc.setTitle(title);
        newDoc.setContent(content);
        newDoc.setOwnerUserId(currentUser.getId());
        newDoc.setStatus("DRAFT"); // 新文档默认为草稿状态

        return documentDao.save(newDoc);
    }

    /**
     * 更新文档
     * @param docToUpdate 包含更新后信息的文档对象
     * @return 如果更新成功返回true
     */
    public boolean updateDocument(Document docToUpdate) {
        if (!authService.isLoggedIn()) return false;

        // 1. 先从数据库获取原始文档，确保它存在
        Optional<Document> originalDocOpt = documentDao.findById(docToUpdate.getId());
        if (originalDocOpt.isEmpty()) {
            System.err.println("更新失败：文档不存在。");
            return false;
        }
        Document originalDoc = originalDocOpt.get();
        User currentUser = authService.getCurrentUser();

        // 2. 权限检查：用户必须拥有 'document:edit:own' 权限，并且是文档的所有者
        boolean canEdit = authService.hasPermission("document:edit:own") && originalDoc.getOwnerUserId() == currentUser.getId();

        // (可以扩展：如果管理员有全局编辑权限)
        // boolean isAdmin = authService.hasPermission("document:edit:all");
        // if (!canEdit && !isAdmin) { ... }

        if (!canEdit) {
            System.err.println("权限不足：你只能编辑自己的文档。");
            return false;
        }

        // 3. 执行更新
        return documentDao.update(docToUpdate);
    }

    /**
     * 删除文档
     * @param documentId 要删除的文档ID
     * @return 如果删除成功返回true
     */
    public boolean deleteDocument(int documentId) {
        if (!authService.isLoggedIn()) return false;

        // 1. 先从数据库获取文档信息，特别是所有者ID
        Optional<Document> docOpt = documentDao.findById(documentId);
        if (docOpt.isEmpty()) {
            System.err.println("删除失败：文档不存在。");
            return false; // 或者返回true，因为文档已经“没了”
        }
        Document doc = docOpt.get();
        User currentUser = authService.getCurrentUser();

        // 2. 权限检查：用户必须拥有 'document:delete:own' 权限，并且是文档的所有者
        boolean canDelete = authService.hasPermission("document:delete:own") && doc.getOwnerUserId() == currentUser.getId();

        // (可以扩展：如果管理员有全局删除权限)
        // boolean isAdmin = authService.hasPermission("document:delete:all");
        // if (!canDelete && !isAdmin) { ... }

        if (!canDelete) {
            System.err.println("权限不足：你只能删除自己的文档。");
            return false;
        }

        // 3. 执行删除
        return documentDao.delete(documentId);
    }
}