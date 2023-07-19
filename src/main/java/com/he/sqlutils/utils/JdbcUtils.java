package com.he.sqlutils.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;

import javax.sql.DataSource;
import static com.he.sqlutils.commons.Constants.*;

import com.alibaba.druid.pool.DruidDataSource;
import com.he.sqlutils.config.PropertyFactory;


/**
 * @author hemoren
 */
public class JdbcUtils {

    private static Connection connection = null;

    /**
     * 获取连接,单例模式
     * @return connection
     */
    public static Connection getConnection() {
        if (Objects.isNull(connection)) {
            connection = initConnection();
            return connection;
        } else {
            return connection;
        }
    }

    /**
     * 初始化连接
     * @return connection
     */
    private static Connection initConnection() {
        try {
            return iniDataSource().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 数据源初始化
     * 
     * @return
     */
    private static DataSource iniDataSource() {
        Properties properties = PropertyFactory.getProperties();
        boolean flag = Boolean.parseBoolean(properties.getProperty(SQL_INITDB_ENABLE, "true"));
        if (flag) {
            DruidDataSource dataSource = new DruidDataSource();
            dataSource.setUrl(properties.getProperty(SQL_JDBC_URL));
            dataSource.setUsername(properties.getProperty(SQL_JDBC_USERNAME));
            dataSource.setPassword(properties.getProperty(SQL_JDBC_PASSWORD));
            try {
                dataSource.init();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return dataSource;
        }
        return null;
    }

    /**
     * 关闭连接
     */
    public static void closeConnection() {
        if (Objects.nonNull(connection)) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
