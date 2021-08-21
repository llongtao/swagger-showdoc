package top.aexp.swaggershowdoc.springboot.properties;

import io.swagger.annotations.ApiOperation;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;
import java.util.HashSet;

/**
 * @author LILONGTAO
 * @date 2020-09-29
 */
@Data
@ConfigurationProperties(prefix = "swagger-showdoc")
public class SwaggerShowDocProperties {

    private Boolean enable = false;

    private Boolean autoSync = true;

    private String basePath;

    private String basePkg;

    private String host="localhost:8080";

    private String title;

    private String version;

    private String module;

    private ShowDoc showDoc;


    @Data
    public static class ShowDoc {
        private String url;

        private String apiKey;

        private String apiToken;
    }



}
