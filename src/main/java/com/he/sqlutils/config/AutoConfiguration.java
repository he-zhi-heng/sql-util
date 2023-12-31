package com.he.sqlutils.config;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import com.he.sqlutils.service.SqlService;
import static com.he.sqlutils.commons.Constants.*;


/**
 * @author hemoren
 */
@Configuration
public class AutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(AutoConfiguration.class);


    public AutoConfiguration() {
        long startTime = System.currentTimeMillis();
        Properties properties = PropertyFactory.getProperties();
        boolean dbInitFlag = Boolean.parseBoolean(properties.getProperty(SQL_INITDB_ENABLE, "true"));
        boolean sqlFileFlag = Boolean.parseBoolean(properties.getProperty(SQL_FILE_ENABLE, "true"));
        if (dbInitFlag) {
            SqlService.initDb();
            logger.info("数据库初始化成功");
            SqlService.updateDb();
        }
        if (sqlFileFlag) {
            SqlService.generateSqlFile();
            logger.info("sql文件生成成功");
        }
        long endTime = System.currentTimeMillis();
        long timeInSeconds = TimeUnit.MILLISECONDS.toSeconds(endTime - startTime);
        logger.info("sql工具执行耗时：{}s", timeInSeconds);
    }
}
