package com.company.docmgmt.documentmanagementsystem.view.manager;

import com.company.docmgmt.documentmanagementsystem.MainApp;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewManager {

    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void loadView(String fxmlFile, String title) {
        try {
            // 修改这一行：使用 ViewManager.class 来定位资源
            FXMLLoader fxmlLoader = new FXMLLoader(ViewManager.class.getResource("/com/company/docmgmt/documentmanagementsystem/fxml/" + fxmlFile));

            // 调试语句: 检查URL是否为null
            if (fxmlLoader.getLocation() == null) {
                throw new IOException("无法找到FXML文件: " + "/com/company/docmgmt/documentmanagementsystem/fxml/" + fxmlFile);
            }

            Parent root = fxmlLoader.load();
            primaryStage.setTitle(title);
            if (primaryStage.getScene() == null) {
                primaryStage.setScene(new Scene(root));
            } else {
                primaryStage.getScene().setRoot(root);
            }
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("无法加载视图: " + fxmlFile);
            e.printStackTrace();
        }
    }

    public static void showLoginView() {
        loadView("LoginView.fxml", "登录");
    }

    public static void showMainView() {
        loadView("MainView.fxml", "文档管理系统");
    }
}