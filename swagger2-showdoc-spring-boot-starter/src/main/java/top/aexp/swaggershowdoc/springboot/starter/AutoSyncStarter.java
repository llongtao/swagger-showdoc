package top.aexp.swaggershowdoc.springboot.starter;

import io.swagger.models.Swagger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import springfox.documentation.service.Documentation;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper;
import top.aexp.swaggershowdoc.Swagger2ShowDocBuilder;
import top.aexp.swaggershowdoc.config.Config;
import top.aexp.swaggershowdoc.springboot.cons.CommonConstants;
import top.aexp.swaggershowdoc.springboot.properties.SwaggerShowDocProperties;

/**
 * @author LILONGTAO
 * @date 2020-09-29
 */
@Slf4j
@Component
public class AutoSyncStarter implements ApplicationRunner {


    @Autowired
    private DocumentationCache documentationCache;

    @Autowired
    private ServiceModelToSwagger2Mapper mapper;

    @Autowired
    private SwaggerShowDocProperties swaggerShowDocProperties;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!swaggerShowDocProperties.getAutoSync()) {
            log.info("SwaggerShowDoc 不生成文档");
            return;
        }
        if (!checkConfig(swaggerShowDocProperties)) {
            return;
        }
        //noinspection AlibabaAvoidManuallyCreateThread
        new Thread(() -> {
            log.info("SwaggerShowDoc 开始同步文档");
            Documentation documentation = this.documentationCache.documentationByGroup(CommonConstants.DOC_GROUP);
            if (documentation == null) {
                log.warn("SwaggerShowDoc documentation isNull");
                return;
            }
            Swagger swagger = this.mapper.mapDocumentation(documentation);
            swagger.basePath(swaggerShowDocProperties.getBasePath());
            swagger.host(swaggerShowDocProperties.getHost());
            SwaggerShowDocProperties.ShowDoc showDoc = swaggerShowDocProperties.getShowDoc();
            Config config = new Config(showDoc.getUrl(), showDoc.getApiKey(), showDoc.getApiToken());
            config.addModule(swaggerShowDocProperties.getModule(), swagger);
            try{
                Swagger2ShowDocBuilder.start(config);
                log.info("SwaggerShowDoc 同步文档完成:"+config.getShowDocAddr());
            }catch (Exception e){
                log.warn("SwaggerShowDoc 同步文档异常",e);
            }
        }).start();

    }

    private boolean checkConfig(SwaggerShowDocProperties swaggerShowDocProperties) {
        String module = swaggerShowDocProperties.getModule();
        SwaggerShowDocProperties.ShowDoc showDoc = swaggerShowDocProperties.getShowDoc();
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
