package com.he.sqlutils.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.he.sqlutils.pojo.entity.Column;
import com.he.sqlutils.pojo.entity.Table;

/**
 * 将类的数据类型转为sql的数据类型
 * 
 * @author hemoren
 */
public class ClassToTableConverterUtils {

    private static final String DEFAULT_TYPE = "VARCHAR(512)";
    private static final String STRING = "java.lang.String";
    private static final String INTEGER = "java.lang.Integer";
    private static final String LONG = "java.lang.Long";
    private static final String DOUBLE = "java.lang.Double";
    private static final String FLOAT = "java.lang.Float";
    private static final String SHORT = "java.lang.Short";
    private static final String BYTE = "java.lang.Byte";
    private static final String BOOLEAN = "java.lang.Boolean";
    private static final String BIGDECIMAL = "java.math.BigDecimal";
    private static final String DATE = "java.util.Date";
    private static final String SQLDATE = "java.sql.Date";
    private static final String LOCALDATE = "java.time.LocalDate";
    private static final String LOCALDATETIME = "java.time.LocalDateTime";

    private static String prefix = "";
    private static Map<String, String> classTypeMap;
    static {
        classTypeMap = new HashMap<>();
        classTypeMap.put(STRING, DEFAULT_TYPE);
        classTypeMap.put(INTEGER, "INT");
        classTypeMap.put(LONG, "BIGINT");
        classTypeMap.put(DOUBLE, "DOUBLE");
        classTypeMap.put(FLOAT, "FLOAT");
        classTypeMap.put(SHORT, "SMALLINT");
        classTypeMap.put(BYTE, "SMALLINT");
        classTypeMap.put(BOOLEAN, "TINYINT");
        classTypeMap.put(BIGDECIMAL, "DECIMAL(10,2)");
        classTypeMap.put(DATE, "DATETIME");
        classTypeMap.put(SQLDATE, "DATETIME");
        classTypeMap.put(LOCALDATE, "DATE");
        classTypeMap.put(LOCALDATETIME, "DATETIME");
    }

    /**
     * 将类文件转为列信息
     *
     * @param psiClass
     * @return
     */
    public static Table convert(Class<?> psiClass) {
        Table table = new Table();
        table.setName(prefix + HumpUtil.humpToUnderline(psiClass.getSimpleName()));
        
        String name = psiClass.getName();
        // . 替换为 /
        name = name.replaceAll("\\.", "/");
        Map<String, Map<String, String>> show = JavaDocUtils.getJavaDoc(name);

        String separator = "\\n+";
        Field[] fields = ReflectUtil.getAllFields(psiClass);
        for (Field psiField : fields) {
            // 创建列信息实体类
            Column column = new Column();
            column.setName(HumpUtil.humpToUnderline(psiField.getName(), false));
            // 注释信息
            StringBuilder commentAccum = new StringBuilder();
            if (psiField.getName() != null) {
                show.get(JavaDocUtils.KEY_FIELD).forEach((k, v) -> {
                    if (Objects.equals(k, psiField.getName())) {
                        commentAccum.append(v);
                    }
                });
                column.setComment(commentAccum.toString().replaceAll(separator, "").trim());
            }
            if (psiField.getType() instanceof Class) {
                column.setType(classTypeMap.get(psiField.getType().getCanonicalName()));
            }
            if (column.getType() == null) {
                column.setType(DEFAULT_TYPE);
            }
            for (String s : commentAccum.toString().split(separator)) {
                if (s.trim().toLowerCase().startsWith("primary")) {
                    column.setPrimaryKey(true);
                }
                if (s.trim().toLowerCase().startsWith("max")) {
                    Long length = Long.parseLong(s.trim().split("=")[1].trim());
                    if (column.getType().equalsIgnoreCase(DEFAULT_TYPE)) {
                        if (length > 65535L) {
                            column.setType("text");
                        } else if (length > 4L) {
                            column.setType(String.format("VARCHAR(%s)", length));
                        } else {
                            column.setType(String.format("CHAR(%s)", length));
                        }
                    }
                }
                if (s.trim().toLowerCase().startsWith("required")) {
                    column.setNullable(!Boolean.parseBoolean(s.trim().split("=")[1].trim()));
                }
            }
            table.getColumns().add(column);
        }
        return table;
    }

}

