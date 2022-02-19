package com.jjs.dto;

import java.util.List;

import lombok.Data;

/**
 * @author lvchao
 * @date 2022-02-13
 */
@Data
public class ApiInfoDto {

    private String name;
    private String url;
    private String method;

    private List<ParamValueDto> requestHeader;
    private List<ParamValueDto> requestParam;
    private List<ParamValueDto> requestFormData;
    private List<ParamValueDto> requestBody;

    private String className;

}
 