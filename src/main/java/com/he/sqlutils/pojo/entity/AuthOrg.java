package com.he.sqlutils.pojo.entity;

import java.io.Serializable;
import java.sql.Date;


/**
 * 组织表
 * @author hemoren
 */
public class AuthOrg implements Serializable {


    /**
     * 组织名称
     */
    String name;

    /**
     * 父级组织id
     */
    Long parentId;
        /**
     * 创建时间
     */
    Date createTime;
    /**
     * 更新时间
     */
    Date updateTime;

}
