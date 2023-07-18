package com.he.sqlutils.config;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 读取配置文件
 * @author hemoren
 */
public class PropertyFactory {
    private static final Logger logger = LoggerFactory.getLogger(PropertyFactory.class);
    private static final Properties PROPERTIES = new Properties();
    static {
        try {
            PROPERTIES.load(PropertyFactory.class.getClassLoader().getResourceAsStream("application.properties"));
        } catch (IOException e) {
            logger.warn("Load Properties Ex", e);
        }
    }
    public static Properties getProperties() {
        return PROPERTIES;
    }
}
