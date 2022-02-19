package com.jjs.dto;

import lombok.Data;

/**
 * 请求参数属性
 *
 * @author lvchao
 * @date 2022.2.19
 */
@Data
public class ParamValueDto {

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

}
