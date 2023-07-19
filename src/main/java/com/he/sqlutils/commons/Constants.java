package com.he.sqlutils.commons;

/**
 * @author hemoren
 */
public class Constants {
    /**
     * 数据库初始化标识
     */
    public static final String SQL_INITDB_ENABLE = "sql.initDB.enable";
    /**
     * 文件生成标识
     */
    public static final String SQL_FILE_ENABLE = "sql.file.enable";
    /**
     * 数据库配置
     */
    public static final String SQL_JDBC_URL = "sql.jdbc.url";
    public static final String SQL_JDBC_USERNAME = "sql.jdbc.username";
    public static final String SQL_JDBC_PASSWORD = "sql.jdbc.password";
    public static final String SQL_JDBC_DRIVER = "sql.jdbc.driver";
    /**
     * 扫描的包的位置
     */
    public static final String SQL_ENTITY_PACKAGE = "sql.entity.package";
    /**
     * 文件夹路径,默认在resources/sql/下
     */
    public static final String FILE_PATH = "src/main/resources/sql/";
    /**
     * 文件类型,默认为.sql
     */
    public static final String FILE_TYPE = ".sql";
    public static final String DEFAULT_TYPE = "VARCHAR(512)";
    public static final String STRING = "java.lang.String";
    public static final String INTEGER = "java.lang.Integer";
    public static final String LONG = "java.lang.Long";
    public static final String DOUBLE = "java.lang.Double";
    public static final String FLOAT = "java.lang.Float";
    public static final String SHORT = "java.lang.Short";
    public static final String BYTE = "java.lang.Byte";
    public static final String BOOLEAN = "java.lang.Boolean";
    public static final String BIGDECIMAL = "java.math.BigDecimal";
    public static final String DATE = "java.util.Date";
    public static final String SQLDATE = "java.sql.Date";
    public static final String LOCALDATE = "java.time.LocalDate";
    public static final String LOCALDATETIME = "java.time.LocalDateTime";

    public static final String DOC_LET_STRING = "-doclet";
    public static final String ENCODING_STRING = "-encoding";
    public static final String UTF_8_STRING = "utf-8";
    public static final String SRC_MAIN_JAVA_STRING = "src/main/java/";
    public static final String JAVA_STRING = ".java";
    public static final String KEY_FIELD = "field";
    public static final String KEY_METHOD = "method";
    /**
     * 
     */
    public static final String ADD = "add";
    public static final String UPDATE = "update";
    public static final String DELETE = "delete";
    /**
     * 
     */
    public static final String CREATE_TIME = "create_time";
    public static final String UPDATE_TIME = "update_time";
}
