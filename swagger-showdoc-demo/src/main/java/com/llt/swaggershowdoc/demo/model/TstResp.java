package com.llt.swaggershowdoc.demo.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author LILONGTAO
 * @date 2020-09-29
 */
@Data
@ApiModel
public class TstResp {

    @ApiModelProperty("是否成功")
    private boolean succeed;

    @ApiModelProperty("TstResp.bb")
    private String aa;
}
