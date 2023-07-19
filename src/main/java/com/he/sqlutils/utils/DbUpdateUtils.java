package com.he.sqlutils.utils;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.he.sqlutils.pojo.entity.Column;
import com.he.sqlutils.pojo.entity.Table;

import static com.he.sqlutils.commons.Constants.*;

/**
 * @author hemoren
 */
public class DbUpdateUtils {
    private static final Logger logger = LoggerFactory.getLogger(CreateDbSqlUtils.class);

    /**
     * 保存sql语句
     */
    private static final Map<String, String> SQL_MAP = new HashMap<>(6);

    private static void generater(String path) {
        FileUtils.getAllFiles(path).forEach(DbUpdateUtils::dbUpdateSql);
    }

    /**
     * 生成sql语句
     * 
     * @param clazz
     */
    private static void dbUpdateSql(Class<?> clazz) {
        Table table = ClassToTableConverterUtils.convert(clazz);
        String sqlString = dbUpdateSql(table);
        String tableName = table.getName();
        // sql语句保存到map中
        // 如果sql为空则返回
        if (Objects.isNull(sqlString) || "".equals(sqlString)) {
            return;
        }
        dbUpdateSql(sqlString, tableName);
    }

    private static String dbUpdateSql(Table table) {
        String name = table.getName();
        // 判断数据表中是否存在该字段
        ResultSet tableColumns = SqlExecutionUtils.getTableColumns(name);
        return dbUpdateSql(table, tableColumns);
    }

    private static String dbUpdateSql(Table table, ResultSet tableColumns) {
        List<Column> columns = table.getColumns();
        // 保存需要更新的字段
        Table updateTable = new Table();
        updateTable.setName(table.getName());
        // 保存需要添加的字段
        Table addTable = new Table();
        addTable.setName(table.getName());
        // 保存需要删除的字段
        Table deleteTable = new Table();
        deleteTable.setName(table.getName());
        // 判断字段是否存在
        for (Column column : columns) {
            String columnName = column.getName();
            if (isExistColumn(tableColumns, columnName)) {
                // 判断类型是否一致
                if (!isSameType(tableColumns, column.getType())) {
                    updateTable.getColumns().add(column);
                }
                // 判断注释是否一致
                if (!isSameComment(tableColumns, column.getComment())) {
                    updateTable.getColumns().add(column);
                }
            } else {
                addTable.getColumns().add(column);
            }

        }
        // 针对删除字段的处理
        List<Column> deleteColumns = isExistColumn(columns, tableColumns);
        deleteTable.setColumns(deleteColumns);
        // 判断是否为空,为空则不生成sql语句
        String updateSql = "";
        String addSql = "";
        String deleteSql = "";
        if (!updateTable.getColumns().isEmpty()) {
            updateSql = SqlGeneratorUtils.updateTableSqlGenerator(updateTable, UPDATE);
        }
        if (!addTable.getColumns().isEmpty()) {
            addSql = SqlGeneratorUtils.updateTableSqlGenerator(addTable, ADD);
        }
        if (!deleteTable.getColumns().isEmpty()) {
            deleteSql = SqlGeneratorUtils.updateTableSqlGenerator(deleteTable, DELETE);
        }
        return updateSql + addSql + deleteSql;
    }

    /**
     * 判断表中存在但是实体类中不存在的字段
     * 
     * @param columns 实体类中的字段
     * @param name    表中的字段
     * @return column
     */
    private static List<Column> isExistColumn(List<Column> columns, ResultSet tableColumns) {
        // 保存需要删除的字段
        List<Column> deleteColumns = new ArrayList<>();
        // 遍历,判断表中存在但是实体类中不存在的字段
        try {
            tableColumns.beforeFirst();
            while (tableColumns.next()) {
                String name = tableColumns.getString("COLUMN_NAME");
                Column existColumn = isExistColumn(columns, name);
                if (!Objects.isNull(existColumn)) {
                    deleteColumns.add(existColumn);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deleteColumns;
    }

    /**
     * 判断表中存在但是实体类中不存在的字段
     * 
     * @param columns 实体类中的字段
     * @param name    表中的字段
     * @return column
     */
    private static Column isExistColumn(List<Column> columns, String name) {
        // 保存需要删除的字段
        List<String> columnNames = new ArrayList<>();
        for (Column column : columns) {
            columnNames.add(column.getName());
        }
        // 添加create_time和update_time
        columnNames.add(CREATE_TIME);
        columnNames.add(UPDATE_TIME);
        Column column = null;
        if (!columnNames.contains(name)) {
            column = new Column();
            column.setName(name);
        }
        return column;
    }

    /**
     * 判断表中字段是否存在
     */
    private static boolean isExistColumn(ResultSet tableColumns, String column) {
        try {
            tableColumns.beforeFirst();
            while (tableColumns.next()) {
                String name = tableColumns.getString("COLUMN_NAME");
                if (Objects.equals(name, column)) {
                    logger.info("表中存在该字段");
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断类型是否一致
     */
    private static boolean isSameType(ResultSet tableColumns, String type) {
        try {
            tableColumns.beforeFirst();
            while (tableColumns.next()) {
                String name = tableColumns.getString("TYPE_NAME");
                if (!Objects.isNull(type) | type.equals(name)) {
                    logger.info("字段类型一致");
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断注释是否一致
     */
    private static boolean isSameComment(ResultSet tableColumns, String comment) {
        try {
            tableColumns.beforeFirst();
            while (tableColumns.next()) {
                String name = tableColumns.getString("REMARKS");
                if (Objects.equals(name, comment)) {
                    logger.info("字段注释一致");
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * sql语句保存到map中
     * 
     * @param sqlString
     * @param tableName
     */
    private static void dbUpdateSql(String sqlString, String tableName) {
        logger.info("tableName:{}", tableName);
        if (sqlString == null || "".equals(sqlString)) {
            throw new RuntimeException("sql语句为空");
        }
        if (tableName == null || "".equals(tableName)) {
            throw new RuntimeException("表名为空");
        }
        SQL_MAP.put(tableName, sqlString);
    }

    public static void dbUpdate(String path) {
        generater(path);
        SQL_MAP.forEach((k, v) -> {
            logger.info("tableName:{}", k);
            logger.info("sql:{}", v);
            SqlExecutionUtils.executeSql(v);
        });
    }

    public static void main(String[] args) {
        dbUpdate("src/main/java/com/he/sqlutils/pojo/entity");
    }
}
