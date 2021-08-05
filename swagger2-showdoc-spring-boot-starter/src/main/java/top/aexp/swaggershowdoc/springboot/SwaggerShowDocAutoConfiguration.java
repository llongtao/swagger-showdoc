package top.aexp.swaggershowdoc.springboot;

import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.bean.validators.plugins.Validators;
import springfox.bean.validators.plugins.schema.NotNullAnnotationPlugin;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import top.aexp.swaggershowdoc.springboot.cons.CommonConstants;
import top.aexp.swaggershowdoc.springboot.properties.SwaggerShowDocProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashSet;

/**
 * @author LILONGTAO
 * @date 2020-09-29
 */
@EnableSwagger2
@ComponentScan(basePackages = "top.aexp.swaggershowdoc.springboot")
@ConditionalOnProperty(prefix = "swagger-showdoc", name = "enable", havingValue = "true", matchIfMissing = true)
@Configuration
@EnableConfigurationProperties({SwaggerShowDocProperties.class})
public class SwaggerShowDocAutoConfiguration {

    @Resource
    SwaggerShowDocProperties swaggerShowDocProperties;

    @Bean
    public Docket createSwaggerShowdocRestApi() {

        return new Docket(DocumentationType.SWAGGER_2).enable(swaggerShowDocProperties.getEnable())
                .groupName(CommonConstants.DOC_GROUP)
                .host(swaggerShowDocProperties.getHost())
                .pathMapping(swaggerShowDocProperties.getBasePath())
                .forCodeGeneration(true)
                .protocols(new HashSet<>(Collections.singletonList("http")))
                .apiInfo(apiInfo())
                .select()
                .apis(requestHandler -> requestHandler!=null&&requestHandler.isAnnotatedWith(ApiOperation.class))
                .apis(RequestHandlerSelectors.basePackage(swaggerShowDocProperties.getBasePkg()))
                .paths(PathSelectors.any())
                .build();
    }
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(swaggerShowDocProperties.getTitle())
                .version(swaggerShowDocProperties.getVersion())
                .termsOfServiceUrl(swaggerShowDocProperties.getBasePath())
                .build();
    }

    @Bean
    @Order(Validators.BEAN_VALIDATOR_PLUGIN_ORDER)
    public NotNullAnnotationPlugin propertyNotNullPlugin() {
        return new NotNullAnnotationPlugin();
    }
}
