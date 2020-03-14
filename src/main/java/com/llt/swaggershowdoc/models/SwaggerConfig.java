package com.llt.swaggershowdoc.models;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class SwaggerConfig {

    @NotEmpty(message = "模块名不能为空")
    private String module;

    @NotEmpty(message = "swagger地址不能为空")
    private String ip;

    @NotEmpty(message = "swagger端口不能为空")
    private Integer port;

    private String path;

}
