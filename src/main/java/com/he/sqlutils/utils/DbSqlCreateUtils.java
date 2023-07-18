package com.he.sqlutils.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author hemoren
 */
public class DbSqlCreateUtils {

    /**
     * 数据库连接信息
     */
    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/nacos";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";
    /**
     * 工具中使用到的相关标识定义
     */
    private static final String TABLE_PREFIX = "auth_";
    /**
     * 数据库连接对象
     */
    private static Connection connection = null;
    static {
        try {
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前程序的数据库连接
     */
    public static Connection getConnection() {
        // 获取系统的数据库连接
        return connection;
    }

    public void dbSchemaCreate() {
        // 判断是否存在auth开头的表是否存在
        // 不存在则创建
        try {
            if (!isTableExist(TABLE_PREFIX)) {
                // 创建表
                createTable();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isTableExist(String tablePreFix) throws SQLException {
        // 判断表是否存在
        Statement createStatement = connection.createStatement();
        String sql = "select count(*) from information_schema.TABLES where TABLE_SCHEMA='nacos' and TABLE_NAME='"
                + tablePreFix + "'";
        System.out.println(sql);
        createStatement.execute(sql);
        ResultSet resultSet = createStatement.getResultSet();
        while (resultSet.next()) {
            int anInt = resultSet.getInt(1);
            if (anInt > 0) {
                return true;
            }
        }
        return false;
    }

    public static void createTable() {
        // 创建表
       
        
    }
}
