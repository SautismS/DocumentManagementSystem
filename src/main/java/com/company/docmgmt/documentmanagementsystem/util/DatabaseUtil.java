package com.company.docmgmt.documentmanagementsystem.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
    // !!! 请务必修改成你自己的数据库配置 !!!
    private static final String URL = "jdbc:mysql://localhost:3306/doc_management_db?serverTimezone=UTC";
    private static final String USER = "root";       // 你的MySQL用户名
    private static final String PASSWORD = "xjtuse"; // 你的MySQL密码

    /**
     * 获取数据库连接
     * @return 数据库连接对象
     * @throws SQLException 如果连接失败
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // =============================================================
    //  这是一个临时的测试方法，用来验证数据库连接是否成功
    //  测试成功后可以删除或注释掉
    // =============================================================
    public static void main(String[] args) {
        System.out.println("正在测试数据库连接...");
        try (Connection connection = getConnection()) {
            if (connection != null && !connection.isClosed()) {
                System.out.println("==========================================");
                System.out.println("      ✅ 数据库连接成功！");
                System.out.println("==========================================");
                System.out.println("数据库产品名称: " + connection.getMetaData().getDatabaseProductName());
                System.out.println("数据库产品版本: " + connection.getMetaData().getDatabaseProductVersion());
            }
        } catch (SQLException e) {
            System.err.println("==========================================");
            System.err.println("      ❌ 数据库连接失败！");
            System.err.println("==========================================");
            System.err.println("请检查以下几点:");
            System.err.println("1. 数据库服务是否已启动？");
            System.err.println("2. URL、用户名、密码是否正确？");
            System.err.println("3. pom.xml中是否已添加MySQL驱动依赖？");
            System.err.println("\n--- 详细错误信息 ---");
            e.printStackTrace();
        }
    }
}