package com.he.sqlutils.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hemoren
 */
public class SqlExecutionUtils {

    private static final Logger logger = LoggerFactory.getLogger(SqlExecutionUtils.class);
    private static Connection connection = null;
    private static ResultSet tables = null;
    /**
     * 初始化连接
     */
    static {
        connection = JdbcUtils.getConnection();
    }

    /**
     * 执行sql语句
     */
    public static void executeSql(String sql) {
        logger.info("执行sql:{}", sql);
        if (sql == null || "".equals(sql)) {
            throw new RuntimeException("sql语句为空");
        }
        try {
            String[] sqls = sql.split(";");
            for (String s : sqls) {
                connection.prepareStatement(s).execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取所有表
     */
    public static synchronized ResultSet getAllTables() {
        try {
            if (Objects.isNull(tables)) {
                tables = connection.getMetaData().getTables(null, null, "%", new String[] { "TABLE" });
            }
            logger.info("tables:{}", tables);
            return tables;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 通过表名查询表字段及类型
     */
    public static ResultSet getTableColumns(String tableName) {
        try {
            ResultSet columns = connection.getMetaData().getColumns(null, null, tableName, null);
            logger.info("columns:{}", columns);
            return columns;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        ResultSet allTables = getTableColumns("auth_org");
        while (true) {
            try {
                if (!allTables.next()) {
                    break;
                }
                String string = allTables.getString("COLUMN_NAME");
                System.out.println(string);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
