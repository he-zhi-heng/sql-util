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

import com.he.sqlutils.commons.Constants;
import com.he.sqlutils.pojo.entity.Table;

/**
 * @author hemoren
 */
public class CreateSqlFileUtils {
    private static final Logger logger = LoggerFactory.getLogger(CreateSqlFileUtils.class);
    /**
     * 生成sql文件,对path进行校验,
     * @param path 实体类所在的当前项目下的相对路径
     */
    public static void generater(String path){
        FileUtils.getAllFiles(path).forEach(CreateSqlFileUtils::createSqlFile);
    }

    /**
     * 生成sql文件
     * @param clazz 实体类
     */
    private static void createSqlFile(Class<?> clazz) {
        Table table = ClassToTableConverterUtils.convert(clazz);
        String sqlString = SqlGeneratorUtils.generateByString(table);
        String name = table.getName();
        // 文件生成在resources/sql/下
        String fileName = Constants.FILE_PATH + name + Constants.FILE_TYPE;
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

}
