package com.he.sqlutils.utils;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        //删除最后一个逗号
        sb.deleteCharAt(sb.lastIndexOf(","));
        //设置字符集,引擎
        sb.append(String.format(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;"));
        logger.info("SQL: {}", sb.toString());
        return sb.toString();
    }

}

