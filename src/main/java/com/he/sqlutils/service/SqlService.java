package com.he.sqlutils.service;

import com.he.sqlutils.commons.Constants;
import com.he.sqlutils.config.PropertyFactory;
import com.he.sqlutils.utils.CreateDbSqlUtils;
import com.he.sqlutils.utils.CreateSqlFileUtils;

/**
 * @author hemoren
 */
public class SqlService {

    /**
     * 生成sql文件
     */
    public static void generateSqlFile(){
        CreateSqlFileUtils.generater(PropertyFactory.getProperties().getProperty(Constants.SQL_ENTITY_PACKAGE));
    }

    public static void initDb(){
        CreateDbSqlUtils.initDb(PropertyFactory.getProperties().getProperty(Constants.SQL_ENTITY_PACKAGE));
    }
}
