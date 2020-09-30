package com.llt.swaggershowdoc.demo.model;

import com.llt.swaggershowdoc.demo.enums.Type;
import com.llt.swaggershowdoc.springboot.annotations.DisplayEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author LILONGTAO
 * @date 2020-09-29
 */
@Data
@ApiModel
public class TstReq {
    @ApiModelProperty("TstReq.aa")
    private String aaa;


    @ApiModelProperty("枚举测试")
    private Type type;

}
