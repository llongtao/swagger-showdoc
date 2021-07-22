package top.aexp.swaggershowdoc;

import top.aexp.swaggershowdoc.config.Config;
import top.aexp.swaggershowdoc.markdownbulider.MarkdownBuilder;
import top.aexp.swaggershowdoc.markdownbulider.constants.FontStyle;
import top.aexp.swaggershowdoc.markdownbulider.constants.MdLevel;
import top.aexp.swaggershowdoc.models.OperationDTO;
import top.aexp.swaggershowdoc.models.PropertyModel;
import top.aexp.swaggershowdoc.models.ShowDocResponse;
import top.aexp.swaggershowdoc.models.Swagger2;
import top.aexp.swaggershowdoc.util.HttpUtil;
import top.aexp.swaggershowdoc.util.Jackson;
import io.swagger.models.*;
import io.swagger.models.parameters.AbstractSerializableParameter;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;


@SuppressWarnings("rawtypes")
@Slf4j
public class Swagger2ShowDocBuilder {


    private static final LinkedHashMap<String, String> TITLE_MAP;

    static {
        TITLE_MAP = new LinkedHashMap<>();
        TITLE_MAP.put("name", "参数名");
        TITLE_MAP.put("required", "必选");
        TITLE_MAP.put("type", "类型");
        TITLE_MAP.put("description", "说明");
        TITLE_MAP.put("example", "示例");
    }


    public static void start(Config config) {
        config.getSwaggerConfigList().forEach(swaggerConfig -> updateToShowDoc(config, new Swagger2(swaggerConfig.getSwagger()), swaggerConfig.getModule()));
    }

    /**
     * 更新文档到ShowDoc
     */
    public static void updateToShowDoc(Config config, Swagger swagger, String moduleName) {

        List<Tag> tags = swagger.getTags();
        Map<String, Path> paths = swagger.getPaths();

        tags.forEach(tag -> {
            List<Path> showDocPath = new ArrayList<>();
            paths.forEach((k, v) -> {
                findApiByTag(v, tag, showDocPath);
                doShowDoc(config, moduleName, v, tag, swagger, k);
            });
        });

    }

    private static void findApiByTag(Path path, Tag tag, List<Path> showDocPath) {
        addShowDocByTag(path.getPost(), path, tag, showDocPath);
        addShowDocByTag(path.getGet(), path, tag, showDocPath);
        addShowDocByTag(path.getDelete(), path, tag, showDocPath);
        addShowDocByTag(path.getPut(), path, tag, showDocPath);
        addShowDocByTag(path.getOptions(), path, tag, showDocPath);
        addShowDocByTag(path.getHead(), path, tag, showDocPath);
        addShowDocByTag(path.getPatch(), path, tag, showDocPath);
    }

    private static void addShowDocByTag(Operation operation, Path path, Tag tag, List<Path> showDocPath) {
        if (null != operation && null != operation.getTags()) {
            List<String> tagList = operation.getTags();
            tagList.forEach(tags -> {
                if (Objects.equals(tag.getName(), tags)) {
                    showDocPath.add(path);
                }
            });
        }
    }

    private static void doShowDoc(Config config, String moduleName, Path path, Tag tag, Swagger swagger, String apiPath) {
        boolean contains = null != path.getPost() && path.getPost().getTags().contains(tag.getName())
                || null != path.getGet() && path.getGet().getTags().contains(tag.getName())
                || null != path.getPut() && path.getPut().getTags().contains(tag.getName())
                || null != path.getOptions() && path.getOptions().getTags().contains(tag.getName())
                || null != path.getDelete() && path.getDelete().getTags().contains(tag.getName())
                || null != path.getPatch() && path.getPatch().getTags().contains(tag.getName())
                || null != path.getHead() && path.getHead().getTags().contains(tag.getName());
        if (contains) {
            getOperationMap(path).forEach((summary, operationList) -> {
                try{
                    sendToShowDoc(config, moduleName, tag.getName(), summary, generateMd(swagger, apiPath, path, summary, operationList));
                }catch (Exception e){
                    log.error("文档上传到showdoc失败",e);
                }
            });
        }
    }

    /**
     * 将数据发送到ShowDoc
     * url:https://www.showdoc.cc/web/#/page/102098
     *
     * @param config      showDoc文档信息(必填)
     * @param catName     当页面文档处于目录下时，请传递目录名（可空）
     * @param catNameSub  当页面文档处于更细分的子目录下时，请传递子目录名。（可空）
     * @param pageTitle   页面标题(必填)page_title存在则用内容更新，不存在则创建
     * @param pageContent 页面内容，可传递markdown格式的文本或者html源码(必填)
     */
    private static void sendToShowDoc(Config config, String catName, String catNameSub, String pageTitle, String pageContent) {
        Map<String, String> parMap = new HashMap<>();
        parMap.put("api_key", config.getShowDocApiKey());
        parMap.put("api_token", config.getShowDocApiToken());
        parMap.put("cat_name", catName);
        parMap.put("cat_name_sub", catNameSub);
        parMap.put("page_title", pageTitle);
        parMap.put("page_content", pageContent);
        parMap.put("s_number", "");


        String response = HttpUtil.doPost(config.getShowDocUrl(), parMap);
        ShowDocResponse showDocResponse = Jackson.parseObject(response, ShowDocResponse.class);
        if (showDocResponse == null) {
            throw new IllegalArgumentException("showdoc 接口请求失败");
        }
        if (!"0".equals(showDocResponse.getError_code())) {
            throw new IllegalArgumentException(showDocResponse.getError_message());
        }
    }

