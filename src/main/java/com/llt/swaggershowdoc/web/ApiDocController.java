package com.llt.swaggershowdoc.web;

import com.llt.swaggershowdoc.models.BaseResponse;
import com.llt.swaggershowdoc.models.ConfigInfo;
import com.llt.swaggershowdoc.service.Swagger2ShowDocService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

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
     */
    @ResponseBody
    @PostMapping("/updateShowDoc")
    public BaseResponse updateShowDoc(@RequestBody @Validated ConfigInfo configInfo) {
        try{
            swagger2ShowDocService.start(configInfo);
        }catch (Exception e){
            e.printStackTrace();
            return BaseResponse.error(e.getMessage());
        }

        return BaseResponse.ok();
    }

}
