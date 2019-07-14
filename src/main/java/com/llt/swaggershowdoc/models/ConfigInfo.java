package com.llt.swaggershowdoc.models;

import lombok.Data;

import java.util.List;

@Data
public class ConfigInfo {

    private ShowDocConfig showDocConfig;

    private List<SwaggerConfig> swaggerConfigList;

}
