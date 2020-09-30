package com.llt.swaggershowdoc.springboot.config;

import io.swagger.annotations.ApiOperation;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @author LILONGTAO
 * @date 2020-09-29
 */
@Data
@Component
@EnableOpenApi
@ConfigurationProperties(prefix = "swagger-showdoc")
public class SwaggerShowDocConfig   {

    private Boolean enable = false;

    private Boolean autoSync = true;

    private String basePath;

    private String host;

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


    @Bean
    public Docket createRestApi() {

        return new Docket(DocumentationType.OAS_30).enable(enable)
                .host(host)
                .pathMapping(basePath)
                .apiInfo(apiInfo())
                .select()
                .apis(requestHandler -> requestHandler!=null&&requestHandler.isAnnotatedWith(ApiOperation.class))
                .paths(PathSelectors.any())
                .build();
    }
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(title)
                .version(version)
                .termsOfServiceUrl(basePath)
                .build();
    }

}
