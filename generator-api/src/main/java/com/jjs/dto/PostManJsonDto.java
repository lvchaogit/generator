package com.jjs.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author l2503
 * @date 2022-02-13
 */
@Data
@AllArgsConstructor
public class PostManJsonDto {

    private String name;

    private Request request;

    @Data
    @AllArgsConstructor
    public static class Request {
        private String method;
        private List<ValueItem> header;
        private Body body;
        private String url;
    }

    @Data
    @AllArgsConstructor
    public static class ValueItem {
        private String key;
        private String value;
        private String type;
        private String description;
    }

    @Data
    @AllArgsConstructor
    public static class Body {
        private String mode;
        private List<ValueItem> urlencoded;
        private List<ValueItem> formdata;
        private String type;
        private String raw;
        private Options options;
    }

    @Data
    @AllArgsConstructor
    public static class Options {
        private Raw raw;
    }

    @Data
    @AllArgsConstructor
    public static class Raw {
        private String language;
    }
}
 