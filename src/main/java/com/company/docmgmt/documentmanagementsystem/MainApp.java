// MainApp.java
package com.company.docmgmt.documentmanagementsystem;

import com.company.docmgmt.documentmanagementsystem.view.manager.ViewManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        ViewManager.setPrimaryStage(stage); // 将主舞台交给管理器
        ViewManager.showLoginView();      // 首先显示登录视图
    }
}
