package com.llt.swaggershowdoc.server.models;

import lombok.Data;


import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ConfigInfo {

    @Valid
    @NotNull(message = "showDoc配置不能为空")
    private ShowDocBuildConfig showDocConfig;

    @Valid
    @NotEmpty(message = "swagger配置不能为空")
    private List<SwaggerBuildConfig> swaggerConfigList;

}
