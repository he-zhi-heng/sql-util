package com.he.sqlutils.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.he.sqlutils.commons.Constants.*;
import com.he.sqlutils.pojo.entity.Column;
import com.he.sqlutils.pojo.entity.Table;

/**
 * @author hemoren
 */
public class SqlGeneratorUtils {
    private static final Logger logger = LoggerFactory.getLogger(SqlGeneratorUtils.class);

    public static String generateByString(Table table) {
        StringBuffer sb = new StringBuffer();
        sb.append(String.format("DROP TABLE IF EXISTS `%s`;\n", table.getName()));
        sb.append(String.format("CREATE TABLE `%s` (\n", table.getName()));
        // 主键名称临时保存s
        List<String> primaryKeyList = new ArrayList<>();
        // create_time 字段是否存在
        Boolean createTimeFiled = false;
        String createTimeFiledName = "create_time";
        // update_time 字段是否存在
        boolean updateTimeFiled = false;
        String updateTimeFiledName = "update_time";

        for (Column column : table.getColumns()) {
            sb.append(String.format("\t`%s` %s %s COMMENT '%s',\n",
                    column.getName(),
                    column.getType(),
                    column.isPrimaryKey() ? "NOT NULL" : column.isNullable() ? "Null" : "NOT NULL",
                    column.getComment() == null ? "" : column.getComment()));
            if (column.isPrimaryKey()) {
                primaryKeyList.add(String.format("`%s`", column.getName()));
            }
            if (column.getName().trim().equalsIgnoreCase(createTimeFiledName)) {
                createTimeFiled = true;
            }
            if (column.getName().trim().equalsIgnoreCase(updateTimeFiledName)) {
                updateTimeFiled = true;
            }
        }

        if (!createTimeFiled) {
            sb.append(String.format("\t`%s` timestamp NULL DEFAULT CURRENT_TIMESTAMP,\n", createTimeFiledName));
        }

        if (!updateTimeFiled) {
            sb.append(
                    String.format("\t`%s` timestamp NOT NULL ON UPDATE CURRENT_TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n",
                            updateTimeFiledName));
        }

        if (!primaryKeyList.isEmpty()) {
            sb.append(String.format("\tPRIMARY KEY (%s) USING BTREE\n", String.join(",", primaryKeyList)));
        }
        // 删除最后一个逗号
        sb.deleteCharAt(sb.lastIndexOf(","));
        // 设置字符集,引擎
        sb.append(String.format(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;"));
        logger.info("SQL: {}", sb.toString());
        return sb.toString();
    }

    /**
     * 更新表sql生成器
     * @param table 表
     * @param flag  标志位
     * @return sql
     */
    public String updateTableSqlGenerator(Table table, String flag) {
        String sqlString;
        if (Objects.equals(flag, ADD)) {
            sqlString = getAddColumnSql(table);
        } else if (Objects.equals(flag, UPDATE)) {
            sqlString = getUpdateColumnSql(table);
        } else if (Objects.equals(flag, DELETE)) {
            sqlString = getDeleteColumnSql(table);
        } else {
            throw new RuntimeException("flag参数错误");
        }
        if (sqlString == null || "".equals(sqlString)) {
            throw new RuntimeException("sql语句为空");
        }
        return sqlString;
    }

    /**
     * ALTER TABLE `%s` DROP COLUMN `%s`;
     * @param table 表
     * @return sql
     */
    private String getDeleteColumnSql(Table table) {
        StringBuffer sb = new StringBuffer();
        String name = table.getName();
        sb.append(String.format("ALTER TABLE `%s`", name));
        List<Column> columns = table.getColumns();
        for (Column column : columns) {
            sb.append(String.format("DROP COLUMN `%s`,", column.getName()));
        }
        // 删除最后一个逗号
        sb.deleteCharAt(sb.lastIndexOf(","));
        logger.info("SQL: {}", sb.toString());
        return sb.toString();
    }

    /**
     * ALTER TABLE `%s` MODIFY COLUMN `%s` %s %s COMMENT '%s';
     * @param table 表
     * @return sql
     */
    private String getUpdateColumnSql(Table table) {
        StringBuffer sb = new StringBuffer();
        String name = table.getName();
        sb.append(String.format("ALTER TABLE `%s`", name));
        List<Column> columns = table.getColumns();
        for (Column column : columns) {
            sb.append(String.format("MODIFY COLUMN `%s` %s %s COMMENT '%s',",
                    column.getName(),
                    column.getType(),
                    column.isPrimaryKey() ? "NOT NULL" : column.isNullable() ? "Null" : "NOT NULL",
                    column.getComment() == null ? "" : column.getComment()));
        }
        // 删除最后一个逗号
        sb.deleteCharAt(sb.lastIndexOf(","));
        logger.info("SQL: {}", sb.toString());
        return sb.toString();
    }

    /**
     * ALTER TABLE `%s` ADD COLUMN `%s` %s %s COMMENT '%s';
     * @param table 表
     * @return sql
     */
    private static String getAddColumnSql(Table table) {
        StringBuffer sb = new StringBuffer();
        String name = table.getName();
        sb.append(String.format("ALTER TABLE `%s` ", name));
        List<Column> columns = table.getColumns();
        for (Column column : columns) {
            sb.append(String.format("ADD COLUMN `%s` %s %s COMMENT '%s',",
                    column.getName(),
                    column.getType(),
                    column.isPrimaryKey() ? "NOT NULL" : column.isNullable() ? "Null" : "NOT NULL",
                    column.getComment() == null ? "" : column.getComment()));
        }
        // 删除最后一个逗号
        sb.deleteCharAt(sb.lastIndexOf(","));
        logger.info("SQL: {}", sb.toString());
        return sb.toString();
    }

}
