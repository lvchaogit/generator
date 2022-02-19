package com.jjs.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author l2503
 * @date 2022-02-19
 */
@AllArgsConstructor
@Getter
public enum AttrTypeEnum {

    /**
     * 字符串类型
     */
    String("String"),
    /**
     * 数值类型
     */
    Integer("Integer"),
    /**
     * 对象类型
     */
    Object("Object");

    private String code;
}
