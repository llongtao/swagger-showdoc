package com.llt.swaggershowdoc.web;

import com.llt.swaggershowdoc.models.ConfigInfo;
import com.llt.swaggershowdoc.service.Swagger2ShowDocService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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
        session.setMaxInactiveInterval(-1);
        return "success";
    }

    @GetMapping("/getConfig")
    @ResponseBody
    public ConfigInfo getConfig( HttpSession session) {
        return (ConfigInfo)session.getAttribute("configInfo");
    }


}
