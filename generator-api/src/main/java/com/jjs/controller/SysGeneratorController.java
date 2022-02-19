/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.jjs.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.jjs.service.PostManGeneratorService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 代码生成器
 *
 * @author Mark sunlightcs@gmail.com
 */
@Controller
@RequestMapping("/sys/generator")
public class SysGeneratorController {

    @Autowired
    private PostManGeneratorService postManGeneratorService;

    /**
     * 生成代码
     */
    @RequestMapping("/code")
    public void code(HttpServletResponse response) throws IOException {
        String url = "C:\\Users\\l2503\\Desktop\\一网统管.postman_collection.json";
        byte[] data = postManGeneratorService.generator(url);

        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=\"renren.zip\"");
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");

        IOUtils.write(data, response.getOutputStream());
    }
}
