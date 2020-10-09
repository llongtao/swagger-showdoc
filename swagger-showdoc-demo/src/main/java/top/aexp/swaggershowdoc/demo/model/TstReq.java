package top.aexp.swaggershowdoc.demo.model;

import top.aexp.swaggershowdoc.demo.enums.Type;
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
