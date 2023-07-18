package com.he.sqlutils.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.he.sqlutils.pojo.entity.Table;

/**
 * @author hemoren
 */
public class CreateSqlFileUtils {
    private static final Logger logger = LoggerFactory.getLogger(CreateSqlFileUtils.class);
    /**
     * 文件夹路径,默认在resources/sql/下
     */
    private static final String FILE_PATH = "src/main/resources/sql/";
    /**
     * 文件类型,默认为.sql
     */
    private static final String FILE_TYPE = ".sql";

    /**
     * 生成sql文件,对path进行校验,
     * @param path 实体类所在的当前项目下的相对路径
     */
    public static void generater(String path){
        //path校验,如果为空,则默认为当前项目下包下
        // 创建File对象
        File folder = new File(path);
        
        // 判断文件夹是否存在
        if (!folder.exists()) {
           throw new RuntimeException("文件夹不存在");
        } else if (!folder.isDirectory()) {
            throw new RuntimeException("不是文件夹");
        } else {
            logger.info("文件夹存在");
        }
        createSqlFile(path);
    }

    /**
     * 生成sql文件
     * @param path 实体类所在的包路径
     */
    public static void createSqlFile(String path) {
        //获取实体类下的所有类
        //获取实体类下的所有文件
        File file = new File(path);
        //获取实体类下的所有文件的类名
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File file2 : files) {
                String fileName = file2.getName();
                String className = fileName.substring(0, fileName.lastIndexOf("."));
                //获取包名
                String pacString = file2.getParent().replace("\\", ".").replace("/",".").substring(file2.getParent().indexOf("com"));
                fileName =pacString + "." + className;
                logger.info("className:{}", fileName);
                Class<?> clazz = ReflectUtil.loadClass(fileName);
                createSqlFile(clazz);
            }
        }
    }

    /**
     * 生成sql文件
     * @param clazz 实体类
     */
    public static void createSqlFile(Class<?> clazz) {
        Table table = ClassToTableConverterUtils.convert(clazz);
        String sqlString = SqlGeneratorUtils.generateByString(table);
        String name = table.getName();
        // 文件生成在resources/sql/下
        String fileName = FILE_PATH + name + FILE_TYPE;
        // 创建文件夹
        File file = new File(fileName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        createSqlFile(sqlString, fileName);
    }

    /**
     * 生成sql文件
     * 
     * @param sqlString
     * @param fileName
     */
    private static void createSqlFile(String sqlString, String fileName) {
        logger.info("fileName:{}", fileName);
        File file = new File(fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 将sqlString写入到文件中
        try (
                FileOutputStream fStream = new FileOutputStream(file);
                OutputStreamWriter writer = new OutputStreamWriter(fStream, StandardCharsets.UTF_8);
                PrintWriter pw = new PrintWriter(writer);) {
            pw.write(sqlString);
            pw.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    public static void main(String[] args) {
         String path = "src\\main\\java\\com\\he\\nacosapi\\pojo";
        generater(path);
    }

}
