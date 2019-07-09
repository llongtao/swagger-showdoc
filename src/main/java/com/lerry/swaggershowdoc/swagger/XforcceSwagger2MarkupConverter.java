package com.lerry.swaggershowdoc.swagger;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.github.swagger2markup.Swagger2MarkupConverter;
import io.github.swagger2markup.internal.document.DefinitionsDocument;
import io.github.swagger2markup.internal.document.OverviewDocument;
import io.github.swagger2markup.internal.document.PathsDocument;
import io.github.swagger2markup.internal.document.SecurityDocument;
import io.github.swagger2markup.markup.builder.MarkupDocBuilder;
import io.swagger.models.*;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.properties.*;
import joptsimple.internal.Strings;

import java.lang.reflect.Array;
import java.util.*;

public class XforcceSwagger2MarkupConverter extends Swagger2MarkupConverter {


    private final Context context;
    private final OverviewDocument overviewDocument;
    private final PathsDocument pathsDocument;
    private final DefinitionsDocument definitionsDocument;
    private final SecurityDocument securityDocument;
    private Map<String, Model> definitions;

    public XforcceSwagger2MarkupConverter(Context context) {
        super(context);
        this.context = context;
        this.overviewDocument = new OverviewDocument(context);
        this.pathsDocument = new PathsDocument(context);
        this.definitionsDocument = new DefinitionsDocument(context);
        this.securityDocument = new SecurityDocument(context);
    }

    public XforcceSwagger2MarkupConverter(Context context, Map<String, Model> definitions) {
        this(context);
        this.definitions = definitions;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        ;


        sb.append(applyPathsDocument().toString());
        int i = sb.indexOf("#### 消耗");
        int j = sb.indexOf("#### HTTP请求示例");
        sb.replace(i, j, Strings.EMPTY);
        int i1 = sb.indexOf("<a name=\"paths\"></a>");
        int j1 = sb.indexOf("```");
        sb.replace(i1, j1, Strings.EMPTY);

        int i2 = sb.indexOf("```");
        sb.replace(i2, i2 + 3, "#### 请求URL：");
        int i4 = sb.indexOf("POST");
        int i5 = sb.indexOf("```");
        String url = null;
        if (i5 > i4 + 5) {
            String substring = sb.substring(i4 + 5, i5);
            url = substring.trim();
        }

        sb.replace(i5, i5 + 3, Strings.EMPTY);

        int i6 = sb.indexOf("##### 请求 body");
        if (i6 < 0 && url != null) {
            int i7 = sb.indexOf("#### HTTP响应示例");

            Operation post = context.getSwagger().getPaths().get(url).getPost();
            List<Parameter> parameters = null;
            if (post == null) {
                parameters = post.getParameters();
            }

            BodyParameter bodyParameter = null;
            for (Parameter parameter : parameters) {
                if (parameter instanceof BodyParameter) {
                    bodyParameter = (BodyParameter) parameter;
                }

            }
            if (bodyParameter != null) {


                RefModel schema = (RefModel) bodyParameter.getSchema();
                String s = schema.getSimpleRef();
                Model model = definitions.get(s);
                HashMap<String, Object> resultExample = getExampleHashMap(model,definitions);


                String s1 = JSON.toJSONString(resultExample, SerializerFeature.PrettyFormat);
                sb.insert(i7 - 1, "##### 请求 body\n```json\n" + s1 + "```");
            }

        }else {
            int i3 = sb.indexOf("[ null ]");
            if (i3 > 0 && url != null) {
                int i7 = sb.indexOf("#### HTTP响应示例");
                if (i3 < i7) {
                    List<Parameter> parameters = context.getSwagger().getPaths().get(url).getPost().getParameters();


                    BodyParameter bodyParameter = null;
                    for (Parameter parameter : parameters) {
                        if (parameter instanceof BodyParameter) {
                            bodyParameter = (BodyParameter) parameter;
                        }

                    }
                    if (bodyParameter != null) {
                        ArrayModel schema = (ArrayModel) bodyParameter.getSchema();
                        Property items = schema.getItems();
                        String body;
                        if (items instanceof RefProperty) {

                            Model model = definitions.get(((RefProperty) items).getSimpleRef());
                            HashMap<String, Object> exampleHashMap = getExampleHashMap(model, definitions);
                            List list = new ArrayList();
                            list.add(exampleHashMap);
                            body=JSON.toJSONString(list,SerializerFeature.PrettyFormat);
                        }else {
                            List list = new ArrayList();
                            list.add(items.getType());
                            body=JSON.toJSONString(list,SerializerFeature.PrettyFormat);
                        }
                        sb.replace(i3, i3+8,body);
                    }
                }
            }
        }


        int i7 = sb.indexOf("##### 响应 200");

        int i8 = sb.indexOf("\"[", i7);

        int i9 = sb.indexOf(")\"", i7);
        if (i8 > 0 && i8 < i9) {
            Response response = context.getSwagger().getPaths().get(url).getPost().getResponses().get("200");
            RefModel schema =(RefModel) response.getResponseSchema();

            String s = schema.getSimpleRef();
            Model model = definitions.get(s);
            HashMap<String, Object> resultExample = getExampleHashMap(model,definitions);


            String s1 = JSON.toJSONString(resultExample, SerializerFeature.PrettyFormat);

            sb.replace(i8,i9+2, s1 );
        }

        return sb.toString();
    }

