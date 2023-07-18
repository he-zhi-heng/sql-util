package com.he.sqlutils.utils;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hemoren
 */
public class FileUtils {
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);



    /**
     * 生成sql文件
     * 
     * @param path 实体类所在的包路径
     */
    public static Set<Class<?>> getAllFiles(String path) {
        checkFile(path);
        Set<Class<?>> classes = new HashSet<>();
        // 获取实体类下的所有类
        // 获取实体类下的所有文件
        File file = new File(path);
        // 获取实体类下的所有文件的类名
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File file2 : files) {
                String fileName = file2.getName();
                String className = fileName.substring(0, fileName.lastIndexOf("."));
                // 获取包名
                String pacString = file2.getParent().replace("\\", ".").replace("/", ".")
                        .substring(file2.getParent().indexOf("com"));
                fileName = pacString + "." + className;
                logger.info("className:{}", fileName);
                Class<?> clazz = ReflectUtil.loadClass(fileName);
                classes.add(clazz);
            }
        }
        if (classes.size() == 0) {
            throw new RuntimeException("没有实体类");
        }
        return classes;
    }

    /**
     * 对path进行校验,
     * 
     * @param path 实体类所在的当前项目下的相对路径
     */
    private static void checkFile(String path) {
        // path校验,如果为空,则默认为当前项目下包下
        // 创建File对象
        File folder = new File(path);
        // 判断文件夹是否存在
        if (!folder.exists()) {
            throw new RuntimeException("文件夹"+path +"不存在");
        } else if (!folder.isDirectory()) {
            throw new RuntimeException(path +"不是文件夹");
        } else {
            logger.info("文件夹存在");
        }
    }

}

