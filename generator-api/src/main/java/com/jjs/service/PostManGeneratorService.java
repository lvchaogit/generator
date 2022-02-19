package com.jjs.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jjs.dto.ApiInfoDto;
import com.jjs.dto.ParamValueDto;
import com.jjs.dto.PostManJsonDto;
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
                apiInfoDto.setRequestParam(transformColumnEntity(query));
            }

            if ("urlencoded".equals(body.getMode())) {
                apiInfoDto.setRequestFormData(transformColumnEntity(body.getUrlencoded()));
            } else if ("formdata".equals(body.getMode())) {
                apiInfoDto.setRequestFormData(transformColumnEntity(body.getFormdata()));
            } else if ("raw".equals(body.getMode()) && "json".equals(body.getOptions().getRaw().getLanguage())) {
                //todo json处理
            }
            log.info("apiName:{}", apiName);
            log.info("method:{}", method);
            log.info("headers:{}", headers);
            log.info("url:{}", url);
            log.info("body:{}", body);
            if (!"http://".startsWith(url)) {
                url = "http://" + url;
            }
            apiInfoDto.setUrl(url);
            String className = GenApiUtils.columnToJava(apiInfoDto.getName());
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
            paramValueDto.setAttrname(valueItem.getKey());
            paramValueDto.setAttrType("String");
            return paramValueDto;
        }).collect(Collectors.toList());
    }

}
 