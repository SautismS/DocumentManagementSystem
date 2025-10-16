package com.company.docmgmt.documentmanagementsystem.view;

import com.company.docmgmt.documentmanagementsystem.view.manager.ViewManager;
import com.company.docmgmt.documentmanagementsystem.viewmodel.LoginViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginView {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;

    private final LoginViewModel viewModel = new LoginViewModel();

    @FXML
    public void initialize() {
        // 1. 将View的控件属性双向绑定到ViewModel的属性
        usernameField.textProperty().bindBidirectional(viewModel.usernameProperty());
        passwordField.textProperty().bindBidirectional(viewModel.passwordProperty());

        // 2. 将错误标签的文本单向绑定到ViewModel的错误信息属性
        errorLabel.textProperty().bind(viewModel.errorMessageProperty());

        // 3. 监听ViewModel的登录成功状态属性
        viewModel.loginSuccessfulProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                // 如果登录成功，则切换到主界面
                ViewManager.showMainView();
            }
        });
    }

    @FXML
    private void handleLoginAction() {
        // 4. 将点击事件委派给ViewModel处理
        viewModel.login();
    }
}