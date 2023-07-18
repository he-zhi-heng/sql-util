package com.he.sqlutils.utils;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.RootDoc;

/**
 * javadoc工具类
 * @author hemoren
 */
public class JavaDocUtils {
    private static final Logger log = LoggerFactory.getLogger(JavaDocUtils.class);
    private static final String DOC_LET_STRING = "-doclet";
    private static final String ENCODING_STRING = "-encoding";
    private static final String UTF_8_STRING = "utf-8";
    private static final String SRC_MAIN_JAVA_STRING = "src/main/java/";
    private static final String JAVA_STRING = ".java";
    public static final String KEY_FIELD = "field";
    public static final String KEY_METHOD = "method";

    public static Map<String, Map<String, String>> getJavaDoc(String name) {
        com.sun.tools.javadoc.Main.execute(new String[] { DOC_LET_STRING,
                JavaDocUtils.class.getName(),
                ENCODING_STRING,
                UTF_8_STRING,
                SRC_MAIN_JAVA_STRING + name + JAVA_STRING,
        });
        return show();
    }

    /** 文档根节点 */
    private static RootDoc root;

    /**
     * javadoc调用入口
     *
     * @param root
     * @return
     */
    public static boolean start(RootDoc root) {
        JavaDocUtils.root = root;
        return true;
    }

    /**
     * 显示DocRoot中的基本信息
     * 
     * @return map key为field和method
     */
    private static Map<String, Map<String, String>> show() {
        Map<String, Map<String, String>> map = new HashMap<>(6);
        ClassDoc[] classes = root.classes();
        for (ClassDoc classDoc : classes) {
            System.out.println("------------------------------------------------");
            System.out.println(classDoc.name() + "类的注释:" + classDoc.getRawCommentText());
            FieldDoc[] fields = classDoc.fields(false);
            // 注释保存在map中
            Map<String, String> fieldMap = new HashMap<>(6);
            for (FieldDoc fieldDoc : fields) {
                // 打印出字段上的注释
                log.info("字段名称：" + fieldDoc.name() + "字段注释:" + fieldDoc.commentText());
                fieldMap.put(fieldDoc.name(), fieldDoc.commentText());
            }
            map.put(KEY_FIELD, fieldMap);
            MethodDoc[] methodDocs = classDoc.methods();
            // 方法的注释保存在map中
            Map<String, String> methodMap = new HashMap<>(6);
            for (MethodDoc methodDoc : methodDocs) {
                // 打印出方法上的注释
                log.info("方法名称：" + methodDoc.name() + "方法注释:" + methodDoc.commentText());
                methodMap.put(methodDoc.name(), methodDoc.commentText());
            }
            map.put(KEY_METHOD, methodMap);
        }
        return map;
    }

}
