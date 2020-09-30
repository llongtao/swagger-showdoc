package com.llt.swaggershowdoc.models;

import com.fasterxml.jackson.databind.node.TextNode;
import com.llt.swaggershowdoc.util.Jackson;
import lombok.Data;

@Data
public class PropertyModel {

    private String name;
    private boolean required;
    private String type;
    private String description;
    private String example;

    public PropertyModel(String name, boolean required, String type, String description, Object example) {
        this.name = name;
        this.required = required;
        this.type = type;
        this.description = description;
        if (example instanceof TextNode) {
            this.example = ((TextNode) example).asText();
        }else {
            this.example = Jackson.toJSONString(example);
        }

    }
}
