package com.llt.swaggershowdoc.demo.controller;

import com.llt.swaggershowdoc.demo.model.TstReq;
import com.llt.swaggershowdoc.demo.model.TstResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author LILONGTAO
 * @date 2020-09-29
 */
@Api(tags = "tst")
@RequestMapping("tst")
@RestController
public class TstController {

    @ApiOperation("测试")
    @RequestMapping("t")
    public TstResp tst(@RequestBody TstReq tstReq){
        return null;
    }
}
