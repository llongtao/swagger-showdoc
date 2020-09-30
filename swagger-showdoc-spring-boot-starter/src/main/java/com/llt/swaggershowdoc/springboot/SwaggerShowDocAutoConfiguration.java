package com.llt.swaggershowdoc.springboot;

import com.llt.swaggershowdoc.springboot.config.SwaggerShowDocConfig;
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
@ComponentScan(basePackages = "com.llt.swaggershowdoc.springboot")
@ConditionalOnProperty(prefix = "swagger-showdoc", name = "enable", havingValue = "true", matchIfMissing = true)
@Configuration
@EnableConfigurationProperties({SwaggerShowDocConfig.class})
public class SwaggerShowDocAutoConfiguration {
}
