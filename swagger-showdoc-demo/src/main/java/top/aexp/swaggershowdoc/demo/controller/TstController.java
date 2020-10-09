package top.aexp.swaggershowdoc.demo.controller;

import top.aexp.swaggershowdoc.demo.model.TstReq;
import top.aexp.swaggershowdoc.demo.model.TstResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

/**
 * @author LILONGTAO
 * @date 2020-09-29
 */
@Api(tags = "tst")
@RequestMapping("tst")
@RestController
public class TstController {

    @ApiOperation("测试post")
    @PostMapping("t")
    public TstResp tst(@RequestBody TstReq tstReq){
        return null;
    }

    @ApiOperation("测试get")
    @GetMapping("t")
    public TstResp tstGet(@ApiParam("用户名") String username,@ApiParam("密码") String password){
        return null;
    }

    @ApiOperation("测试")
    @RequestMapping("t2")
    public TstResp tstAll(@RequestBody TstReq tstReq){
        return null;
    }
}
