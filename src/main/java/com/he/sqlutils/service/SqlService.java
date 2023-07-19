package com.he.sqlutils.service;

import com.he.sqlutils.commons.Constants;
import com.he.sqlutils.config.PropertyFactory;
import com.he.sqlutils.utils.CreateDbSqlUtils;
import com.he.sqlutils.utils.CreateSqlFileUtils;
import com.he.sqlutils.utils.DbUpdateUtils;

/**
 * @author hemoren
 */
public class SqlService {

    /**
     * 生成sql文件
     */
    public static void generateSqlFile() {
        CreateSqlFileUtils.generater(PropertyFactory.getProperties().getProperty(Constants.SQL_ENTITY_PACKAGE));
    }

    /**
     * 初始化数据库
     */
    public static void initDb() {
        CreateDbSqlUtils.initDb(PropertyFactory.getProperties().getProperty(Constants.SQL_ENTITY_PACKAGE));
    }

    /**
     * 更新数据库
     */
    public static void updateDb() {
        DbUpdateUtils.dbUpdate(PropertyFactory.getProperties().getProperty(Constants.SQL_ENTITY_PACKAGE));
    }
}
