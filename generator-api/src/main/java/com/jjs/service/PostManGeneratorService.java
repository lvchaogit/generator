package com.jjs.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jjs.dto.ApiInfoDto;
import com.jjs.dto.BodyValueDto;
import com.jjs.dto.ParamValueDto;
import com.jjs.dto.PostManJsonDto;
import com.jjs.utils.AttrTypeEnum;
import com.jjs.utils.GenApiUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

/**
 * @author l2503
 * @date 2022-02-13
 */
@Service
@Slf4j
public class PostManGeneratorService {

    public byte[] generator(String fileUrl) {
        JSONObject jsonObject = JSONUtil.readJSONObject(new File(fileUrl), StandardCharsets.UTF_8);
        JSONArray itemArray = jsonObject.getJSONArray("item");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        List<ApiInfoDto> list = new ArrayList<>();
        itemArray.forEach(o -> {
            JSONObject apiObject = (JSONObject)o;
            String apiName = apiObject.getStr("name");
            JSONObject requestObject = apiObject.getJSONObject("request");
            String method = requestObject.getStr("method");
            List<PostManJsonDto.ValueItem> headers = requestObject.getBeanList("header",
                PostManJsonDto.ValueItem.class);
            PostManJsonDto.Body body = requestObject.getBean("body", PostManJsonDto.Body.class);
            String url = "";
            ApiInfoDto apiInfoDto = new ApiInfoDto();
            apiInfoDto.setName(apiName);
            apiInfoDto.setMethod(method);
            //设置请求头
            apiInfoDto.setRequestHeader(transformColumnEntity(headers));

            if (requestObject.getObj("url") instanceof String) {
                url = requestObject.getStr("url");
            } else {
                JSONObject urlJsonObject = requestObject.getJSONObject("url");
                url = urlJsonObject.getStr("raw").split("\\?")[0];
                List<PostManJsonDto.ValueItem> query = urlJsonObject.getBeanList("query",
                    PostManJsonDto.ValueItem.class);
                log.info("query:{}", query);
                if (query != null) {
                    apiInfoDto.setRequestParam(transformColumnEntity(query));
                }

            }
            if (body != null) {
                if ("urlencoded".equals(body.getMode())) {
                    apiInfoDto.setRequestFormData(transformColumnEntity(body.getUrlencoded()));
                } else if ("formdata".equals(body.getMode())) {
                    apiInfoDto.setRequestFormData(transformColumnEntity(body.getFormdata()));
                } else if ("raw".equals(body.getMode()) && "json".equals(body.getOptions().getRaw().getLanguage())) {
                    List<BodyValueDto> bodyValueDto = new ArrayList<>();
                    transformBody(bodyValueDto, JSONUtil.parseObj(body.getRaw()));
                    log.info("bodyStr:{}", JSONUtil.toJsonStr(bodyValueDto));
                    apiInfoDto.setRequestBody(bodyValueDto);
                }
            }

            if (!url.startsWith("http://")) {
                url = "http://" + url;
            }
            apiInfoDto.setUrl(url);
            String className = GenApiUtils.classToJava(apiInfoDto.getName());
            apiInfoDto.setClassName(className);
            list.add(apiInfoDto);
            log.info("apiInfoDto:{}", apiInfoDto);
        });
        GenApiUtils.generatorCode(list, zip);
        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }

    private static List<ParamValueDto> transformColumnEntity(List<PostManJsonDto.ValueItem> postValue) {
        return postValue.stream().map(valueItem -> {
            ParamValueDto paramValueDto = new ParamValueDto();
            paramValueDto.setAttrname(GenApiUtils.columnToJava(valueItem.getKey()));
            paramValueDto.setAttrType(AttrTypeEnum.String.getCode());
            return paramValueDto;
        }).collect(Collectors.toList());
    }

    private static void transformBody(List<BodyValueDto> bodyValueDtoList,
                                      JSONObject jsonObject) {
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            BodyValueDto bodyValueDto = new BodyValueDto();
            String key = entry.getKey();
            Object value = entry.getValue();
            bodyValueDto.setAttrname(GenApiUtils.columnToJava(key));
            if (value instanceof JSONObject) {
                bodyValueDto.setIsArray(false);
                bodyValueDto.setAttrType(GenApiUtils.classToJava(key));
                bodyValueDto.setProperties(new ArrayList<>());
                transformBody(bodyValueDto.getProperties(), (JSONObject)value);
            } else if (value instanceof Integer) {
                //列名转换成Java属性名
                bodyValueDto.setIsArray(false);
                bodyValueDto.setAttrType(AttrTypeEnum.Integer.getCode());
            } else if (value instanceof JSONArray) {
                bodyValueDto.setIsArray(true);
                JSONArray array = (JSONArray)value;
                if (array.size() > 0) {
                    if (array.getObj(0) instanceof String) {
                        bodyValueDto.setAttrType(AttrTypeEnum.String.getCode());
                    } else if (array.getObj(0) instanceof Integer) {
                        bodyValueDto.setAttrType(AttrTypeEnum.Integer.getCode());
                    } else {
                        bodyValueDto.setAttrType(GenApiUtils.classToJava(key));
                        bodyValueDto.setProperties(new ArrayList<>());
                        transformBody(bodyValueDto.getProperties(), array.getJSONObject(0));
                    }
                } else {
                    bodyValueDto.setAttrType(AttrTypeEnum.Object.getCode());
                }
            } else if (value instanceof String) {
                bodyValueDto.setIsArray(false);
                bodyValueDto.setAttrType(AttrTypeEnum.String.getCode());
            }
            bodyValueDtoList.add(bodyValueDto);
        }
    }
}
 