    private static String generateMd(Swagger swagger, String url, Path swaggerPath, String summary, List<OperationDTO> operationList) {
        Map<String, Model> definitions = swagger.getDefinitions();
        Map<String, Path> paths = new HashMap<>();
        paths.put(url, swaggerPath);

        swagger.setPaths(paths);

        List<String> methodList = operationList.stream().map(OperationDTO::getMethod).collect(Collectors.toList());
        Operation operation = operationList.get(0).getOperation();

        assert operation != null;
        List<Parameter> parameters = operation.getParameters();
        Response response = operation.getResponses().get("200");


        List<AbstractSerializableParameter> tableParameterList = new ArrayList<>();
        BodyParameter bodyParameter = null;
        for (Parameter parameter : parameters) {
            if (parameter instanceof AbstractSerializableParameter) {
                tableParameterList.add((AbstractSerializableParameter) parameter);
            } else if (parameter instanceof BodyParameter) {
                bodyParameter = (BodyParameter) parameter;
            }
        }

        MarkdownBuilder markdownBuilder = MarkdownBuilder.start();
        markdownBuilder.writeln("简要描述：", FontStyle.BOLD)
                .writeUnorderedListItem(summary).newLine()
                .writeln("请求URL：", FontStyle.BOLD)
                .writeUnorderedListItem("http://" + swagger.getHost() + url, FontStyle.HIGHLIGHT).newLine()
                .writeln("请求方式：", FontStyle.BOLD)
                .writeUnorderedList(methodList).newLine()
        ;

        if (tableParameterList.size() != 0) {
            markdownBuilder.writeln("请求参数：", FontStyle.BOLD)
                    .writeTable(tableParameterList, TITLE_MAP).newLine();
        }

        List<RefModel> refModelList = new ArrayList<>();
        if (bodyParameter != null) {
            Map<String, String> examples = bodyParameter.getExamples();
            Model schema = bodyParameter.getSchema();
            if (schema instanceof RefModel) {
                String simpleRef = ((RefModel) schema).getSimpleRef();
                markdownBuilder.write("请求实体：", FontStyle.BOLD).crossReferenceRaw(null, simpleRef, simpleRef).newLine();
                refModelList.add((RefModel) schema);
            } else if (schema instanceof ArrayModel) {
                Property items = ((ArrayModel) schema).getItems();
                if (items instanceof ArrayProperty) {
                    items = getArrayRefProperty((ArrayProperty) items);
                }
                if (items instanceof RefProperty) {
                    String simpleRef = ((RefProperty) items).getSimpleRef();
                    Model model = definitions.get(simpleRef);
                    if (model instanceof RefModel) {
                        markdownBuilder.write("请求实体：", FontStyle.BOLD).crossReferenceRaw(null, simpleRef, simpleRef).newLine();
                        refModelList.add((RefModel) model);
                    }
                }
            }

            if (examples != null) {
                markdownBuilder.writeln("请求body：", FontStyle.BOLD)
                        .writeHighlight(Jackson.toJSONString(examples), "json").newLine();
            }
        }

        if (response != null) {
            Map<String, Object> examples = response.getExamples();
            Model responseSchema1 = response.getResponseSchema();
            if (responseSchema1 instanceof RefModel) {
                String simpleRef = ((RefModel) responseSchema1).getSimpleRef();
                markdownBuilder.write("响应实体：", FontStyle.BOLD).crossReferenceRaw(null, simpleRef, simpleRef).newLine();
                refModelList.add((RefModel) responseSchema1);
            } else if (responseSchema1 instanceof ArrayModel) {
                Property items = ((ArrayModel) responseSchema1).getItems();
                if (items instanceof ArrayProperty) {
                    items = getArrayRefProperty((ArrayProperty) items);
                }
                if (items instanceof RefProperty) {
                    String simpleRef = ((RefProperty) items).getSimpleRef();
                    Model model = definitions.get(simpleRef);
                    if (model instanceof RefModel) {
                        markdownBuilder.write("响应实体：", FontStyle.BOLD).crossReferenceRaw(null, simpleRef, simpleRef).newLine();
                        refModelList.add((RefModel) model);
                    }
                }
            }
            if (examples != null) {
                markdownBuilder.writeln("响应示例", FontStyle.BOLD)
                        .writeHighlight(Jackson.toJSONString(examples), "json").newLine();

            }
        }




        markdownBuilder.writeTitle("数据模型", MdLevel.THREE);
        markdownBuilder.newLine();


        refModelList.forEach(refModel -> {
            List<PropertyModel> propertyList = new ArrayList<>();
            String simpleRef = refModel.getSimpleRef();
            Model model = definitions.get(simpleRef);
            if(model!=null){
                Map<String, Property> properties = model.getProperties();
                List<RefProperty> refPropertyList = new ArrayList<>();
                buildPropertyList(propertyList, properties, refPropertyList);
                Set<String> set = new HashSet<>();
                set.add(simpleRef);
                buildRefModelTable(refPropertyList, definitions, markdownBuilder, set);
            }

            markdownBuilder.writeAnchor(simpleRef);
            markdownBuilder.writeln(simpleRef, FontStyle.BOLD);
            markdownBuilder.writeTable(propertyList, TITLE_MAP);

        });

        return markdownBuilder.toString();
    }

