package com.llt.swaggershowdoc.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShowDocConfig {

    @NotEmpty(message = "ShowDoc地址不能为空")
    private String url;

    @NotEmpty(message = "apiKey不能为空")
    private String apiKey;

    @NotEmpty(message = "apiToken不能为空")
    private String apiToken;
}
