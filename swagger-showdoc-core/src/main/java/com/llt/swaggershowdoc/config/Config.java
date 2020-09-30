package com.llt.swaggershowdoc.config;

import com.llt.swaggershowdoc.models.Swagger2;
import io.swagger.models.Swagger;
import io.swagger.parser.Swagger20Parser;
import io.swagger.parser.SwaggerParser;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LILONGTAO
 * @date 2020-09-29
 */
@Data
public class Config {

    private String showDocUrl;


    private String showDocApiKey;


    private String showDocApiToken;

    private List<SwaggerConfig> swaggerConfigList;

    public Config(String showDocUrl, String showDocApiKey, String showDocApiToken) {
        this.showDocUrl = showDocUrl;
        this.showDocApiKey = showDocApiKey;
        this.showDocApiToken = showDocApiToken;
        this.swaggerConfigList = new ArrayList<>();
    }

    public Config addModule(String module,String swaggerStr){
        try {

            return addModule(module,new Swagger20Parser().parse(swaggerStr));
        } catch (Exception e) {
            throw new RuntimeException("swagger解析失败:"+swaggerStr,e);
        }
    }
    public Config addModule(String module, Swagger swagger){
        swaggerConfigList.add(new SwaggerConfig(module,swagger));
        return this;
    }

    public String getShowDocUrl() {
        if (!showDocUrl.contains("www.showdoc.cc")) {
            return  "http://" + showDocUrl + "/server/index.php?s=/api/item/updateByApi";
        } else {
            return  "https://www.showdoc.cc/server/api/item/updateByApi";
        }
    }

    public String getShowDocAddr() {
        return  "http://" + showDocUrl ;
    }
}
