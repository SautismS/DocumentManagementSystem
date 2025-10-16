package com.company.docmgmt.documentmanagementsystem.view;

import com.company.docmgmt.documentmanagementsystem.model.Document;
import com.company.docmgmt.documentmanagementsystem.view.manager.ViewManager;
import com.company.docmgmt.documentmanagementsystem.viewmodel.MainViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDateTime;

public class MainView {
    // --- FXML Injected Controls ---
    @FXML private Label welcomeLabel;
    @FXML private Button logoutButton;
    @FXML private Button createButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button refreshButton;
    @FXML private TableView<Document> documentTableView;
    @FXML private TableColumn<Document, Integer> idColumn;
    @FXML private TableColumn<Document, String> titleColumn;
    @FXML private TableColumn<Document, Integer> ownerIdColumn;
    @FXML private TableColumn<Document, String> statusColumn;
    @FXML private TableColumn<Document, LocalDateTime> createdAtColumn;
    @FXML private TableColumn<Document, LocalDateTime> updatedAtColumn;

    private final MainViewModel viewModel = new MainViewModel();

    @FXML
    public void initialize() {
        // --- 1. 绑定UI元素权限 ---
        welcomeLabel.textProperty().bind(viewModel.welcomeMessageProperty());
        createButton.visibleProperty().bind(viewModel.canCreateProperty());
        // 编辑和删除按钮的可用状态是动态的，所以绑定disable属性
        editButton.disableProperty().bind(viewModel.canEditProperty().not());
        deleteButton.disableProperty().bind(viewModel.canDeleteProperty().not());

        // --- 2. 绑定表格数据 ---
        // 将表格的items属性绑定到ViewModel的ObservableList
        documentTableView.setItems(viewModel.getDocumentList());

        // --- 3. 配置表格列 ---
        // 将每一列与Document对象的属性关联起来
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        ownerIdColumn.setCellValueFactory(new PropertyValueFactory<>("ownerUserId"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        updatedAtColumn.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));

        // --- 4. 绑定表格选中项 ---
        // 将ViewModel的selectedDocument属性与表格的选中项双向绑定
        viewModel.selectedDocumentProperty().bind(documentTableView.getSelectionModel().selectedItemProperty());
    }

    // --- 5. 委派事件到ViewModel ---
    @FXML
    private void handleCreateAction() {
        viewModel.createNewDocument();
    }

    @FXML
    private void handleEditAction() {
        viewModel.editSelectedDocument();
    }

    @FXML
    private void handleDeleteAction() {
        viewModel.deleteSelectedDocument();
    }

    @FXML
    private void handleRefreshAction() {
        viewModel.refreshDocumentList();
    }

    @FXML
    private void handleLogoutAction() {
        viewModel.logout();
        ViewManager.showLoginView(); // 退出登录后，返回登录界面
    }
}