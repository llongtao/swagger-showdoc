package com.llt.swaggershowdoc.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.llt.swaggershowdoc.util.ExampleUtil;
import io.swagger.models.ArrayModel;
import io.swagger.models.Model;
import io.swagger.models.RefModel;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LILONGTAO
 * @date 2019-07-10
 * @Description
 */
public class BodyParameter2 extends BodyParameter {

    private ExampleModel examples;

    @Override
    @JsonProperty("x-examples")
    public Map getExamples() {
        return examples;
    }

    @Override
    public void setExamples(Map<String, String> examples) {
        this.examples = new ExampleModel(examples);
    }

    public BodyParameter2(BodyParameter bodyParameter, Map<String, Model> definitions) {
        setSchema(bodyParameter.getSchema());
        setIn(bodyParameter.getIn());
        setName(bodyParameter.getName());
        setDescription(bodyParameter.getDescription());
        setRequired(bodyParameter.getRequired());
        setAccess(bodyParameter.getAccess());
        setPattern(bodyParameter.getPattern());
        setVendorExtensions(bodyParameter.getVendorExtensions());
        setIn(bodyParameter.getIn());
        setAccess(bodyParameter.getAccess());
        setName(bodyParameter.getName());
        setDescription(bodyParameter.getDescription());
        setRequired(bodyParameter.getRequired());
        setPattern(bodyParameter.getPattern());


        Model schema = bodyParameter.getSchema();
        if (schema instanceof RefModel) {
            Model model = definitions.get(((RefModel) schema).getSimpleRef());
            this.examples = new ExampleModel(ExampleUtil.getBodyExample(model, definitions,null));
        } else if (schema instanceof ArrayModel) {
            Property items = ((ArrayModel) schema).getItems();
            List list = new ArrayList();
            if (items instanceof RefProperty) {
                Model model = definitions.get(((RefProperty) items).getSimpleRef());
                HashMap<String, Object> exampleHashMap = ExampleUtil.getBodyExample(model, definitions,null);
                list.add(exampleHashMap);
            } else {
                list.add(items.getType());
            }
            this.examples = new ExampleModel(list);
        }

    }


}
