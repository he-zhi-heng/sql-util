package com.he.sqlutils.pojo.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 表信息实体类
 * @author hemoren
 */
public class Table {
    private List<Column> columns = new ArrayList<>();

    private String name;

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public String getName() {
        return name;
    }

    public void setName(String string) {
        this.name = string;
    }

}
