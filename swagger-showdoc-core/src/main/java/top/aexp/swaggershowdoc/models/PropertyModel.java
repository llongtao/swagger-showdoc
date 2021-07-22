package top.aexp.swaggershowdoc.models;

import com.fasterxml.jackson.databind.node.TextNode;
import io.swagger.models.parameters.AbstractSerializableParameter;
import io.swagger.models.parameters.BodyParameter;
import top.aexp.swaggershowdoc.util.Jackson;
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

    public PropertyModel(BodyParameter bp, String type) {
        name = bp.getName();
        required = bp.getRequired();
        this.type = type;
        description = bp.getDescription();
        example = bp.getExamples() == null ? "-" : bp.getExamples().toString();
    }

    public PropertyModel(AbstractSerializableParameter parameter) {
        name = parameter.getName();
        required = parameter.getRequired();
        this.type = parameter.getType();
        description = parameter.getDescription();
        example = parameter.getExample() == null ? "-" : parameter.getExample().toString();
    }
}