    private HashMap<String, Object> getExampleHashMap(Model model,Map<String, Model> definitions) {
        Map<String, Property> properties = model.getProperties();
        HashMap<String, Object> resultExample = new HashMap<>(16);
        for (String key : properties.keySet()) {

            Property property = properties.get(key);
            String type = property.getType();
            if (property instanceof DateTimeProperty) {
                resultExample.put(key,"yyyy-MM-dd HH:mm:ss");
            }else if (property instanceof DateProperty) {
                resultExample.put(key,"yyyy-MM-dd");
            }else if (property instanceof BooleanProperty) {
                resultExample.put(key,true);
            }else if (property instanceof ArrayProperty) {
                Property items = ((ArrayProperty) property).getItems();
                if (items instanceof RefProperty) {
                    String simpleRef = ((RefProperty) items).getSimpleRef();
                    Model model1 = definitions.get(simpleRef);
                    ArrayList<Object> objectArrayList = new ArrayList<>();
                    objectArrayList.add(getExampleHashMap(model1,definitions));
                    resultExample.put(key,objectArrayList);
                }else {
                    ArrayList<Object> objectArrayList = new ArrayList<>();
                    objectArrayList.add(items.getType());
                    resultExample.put(key,objectArrayList);
                }
            }else if (property instanceof MapProperty) {
                Map map = new HashMap(2);
                map.put("key1","value1");
                map.put("key2","value2");
                resultExample.put(key,map);
            }else if (property instanceof RefProperty) {
                String simpleRef = ((RefProperty) property).getSimpleRef();
                Model model1 = definitions.get(simpleRef);
                resultExample.put(key,getExampleHashMap(model1,definitions));
            }else if (property instanceof BaseIntegerProperty) {
                resultExample.put(key,0);
            }else if (property instanceof DecimalProperty) {
                resultExample.put(key,0.00);
            }else  {
                resultExample.put(key, type);
            }



        }
        return resultExample;
    }

    public String overviewDocumenttoString() {
        StringBuilder sb = new StringBuilder();
        sb.append(applyOverviewDocument().toString());
        return sb.toString();
    }

    public String definitionsDocumenttoString() {
        StringBuilder sb = new StringBuilder();
        sb.append(applyDefinitionsDocument().toString());
        return sb.toString();
    }

    public String securityDocumenttoString() {
        StringBuilder sb = new StringBuilder();
        sb.append(applySecurityDocument().toString());
        return sb.toString();
    }


    private MarkupDocBuilder applyOverviewDocument() {
        return overviewDocument.apply(
                context.createMarkupDocBuilder(),
                OverviewDocument.parameters(context.getSwagger()));
    }

    private MarkupDocBuilder applyPathsDocument() {
        return pathsDocument.apply(
                context.createMarkupDocBuilder(),
                PathsDocument.parameters(context.getSwagger().getPaths()));
    }

    private MarkupDocBuilder applyDefinitionsDocument() {
        return definitionsDocument.apply(
                context.createMarkupDocBuilder(),
                DefinitionsDocument.parameters(context.getSwagger().getDefinitions()));
    }

    private MarkupDocBuilder applySecurityDocument() {
        return securityDocument.apply(
                context.createMarkupDocBuilder(),
                SecurityDocument.parameters(context.getSwagger().getSecurityDefinitions()));
    }

}
