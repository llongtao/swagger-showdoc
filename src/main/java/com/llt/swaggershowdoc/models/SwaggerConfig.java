package com.llt.swaggershowdoc.models;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class SwaggerConfig {

    @NotEmpty(message = "模块名不能为空")
    private String module;

    @NotEmpty(message = "swagger地址不能为空")
    private String ip;

    @NotNull(message = "swagger端口不能为空")
    private Integer port;

    //支持新版本swaggerui，group=CustomAPI
    private List<String> params;

    private String path;

}