    private static Map<String, List<OperationDTO>> getOperationMap(Path swaggerPath) {
        Map<String, List<OperationDTO>> operationMap = new HashMap<>();
        putOperation(swaggerPath.getPost(), "POST", operationMap);
        putOperation(swaggerPath.getGet(), "GET", operationMap);
        putOperation(swaggerPath.getOptions(), "OPTIONS", operationMap);
        putOperation(swaggerPath.getPatch(), "PATCH", operationMap);
        putOperation(swaggerPath.getHead(), "HEAD", operationMap);
        putOperation(swaggerPath.getDelete(), "DELETE", operationMap);
        putOperation(swaggerPath.getPut(), "PUT", operationMap);
        return operationMap;
    }

    private static void putOperation(Operation operation, String method, Map<String, List<OperationDTO>> operationMap) {
        if (operation == null) {
            return;
        }
        operationMap.computeIfAbsent(operation.getSummary(),k-> new ArrayList<>()).add(new OperationDTO(operation, method));
    }


    private static void buildPropertyList(List<PropertyModel> propertyList, Map<String, Property> properties, List<RefProperty> refPropertyList) {
        if (properties == null) {
            return;
        }
        properties.forEach((name, property) -> {
            if (property instanceof RefProperty) {
                refPropertyList.add((RefProperty) property);
                propertyList.add(new PropertyModel(name, property.getRequired(), MarkdownBuilder.getReference(null, ((RefProperty) property).getSimpleRef(), null), property.getDescription(), property.getExample()));
            } else if (property instanceof ArrayProperty) {
                Property arrayRefProperty = getArrayRefProperty((ArrayProperty) property);
                if (arrayRefProperty instanceof RefProperty) {
                    refPropertyList.add((RefProperty) arrayRefProperty);
                }
                propertyList.add(new PropertyModel(name, property.getRequired(), getArrayPropertyType((ArrayProperty) property), property.getDescription(), property.getExample()));
            } else {
                propertyList.add(new PropertyModel(name, property.getRequired(), property.getType(), property.getDescription(), property.getExample()));
            }

        });
    }


    private static String getArrayPropertyType(ArrayProperty arrayProperty) {
        String name;

        Property items = arrayProperty.getItems();
        if (items instanceof RefProperty) {
            String reference = MarkdownBuilder.getReference(null, ((RefProperty) items).getSimpleRef(), null);
            name = "List<" + reference + ">";
        } else if (items instanceof ArrayProperty) {
            name = "List<" + getArrayPropertyType((ArrayProperty) items) + ">";
        } else {
            name = "List<" + items.getType() + ">";
        }

        return name;
    }


    /**
     * 构建引用model
     *
     * @param refList         引用列表
     * @param definitions     definitions
     * @param markdownBuilder markdownBuilder
     * @param modelNameSet    已构建模型set,避免循环引用
     */
    private static void buildRefModelTable(List<RefProperty> refList, Map<String, Model> definitions, MarkdownBuilder markdownBuilder, Set<String> modelNameSet) {
        List<RefProperty> propertyRefList = new ArrayList<>();

        for (RefProperty refProperty : refList) {
            String simpleRef = refProperty.getSimpleRef();
            Model model = definitions.get(simpleRef);
            if (modelNameSet.contains(simpleRef) || model == null) {
                continue;
            }
            modelNameSet.add(simpleRef);
            Map<String, Property> properties = model.getProperties();
            List<PropertyModel> propertyList = new ArrayList<>();
            buildPropertyList(propertyList, properties, propertyRefList);

            markdownBuilder.writeAnchor(simpleRef);
            markdownBuilder.writeln(simpleRef, FontStyle.BOLD);
            markdownBuilder.writeTable(propertyList, TITLE_MAP);
        }

        if (propertyRefList.size() > 0) {
            buildRefModelTable(propertyRefList, definitions, markdownBuilder, modelNameSet);
        }
    }

    private static Property getArrayRefProperty(ArrayProperty arrayProperty) {
        Property items = arrayProperty.getItems();
        if (items instanceof ArrayProperty) {
            items = getArrayRefProperty((ArrayProperty) items);
        }
        return items;
    }


}
