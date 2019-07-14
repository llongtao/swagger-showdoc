package com.llt.swaggershowdoc.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyModel {

    private String name;
    private boolean required;
    private String type;
    private String description;
    private String example;
}
