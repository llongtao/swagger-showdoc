package com.llt.swaggershowdoc.models;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.llt.swaggershowdoc.util.ExampleUtil;
import io.swagger.models.*;
import io.swagger.models.auth.SecuritySchemeDefinition;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
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
public class Swagger2 extends Swagger {

    public Swagger2(Swagger swagger) {
        this.info = swagger.getInfo();
        this.host = swagger.getHost();
        this.basePath = swagger.getBasePath();
        this.tags = swagger.getTags();
        this.schemes = swagger.getSchemes();
        this.consumes = swagger.getConsumes();
        this.produces = swagger.getProduces();
        this.security = swagger.getSecurity();
        this.securityDefinitions = swagger.getSecurityDefinitions();
        this.definitions = swagger.getDefinitions();
        this.parameters = swagger.getParameters();
        this.responses = swagger.getResponses();
        this.externalDocs = swagger.getExternalDocs();
        this.vendorExtensions = swagger.getVendorExtensions();
        this.paths = buildPathWithExamples(swagger.getPaths()) ;
    }


    private Map<String, Path> buildPathWithExamples(Map<String, Path> paths) {
        if (paths == null) {
            return null;
        }

        paths.forEach((k,v)->{
            Operation post = v.getPost();
            if (post != null) {
                List<Parameter> parameterList = new ArrayList<>();
                List<Parameter> parameters = post.getParameters();
                if (parameters != null) {
                    parameters.forEach(parameter -> {
                        if (parameter instanceof BodyParameter) {
                            parameterList.add(new BodyParameter2((BodyParameter)parameter,definitions)) ;
                        }else {
                            parameterList.add(parameter);
                        }
                    });
                }
                post.setParameters(parameterList);
                Map<String, Response> responses = post.getResponses();
                Response response = responses.get("200");
                if (response != null) {
                    Model responseSchema = response.getResponseSchema();

                    if (responseSchema instanceof RefModel) {
                        String s = ((RefModel) responseSchema).getSimpleRef();
                        Model model = definitions.get(s);
                        HashMap<String, Object> resultExample = ExampleUtil.getBodyExample(model,definitions);
                        response.setExamples(resultExample);
                    }else if (responseSchema instanceof ArrayModel) {
                        Property items = ((ArrayModel) responseSchema).getItems();
                        List<Object> list = new ArrayList<>();
                        if (items instanceof RefProperty) {
                            Model model = definitions.get(((RefProperty) items).getSimpleRef());
                            HashMap<String, Object> exampleHashMap = ExampleUtil.getBodyExample(model, definitions);
                            list.add(exampleHashMap);
                        } else {
                            list.add(items.getType());
                        }
                        response.setExamples(new ExampleModel(list));
                    }
                }
            }
        });

        return paths;
    }


}
