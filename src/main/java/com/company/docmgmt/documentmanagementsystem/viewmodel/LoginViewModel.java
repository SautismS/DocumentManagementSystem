package com.company.docmgmt.documentmanagementsystem.viewmodel;

import com.company.docmgmt.documentmanagementsystem.service.AuthService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LoginViewModel {

    private final AuthService authService = AuthService.getInstance();

    // --- Properties for Data Binding ---
    private final StringProperty username = new SimpleStringProperty("");
    private final StringProperty password = new SimpleStringProperty("");
    private final StringProperty errorMessage = new SimpleStringProperty("");
    // 用于通知View登录成功的状态
    private final BooleanProperty loginSuccessful = new SimpleBooleanProperty(false);

    // --- Actions/Commands for View ---
    public void login() {
        if (username.get().isEmpty() || password.get().isEmpty()) {
            errorMessage.set("用户名或密码不能为空");
            return;
        }
        boolean success = authService.login(username.get(), password.get());
        if (success) {
            errorMessage.set("");
            loginSuccessful.set(true); // 设置成功状态
        } else {
            errorMessage.set("用户名或密码错误");
            loginSuccessful.set(false);
        }
    }

    // --- Getters for Properties ---
    public StringProperty usernameProperty() { return username; }
    public StringProperty passwordProperty() { return password; }
    public StringProperty errorMessageProperty() { return errorMessage; }
    public BooleanProperty loginSuccessfulProperty() { return loginSuccessful; }
}