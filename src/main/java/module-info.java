module com.company.docmgmt.documentmanagementsystem {
    // 依赖的模块
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    // 导出主应用程序包
    exports com.company.docmgmt.documentmanagementsystem;

    // 打开(opens)所有需要被JavaFX通过反射访问的包
    // 这是解决当前问题的关键
    opens com.company.docmgmt.documentmanagementsystem.model to javafx.base;

    // 同时，为了让FXMLLoader能工作，也需要打开这些包给fxml
    opens com.company.docmgmt.documentmanagementsystem.view to javafx.fxml;
    opens com.company.docmgmt.documentmanagementsystem.view.manager to javafx.fxml;
}
