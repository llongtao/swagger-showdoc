package com.llt.swaggershowdoc.springboot.starter;

import com.llt.swaggershowdoc.Swagger2ShowDocBuilder;
import com.llt.swaggershowdoc.config.Config;
import com.llt.swaggershowdoc.springboot.config.SwaggerShowDocConfig;
import io.swagger.models.Swagger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import springfox.documentation.service.Documentation;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.json.JsonSerializer;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper;

/**
 * @author LILONGTAO
 * @date 2020-09-29
 */
@Slf4j
@DependsOn("swaggerShowDocConfig")
@Component
public class AutoSyncStarter implements ApplicationRunner {


    @Autowired
    private DocumentationCache documentationCache;

    @Autowired
    private ServiceModelToSwagger2Mapper mapper;

    @Autowired
    private SwaggerShowDocConfig swaggerShowDocConfig;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!swaggerShowDocConfig.getAutoSync()) {
            log.info("SwaggerShowDoc 不生成文档");
            return;
        }
        if (!checkConfig(swaggerShowDocConfig)) {
            return;
        }
        new Thread(() -> {
            log.info("SwaggerShowDoc 开始同步文档");
            Documentation documentation = this.documentationCache.documentationByGroup("default");
            if (documentation == null) {
                log.warn("SwaggerShowDoc documentation isNull");
                return;
            }
            Swagger swagger = this.mapper.mapDocumentation(documentation);
            swagger.basePath(swaggerShowDocConfig.getBasePath());
            swagger.host(swaggerShowDocConfig.getHost());
            SwaggerShowDocConfig.ShowDoc showDoc = swaggerShowDocConfig.getShowDoc();
            Config config = new Config(showDoc.getUrl(), showDoc.getApiKey(), showDoc.getApiToken());
            config.addModule(swaggerShowDocConfig.getModule(), swagger);
            try{
                Swagger2ShowDocBuilder.start(config);
                log.info("SwaggerShowDoc 同步文档完成:"+config.getShowDocAddr());
            }catch (Exception e){
                log.warn("SwaggerShowDoc 同步文档异常",e);
            }
        }).start();

    }

    private boolean checkConfig(SwaggerShowDocConfig swaggerShowDocConfig) {
        String module = swaggerShowDocConfig.getModule();
        SwaggerShowDocConfig.ShowDoc showDoc = swaggerShowDocConfig.getShowDoc();
        if (StringUtils.isEmpty(module)) {
            log.warn("SwaggerShowDoc module为空,无法构建");
            return false;
        }
        if (showDoc == null) {
            log.warn("SwaggerShowDoc showDoc为空,无法构建");
            return false;
        }
        if (StringUtils.isEmpty(showDoc.getUrl())) {
            log.warn("SwaggerShowDoc showDoc.url为空,无法构建");
            return false;
        }
        if (StringUtils.isEmpty(showDoc.getApiKey())) {
            log.warn("SwaggerShowDoc showDoc.apiKey为空,无法构建");
            return false;
        }
        if (StringUtils.isEmpty(showDoc.getApiToken())) {
            log.warn("SwaggerShowDoc showDoc.apiToken为空,无法构建");
            return false;
        }

        return true;
    }

}
