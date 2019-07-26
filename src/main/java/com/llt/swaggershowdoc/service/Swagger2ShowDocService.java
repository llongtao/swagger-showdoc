package com.llt.swaggershowdoc.service;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.node.TextNode;
import com.llt.swaggershowdoc.markdownbulider.MarkdownBuilder;
import com.llt.swaggershowdoc.markdownbulider.constants.MdLevel;
import com.llt.swaggershowdoc.models.*;
import io.github.swagger2markup.Language;
import io.github.swagger2markup.Swagger2MarkupConfig;
import io.github.swagger2markup.builder.Swagger2MarkupConfigBuilder;
import io.github.swagger2markup.markup.builder.MarkupLanguage;
import io.swagger.models.*;
import io.swagger.models.parameters.AbstractSerializableParameter;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import io.swagger.parser.Swagger20Parser;
import io.swagger.util.Json;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URL;
import java.sql.Ref;
import java.util.*;

import static com.llt.swaggershowdoc.markdownbulider.constants.FontStyle.BOLD;
import static com.llt.swaggershowdoc.markdownbulider.constants.FontStyle.HIGHLIGHT;

@Slf4j
@Service
public class Swagger2ShowDocService {

    private static LinkedHashMap<String, String> TITLE_MAP;

    static {
        TITLE_MAP = new LinkedHashMap<>();
        TITLE_MAP.put("name", "参数名");
        TITLE_MAP.put("required", "必选");
        TITLE_MAP.put("type", "类型");
        TITLE_MAP.put("description", "说明");
        TITLE_MAP.put("example", "示例");
    }


    @Resource
    private RestTemplate restTemplate;


    public void start(ConfigInfo configInfo) throws IOException {
        ShowDocConfig showDocConfig = configInfo.getShowDocConfig();
        String showDocUrl = showDocConfig.getUrl();

        //组装showDoc 请求地址
        if (!showDocUrl.contains("www.showdoc.cc")) {
            showDocUrl = "http://" + showDocUrl + "/server/index.php?s=/api/item/updateByApi";
        } else {
            showDocUrl = "https://www.showdoc.cc/server/api/item/updateByApi";
        }
        showDocConfig.setUrl(showDocUrl);

        List<SwaggerConfig> swaggerConfigList = configInfo.getSwaggerConfigList();
        for (SwaggerConfig swaggerConfig : swaggerConfigList) {
            Integer port = swaggerConfig.getPort();
            if (port == null) {
                port = 80;
            }
            String path = swaggerConfig.getPath();
            if (StringUtils.isEmpty(path)) {
                path = Strings.EMPTY;
            } else {
                path = "/" + path;
            }
            URL url = new URL("http", swaggerConfig.getIp(), port, path + "/v2/api-docs");
            String swaggerStr = restTemplate.getForObject(url.toString(), String.class);
            assert swaggerStr != null;
            Swagger swagger = new Swagger20Parser().parse(swaggerStr);
            updateToShowDoc(showDocConfig, new Swagger2(swagger), swaggerConfig.getModule());
        }


    }

