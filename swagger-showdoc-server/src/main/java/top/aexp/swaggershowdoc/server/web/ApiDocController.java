package top.aexp.swaggershowdoc.server.web;

import top.aexp.swaggershowdoc.Swagger2ShowDocBuilder;
import top.aexp.swaggershowdoc.config.Config;
import top.aexp.swaggershowdoc.server.models.BaseResponse;
import top.aexp.swaggershowdoc.server.models.ConfigInfo;
import top.aexp.swaggershowdoc.server.models.ShowDocBuildConfig;
import top.aexp.swaggershowdoc.server.models.SwaggerBuildConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

@Slf4j
@Controller
public class ApiDocController {

    @RequestMapping("/")
    public String index() {
        return "index.html";
    }

    @ResponseBody
    @RequestMapping("/aa")
    public Optional<Integer> aa() {
        return Optional.ofNullable(null);
    }

    private RestTemplate restTemplate = new RestTemplate();
    /**
     * 用于手动上传文档原始文件并且生成文档
     *
     */
    @ResponseBody
    @PostMapping(value = "/updateShowDoc",produces="application/json;charset=UTF-8",consumes="application/json;charset=UTF-8")
    public BaseResponse updateShowDoc(@RequestBody @Validated ConfigInfo configInfo) throws MalformedURLException {
        ShowDocBuildConfig sdConfig = configInfo.getShowDocConfig();
        List<SwaggerBuildConfig> swaggerConfigList = configInfo.getSwaggerConfigList();

        Config config = new Config(sdConfig.getUrl(), sdConfig.getApiKey(), sdConfig.getApiToken());

        addModule(swaggerConfigList, config);

        try{
            Swagger2ShowDocBuilder.start(config);
        }catch (Exception e){
            log.error("执行失败",e);
            return BaseResponse.error(e.getMessage());
        }

        return BaseResponse.ok();
    }

    private void addModule(List<SwaggerBuildConfig> swaggerConfigList, Config config) throws MalformedURLException {
        for (SwaggerBuildConfig swaggerBuildConfig : swaggerConfigList) {
            Integer port = swaggerBuildConfig.getPort();
            if (port == null) {
                port = 80;
            }
            String path = swaggerBuildConfig.getPath();
            if (StringUtils.isEmpty(path)) {
                path = Strings.EMPTY;
            } else {
                path = "/" + path;
            }
            path += "/v2/api-docs";
            /**
             * 兼容新版本swaggerui
             */
            if (!CollectionUtils.isEmpty(swaggerBuildConfig.getParams())) {
                String params = "";
                for (String param : swaggerBuildConfig.getParams()) {
                    if (!StringUtils.isEmpty(param)) {
                        if (StringUtils.isEmpty(params)) {
                            params = param;
                        } else {
                            params += ("&" + param);
                        }
                    }
                }
                if (!StringUtils.isEmpty(params)) {
                    path += ("?" + params);
                }
            }
            URL url = new URL("http", swaggerBuildConfig.getIp(), port, path);
            restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
            Callable<String> callable = () -> restTemplate.getForObject(url.toString(), String.class);
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<String> submit = executor.submit(callable);
            String swaggerStr = null;
            try {
                swaggerStr = submit.get(3, TimeUnit.SECONDS);
            } catch (Exception e) {
                throw new RuntimeException("连接超时");
            }

            assert swaggerStr != null;
            config.addModule(swaggerBuildConfig.getModule(), swaggerStr);
        }
    }

}
