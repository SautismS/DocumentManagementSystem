package com.company.docmgmt.documentmanagementsystem.viewmodel;

import com.company.docmgmt.documentmanagementsystem.model.Document;
import com.company.docmgmt.documentmanagementsystem.service.AuthService;
import com.company.docmgmt.documentmanagementsystem.service.DocumentService;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MainViewModel {

    private final AuthService authService = AuthService.getInstance();
    private final DocumentService documentService = new DocumentService();

    // --- Properties for Data Binding in View ---

    // 用户信息
    private final StringProperty welcomeMessage = new SimpleStringProperty("欢迎, 游客");

    // 文档列表
    private final ObservableList<Document> documentList = FXCollections.observableArrayList();
    private final ObjectProperty<Document> selectedDocument = new SimpleObjectProperty<>();

    // UI元素权限控制
    private final BooleanProperty canCreate = new SimpleBooleanProperty(false);
    private final BooleanProperty canEdit = new SimpleBooleanProperty(false);
    private final BooleanProperty canDelete = new SimpleBooleanProperty(false);
    private final BooleanProperty isAdminPanelVisible = new SimpleBooleanProperty(false);

    public MainViewModel() {
        // ViewModel初始化时，加载初始数据和权限
        initialize();
    }

    public void initialize() {
        if (authService.isLoggedIn()) {
            welcomeMessage.set("欢迎, " + authService.getCurrentUser().getUsername());
        }
        // 根据权限设置UI元素的可见/可用状态
        updatePermissions();
        // 加载文档列表
        refreshDocumentList();

        // 关键：监听selectedDocument的变化，以动态更新“编辑”和“删除”按钮的可用状态
        selectedDocument.addListener((obs, oldSelection, newSelection) -> updateEditDeleteState());
    }

    // --- Actions/Commands for View ---

    public void refreshDocumentList() {
        documentList.setAll(documentService.getVisibleDocuments());
    }

    public void createNewDocument() {
        // 实际应用中，这里会弹出一个新的窗口来创建文档
        System.out.println("Action: 创建新文档...");
        documentService.createDocument("新文档标题", "这是内容...");
        refreshDocumentList();
    }

    public void editSelectedDocument() {
        if (selectedDocument.get() != null) {
            // 实际应用中，这里会弹出一个窗口来编辑文档
            System.out.println("Action: 编辑文档 " + selectedDocument.get().getTitle());
            // ... documentService.updateDocument(...)
        }
    }

    public void deleteSelectedDocument() {
        if (selectedDocument.get() != null) {
            System.out.println("Action: 删除文档 " + selectedDocument.get().getTitle());
            boolean success = documentService.deleteDocument(selectedDocument.get().getId());
            if(success) {
                refreshDocumentList();
            } else {
                // 可以在这里设置一个状态属性，让View显示一个错误提示
                System.err.println("删除失败，请检查权限或日志。");
            }
        }
    }

    public void logout() {
        authService.logout();
        // 这里需要一种机制通知View切换回登录界面
    }

    // --- Private Helper Methods ---

    private void updatePermissions() {
        canCreate.set(authService.hasPermission("document:create"));
        isAdminPanelVisible.set(authService.hasPermission("user:manage"));
        // 编辑和删除按钮的初始状态依赖于是否有对应权限
        updateEditDeleteState();
    }

    private void updateEditDeleteState() {
        Document selected = selectedDocument.get();
        if (selected == null) {
            canEdit.set(false);
            canDelete.set(false);
            return;
        }
        // 动态判断：用户不仅要有权限，还必须是文档的所有者
        boolean isOwner = selected.getOwnerUserId() == authService.getCurrentUser().getId();
        canEdit.set(authService.hasPermission("document:edit:own") && isOwner);
        canDelete.set(authService.hasPermission("document:delete:own") && isOwner);
    }

    // --- Getters for Properties ---
    public StringProperty welcomeMessageProperty() { return welcomeMessage; }
    public ObservableList<Document> getDocumentList() { return documentList; }
    public ObjectProperty<Document> selectedDocumentProperty() { return selectedDocument; }
    public BooleanProperty canCreateProperty() { return canCreate; }
    public BooleanProperty canEditProperty() { return canEdit; }
    public BooleanProperty canDeleteProperty() { return canDelete; }
    public BooleanProperty isAdminPanelVisibleProperty() { return isAdminPanelVisible; }
}