    /**
     * 更新文档到ShowDoc
     *
     * @param swagger
     */
    public void updateToShowDoc(ShowDocConfig showDocConfig, Swagger swagger, String moduleName) {

        List<Tag> tags = swagger.getTags();
        Map<String, Path> paths = swagger.getPaths();

        tags.forEach(tag -> {
            List<Path> showDocPath = new ArrayList<>();
            paths.forEach((k, v) -> {
                findApiByTag(v, tag, showDocPath);
                doShowDoc(showDocConfig, moduleName, v, tag, swagger, k);
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

    private void doShowDoc(ShowDocConfig showDocConfig, String moduleName, Path path, Tag tag, Swagger swagger, String apiPath) {
       boolean contains = null != path.getPost() && path.getPost().getTags().contains(tag.getName())
                || null != path.getGet() && path.getGet().getTags().contains(tag.getName())
                || null != path.getPut() && path.getPut().getTags().contains(tag.getName())
                || null != path.getOptions() && path.getOptions().getTags().contains(tag.getName())
                || null != path.getDelete() && path.getDelete().getTags().contains(tag.getName())
                || null != path.getPatch() && path.getPatch().getTags().contains(tag.getName())
                || null != path.getHead() && path.getHead().getTags().contains(tag.getName());
        if (contains) {
            sendToShowDoc(showDocConfig, moduleName, tag.getName(), getApiDescription(path), generateMd(swagger, apiPath, path), "");
        }
    }

    /**
     * 将数据发送到ShowDoc
     * url:https://www.showdoc.cc/web/#/page/102098
     *
     * @param showDocConfig showDoc文档信息(必填)
     * @param catName       当页面文档处于目录下时，请传递目录名（可空）
     * @param catNameSub    当页面文档处于更细分的子目录下时，请传递子目录名。（可空）
     * @param pageTitle     页面标题(必填)page_title存在则用内容更新，不存在则创建
     * @param pageContent   页面内容，可传递markdown格式的文本或者html源码(必填)
     * @param sNumber       页面序号。数字越小，该页面越靠前（可空）
     */
    private void sendToShowDoc(ShowDocConfig showDocConfig, String catName, String catNameSub, String pageTitle, String pageContent, String sNumber) {
        MultiValueMap<String, String> parMap = new LinkedMultiValueMap<String, String>();
        parMap.add("api_key", showDocConfig.getApiKey());
        parMap.add("api_token", showDocConfig.getApiToken());
        parMap.add("cat_name", catName);
        parMap.add("cat_name_sub", catNameSub);
        parMap.add("page_title", pageTitle);
        parMap.add("page_content", pageContent);
        parMap.add("s_number", sNumber);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String response = restTemplate.postForObject(showDocConfig.getUrl(), new HttpEntity<>(parMap, headers), String.class);
        ShowDocResponse showDocResponse = JSON.parseObject(response, ShowDocResponse.class);
        if (showDocResponse == null) {
            throw new IllegalArgumentException("showdoc 接口请求失败");
        }
        if (!"0".equals(showDocResponse.getError_code())) {
            throw new IllegalArgumentException(showDocResponse.getError_message());
        }
    }

    private String getApiDescription(Path path) {
        if (null != path.getPost()) {
            return path.getPost().getSummary();
        }
        if (null != path.getGet()) {
            return path.getGet().getSummary();
        }
        if (null != path.getHead()) {
            return path.getHead().getSummary();
        }
        if (null != path.getDelete()) {
            return path.getDelete().getSummary();
        }
        if (null != path.getOptions()) {
            return path.getOptions().getSummary();
        }
        if (null != path.getPut()) {
            return path.getPut().getSummary();
        }
        if (null != path.getPatch()) {
            return path.getPatch().getSummary();
        }
        return "";
    }


    private String generateMd(Swagger swagger, String url, Path swaggerPath) {
        Map<String, Model> definitions = swagger.getDefinitions();
        Map<String, Path> paths = new HashMap<>();
        paths.put(url, swaggerPath);

        swagger.setPaths(paths);

        List<String> methodList = new ArrayList<>();
        Operation operation = null;

        Operation post = swaggerPath.getPost();
        Operation get = swaggerPath.getGet();
        Operation put = swaggerPath.getPut();
        Operation delete = swaggerPath.getDelete();
        Operation head = swaggerPath.getHead();
        Operation patch = swaggerPath.getPatch();
        Operation options = swaggerPath.getOptions();
        if (options != null) {
            methodList.add("OPTIONS");
            operation = options;
        }
        if (head != null) {
            methodList.add("HEAD");
            operation = head;
        }
        if (patch != null) {
            methodList.add("PATCH");
            operation = patch;
        }
        if (delete != null) {
            methodList.add("DELETE");
            operation = delete;
        }
        if (put != null) {
            methodList.add("PUT");
            operation = put;
        }
        if (get != null) {
            methodList.add("GET");
            operation = get;
        }
        if (post != null) {
            methodList.add("POST");
            operation = post;
        }


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
        markdownBuilder.writeln("简要描述：", BOLD)
                .writeUnorderedListItem(operation.getSummary()).newLine()
                .writeln("请求URL：", BOLD)
                .writeUnorderedListItem(url, HIGHLIGHT).newLine()
                .writeln("请求方式：", BOLD)
                .writeUnorderedList(methodList).newLine()
        ;

        if (tableParameterList.size() != 0) {
            markdownBuilder.writeln("请求参数：", BOLD)
                    .writeTable(tableParameterList, TITLE_MAP).newLine();
        }

        List<RefModel> refModelList = new ArrayList<>();
        if (bodyParameter != null) {
            Map<String, String> examples = bodyParameter.getExamples();
            Model schema = bodyParameter.getSchema();
            if (schema instanceof RefModel) {
                String simpleRef = ((RefModel) schema).getSimpleRef();
                markdownBuilder.write("请求实体：", BOLD).crossReferenceRaw(null, simpleRef, simpleRef).newLine();
                refModelList.add((RefModel) schema);
            }else if (schema instanceof ArrayModel) {
                Property items = ((ArrayModel) schema).getItems();
                if (items instanceof ArrayProperty) {
                    items = getArrayRefProperty((ArrayProperty) items);
                }
                if (items instanceof RefProperty) {
                    String simpleRef = ((RefProperty) items).getSimpleRef();
                    Model model = definitions.get(simpleRef);
                    if (model instanceof RefModel) {
                        markdownBuilder.write("请求实体：", BOLD).crossReferenceRaw(null, simpleRef, simpleRef).newLine();
                        refModelList.add((RefModel) model);
                    }
                }
            }
            if (examples != null) {
                markdownBuilder.writeln("请求body：", BOLD)
                        .writeHighlight(Json.pretty(examples), "json").newLine();
            }
        }


        Map<String, Object> examples = response.getExamples();
        Model responseSchema1 = response.getResponseSchema();
        if (responseSchema1 instanceof RefModel) {
            String simpleRef = ((RefModel) responseSchema1).getSimpleRef();
            markdownBuilder.write("响应实体：", BOLD).crossReferenceRaw(null, simpleRef, simpleRef).newLine();
            refModelList.add((RefModel) responseSchema1);
        }else if (responseSchema1 instanceof ArrayModel) {
            Property items = ((ArrayModel) responseSchema1).getItems();
            if (items instanceof ArrayProperty) {
                items = getArrayRefProperty((ArrayProperty) items);
            }
            if (items instanceof RefProperty) {
                String simpleRef = ((RefProperty) items).getSimpleRef();
                Model model = definitions.get(simpleRef);
                if (model instanceof RefModel) {
                    markdownBuilder.write("响应实体：", BOLD).crossReferenceRaw(null, simpleRef, simpleRef).newLine();
                    refModelList.add((RefModel) model);
                }
            }
        }



        if (examples != null) {
            markdownBuilder.writeln("响应示例", BOLD)
                    .writeHighlight(Json.pretty(examples), "json").newLine();

        }
        markdownBuilder.writeTitle("数据模型", MdLevel.THREE);
        markdownBuilder.newLine();


        refModelList.forEach(refModel -> {
            List<PropertyModel> propertyList = new ArrayList<>();
            String simpleRef = refModel.getSimpleRef();
            Model model = definitions.get(simpleRef);
            Map<String, Property> properties = model.getProperties();
            List<RefProperty> refPropertyList = new ArrayList<>();
            buildPropertyList(propertyList, properties, refPropertyList);

            markdownBuilder.writeAnchor(simpleRef);
            markdownBuilder.writeln(simpleRef, BOLD);
            markdownBuilder.writeTable(propertyList, TITLE_MAP);
            Set<String> set = new HashSet<>();
            set.add(simpleRef);
            buildRefModelTable(refPropertyList, definitions, markdownBuilder, set);
        });

        return markdownBuilder.toString();
    }

    private void buildPropertyList(List<PropertyModel> propertyList, Map<String, Property> properties, List<RefProperty> refPropertyList) {
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



    private String getArrayPropertyType(ArrayProperty arrayProperty) {
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
    private void buildRefModelTable(List<RefProperty> refList, Map<String, Model> definitions, MarkdownBuilder markdownBuilder, Set<String> modelNameSet) {
        List<RefProperty> propertyRefList = new ArrayList<>();

        for (RefProperty refProperty : refList) {
            String simpleRef = refProperty.getSimpleRef();
            Model model = definitions.get(simpleRef);
            if (modelNameSet.contains(simpleRef)) {
                continue;
            }
            modelNameSet.add(simpleRef);
            Map<String, Property> properties = model.getProperties();
            List<PropertyModel> propertyList = new ArrayList<>();
            buildPropertyList(propertyList, properties, propertyRefList);

            markdownBuilder.writeAnchor(simpleRef);
            markdownBuilder.writeln(simpleRef, BOLD);
            markdownBuilder.writeTable(propertyList, TITLE_MAP);
        }

        if (propertyRefList.size() > 0) {
            buildRefModelTable(propertyRefList, definitions, markdownBuilder, modelNameSet);
        }
    }

    private Property getArrayRefProperty(ArrayProperty arrayProperty) {
        Property items = arrayProperty.getItems();
        if (items instanceof ArrayProperty) {
            items = getArrayRefProperty((ArrayProperty) items);
        }
        return items;
    }




}
