package com.he.sqlutils.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.he.sqlutils.pojo.entity.Table;

/**
 * 数据库sql语句生成器
 * 
 * @author hemoren
 */
public class CreateDbSqlUtils {
    private static final Logger logger = LoggerFactory.getLogger(CreateDbSqlUtils.class);

    /**
     * 保存sql语句
     */
    private static final Map<String, String> SQL_MAP = new HashMap<>(6);

    private static void generater(String path) {
        FileUtils.getAllFiles(path).forEach(CreateDbSqlUtils::generaterDbSql);
    }

    /**
     * 生成sql语句
     * 
     * @param clazz
     */
    private static void generaterDbSql(Class<?> clazz) {
        Table table = ClassToTableConverterUtils.convert(clazz);
        String sqlString = SqlGeneratorUtils.generateByString(table);
        String tableName = table.getName();
        // sql语句保存到map中
        generaterDbSql(sqlString, tableName);
    }

    /**
     * sql语句保存到map中
     * 
     * @param sqlString
     * @param tableName
     */
    private static void generaterDbSql(String sqlString, String tableName) {
        logger.info("tableName:{}", tableName);
        if (sqlString == null || "".equals(sqlString)) {
            throw new RuntimeException("sql语句为空");
        }
        if (tableName == null || "".equals(tableName)) {
            throw new RuntimeException("表名为空");
        }
        SQL_MAP.put(tableName, sqlString);
    }

    /**
     * 判断数据库中是否存在表
     * 
     * @param tableName
     */
    private static boolean isExistTable(String tableName) {
        try {
            ResultSet tables = SqlExecutionUtils.getAllTables();
            tables.beforeFirst();
            // 遍历tables判断表名是否存在SQL_MAP中
            boolean flag = false;
            while (tables.next()) {
                String table = tables.getString("TABLE_NAME");
                if (Objects.equals(tableName, table)) {
                    flag = true;
                    logger.info("表名:{}已存在", tableName);
                    break;
                }
            }
            return flag;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 创建表
     * 
     * @param tableName
     */
    private static void createTable(String tableName) {
        String sql = SQL_MAP.get(tableName);
        if (Objects.isNull(sql) || "".equals(sql)) {
            throw new RuntimeException("sql语句为空");
        }
        String[] sqls = sql.split(";");
        for (String s : sqls) {
            SqlExecutionUtils.executeSql(s);
        }
    }

    /**
     * 初始化数据库
     */
    public static void initDb(String path) {
        generater(path);
        SQL_MAP.forEach((k, v) -> {
            if (!isExistTable(k)) {
                createTable(k);
            }
        });
    }

    public static void main(String[] args) {
        initDb("src/main/java/com/he/sqlutils/pojo/entity");
    }

}
