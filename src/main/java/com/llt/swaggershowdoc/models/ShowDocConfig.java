package com.llt.swaggershowdoc.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShowDocConfig {

    private String url;

    private String apiKey;

    private String apiToken;
}
