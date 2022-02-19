package com.jjs.dto;

import java.util.List;

import lombok.Data;

/**
 * 请求参数属性
 *
 * @author lvchao
 * @date 2022.2.19
 */
@Data
public class BodyValueDto {

    /**
     * 字段备注
     */
    private String comments;

    /**
     * 属性名称(第一个字母大写)，如：user_name => UserName
     */
    private String attrName;
    /**
     * 属性名称(第一个字母小写)，如：user_name => userName
     */
    private String attrname;
    /**
     * 属性类型
     */
    private String attrType;

    private Boolean isArray;
    /**
     * 属性字段
     */
    private List<BodyValueDto> properties;

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getAttrname() {
        return attrname;
    }

    public void setAttrname(String attrname) {
        this.attrname = attrname;
    }

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public String getAttrType() {
        return attrType;
    }

    public void setAttrType(String attrType) {
        this.attrType = attrType;
    }
}
