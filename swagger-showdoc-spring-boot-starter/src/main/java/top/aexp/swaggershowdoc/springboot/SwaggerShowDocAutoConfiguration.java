package top.aexp.swaggershowdoc.springboot;

import springfox.documentation.spring.web.SpringfoxWebConfiguration;
import top.aexp.swaggershowdoc.springboot.config.SwaggerShowDocConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author LILONGTAO
 * @date 2020-09-29
 */
@EnableSwagger2
@ComponentScan(basePackages = "top.aexp.swaggershowdoc.springboot")
@ConditionalOnProperty(prefix = "swagger-showdoc", name = "enable", havingValue = "true", matchIfMissing = true)
@Configuration
@EnableConfigurationProperties({SwaggerShowDocConfig.class})
public class SwaggerShowDocAutoConfiguration {

}
