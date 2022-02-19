package com.jjs.utils;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.jjs.dto.ApiInfoDto;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

/**
 * 代码生成器   工具类
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年12月19日 下午11:40:24
 */
public class GenApiUtils {

    /**
     * 生成代码
     */
    public static void generatorCode(List<ApiInfoDto> apiInfoDtos, ZipOutputStream zip) {
        //配置信息
        Configuration config = getConfig();

        //设置velocity资源加载器
        Properties prop = new Properties();
        prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(prop);
        String mainPath = config.getString("mainPath");
        mainPath = StringUtils.isBlank(mainPath) ? "io.renren" : mainPath;
        //封装模板数据
        Map<String, Object> map = new HashMap<>();
        map.put("apis", apiInfoDtos);
        map.put("mainPath", mainPath);
        map.put("package", config.getString("package"));
        map.put("moduleName", config.getString("moduleName"));
        map.put("author", config.getString("author"));
        map.put("email", config.getString("email"));
        map.put("datetime", DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
        generatorRequestDto(apiInfoDtos, zip, map, config);
        generatorCallApi(zip, map, config);
    }

    public static void generatorRequestDto(List<ApiInfoDto> apiInfoDtos, ZipOutputStream zip,
                                           Map<String, Object> map, Configuration config) {
        VelocityContext context;
        for (ApiInfoDto apiInfoDto : apiInfoDtos) {
            map.put("api", apiInfoDto);
            context = new VelocityContext(map);
            String template = "template/Request.java.vm";
            //渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, "UTF-8");
            tpl.merge(context, sw);

            try {
                //添加到zip
                zip.putNextEntry(new ZipEntry(
                    getFileName(template, apiInfoDto.getClassName(), config.getString("package"),
                        config.getString("moduleName"))));
                IOUtils.write(sw.toString(), zip, "UTF-8");
                IOUtils.closeQuietly(sw);
                zip.closeEntry();
            } catch (IOException e) {
                throw new RRException("渲染模板失败，表名：" + apiInfoDto.getName(), e);
            }
        }
    }

    public static void generatorCallApi(ZipOutputStream zip,
                                        Map<String, Object> map, Configuration config) {
        VelocityContext context = new VelocityContext(map);
        String template = "template/CallApiService.java.vm";
        //渲染模板
        StringWriter sw = new StringWriter();
        Template tpl = Velocity.getTemplate(template, "UTF-8");
        tpl.merge(context, sw);

        try {
            //添加到zip
            zip.putNextEntry(new ZipEntry(
                getFileName(template, "CallApiService", config.getString("package"),
                    config.getString("moduleName"))));
            IOUtils.write(sw.toString(), zip, "UTF-8");
            IOUtils.closeQuietly(sw);
            zip.closeEntry();
        } catch (IOException e) {
            throw new RRException("渲染模板失败，表名：CallApiService", e);
        }
    }

    /**
     * 列名转换成Java属性名
     */
    public static String columnToJava(String columnName) {
        return WordUtils.capitalizeFully(columnName, null);
    }

    /**
     * 获取配置信息
     */
    public static Configuration getConfig() {
        try {
            return new PropertiesConfiguration("generator.properties");
        } catch (ConfigurationException e) {
            throw new RRException("获取配置文件失败，", e);
        }
    }

    /**
     * 获取文件名
     */
    public static String getFileName(String template, String className, String packageName, String moduleName) {
        String packagePath = "main" + File.separator + "java" + File.separator;
        if (StringUtils.isNotBlank(packageName)) {
            packagePath += packageName.replace(".", File.separator) + File.separator + moduleName + File.separator;
        }
        if (template.contains("CallApiService.java.vm")) {
            return packagePath + "service" + File.separator + "CallApiService.java";
        }
        if (template.contains("Request.java.vm")) {
            return packagePath + "request" + File.separator + className + "Request.java";
        }
        return null;
    }
}
