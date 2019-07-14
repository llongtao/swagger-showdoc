package com.llt.swaggershowdoc.web;

import com.llt.swaggershowdoc.models.ConfigInfo;
import com.llt.swaggershowdoc.models.ShowDocConfig;
import com.llt.swaggershowdoc.service.Swagger2ShowDocService;
import com.llt.swaggershowdoc.util.GitUtils;
import io.swagger.models.Swagger;
import io.swagger.parser.Swagger20Parser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
@Controller
public class ApiDocController {

    @Resource
    Swagger2ShowDocService swagger2ShowDocService;

    @RequestMapping("/")
    public String index() {
        return "index.html";
    }

    /**
     * 用于手动上传文档原始文件并且生成文档
     *
     * @return
     */
    @PostMapping("/updateShowDoc")
    @ResponseBody
    public String updateShowDoc(@RequestBody ConfigInfo configInfo) throws IOException {
        swagger2ShowDocService.start(configInfo);
        return "success";
    }



    @PostMapping("/saveConfig")
    @ResponseBody
    public String saveConfig(@RequestBody ConfigInfo configInfo, HttpSession session) {
        session.setAttribute("configInfo",configInfo);
        return "success";
    }

    @GetMapping("/getConfig")
    @ResponseBody
    public ConfigInfo getConfig( HttpSession session) {
        return (ConfigInfo)session.getAttribute("configInfo");
    }


    /**
     * 提供给Git仓库用于 webHook 触发文档自动同步更新
     *
     * @param projectName Git仓库中的项目名称
     * @param ref         分支
     * @param apiKey      showDoc中文档项目的 api_key
     * @param apiToken    showDoc中文档项目的 api_token
     * @param request     用于接收Git仓库中文档生成文件的原始下载地址
     * @return
     */
    @PostMapping("/gitWebHookUpdateShowDoc/{projectName}/{ref}/{api_key}/{api_token}/**")
    public ResponseEntity<String> gitWebHookUpdateShowDoc(@PathVariable("projectName") String projectName,
                                                          @PathVariable("ref") String ref,
                                                          @PathVariable("showDoc_url") String showDocUrl,
                                                          @PathVariable("api_key") String apiKey,
                                                          @PathVariable("api_token") String apiToken,
                                                          @PathVariable("swaggerUiUrl") String swaggerUiUrl,
                                                          HttpServletRequest request) {
        try {


            String filePath = extractPathFromPattern(request);

            String file = GitUtils.downLoadFile(filePath, projectName, ref);

            Swagger swagger = new Swagger20Parser().parse(file);
            swagger2ShowDocService.updateToShowDoc(new ShowDocConfig(showDocUrl,apiKey,apiToken), swagger, swaggerUiUrl);
            return ResponseEntity.ok("gitWebHook触发更新文档成功");
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("gitWebHook触发更新文档失败");

    }

    /**
     * 用于截取请求地址中的特殊参数
     *
     * @param request
     * @return
     */
    private String extractPathFromPattern(final HttpServletRequest request) {
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        return new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, path);
    }
}
