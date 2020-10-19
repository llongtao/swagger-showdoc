package top.aexp.swaggershowdoc.springboot.plug;

import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import top.aexp.swaggershowdoc.springboot.annotations.DisplayEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import springfox.documentation.builders.PropertySpecificationBuilder;
import springfox.documentation.schema.Annotations;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;
import springfox.documentation.swagger.schema.ApiModelProperties;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author LILONGTAO
 * @date 2020-03-12
 */
@SuppressWarnings("rawtypes")
@Slf4j
@Primary
@Component
public class SwaggerCodedEnumPropertyPlugin implements ModelPropertyBuilderPlugin {


    @Override
    public void apply(ModelPropertyContext context) {
        //如果不支持swagger的话，直接返回
        Optional<BeanPropertyDefinition> beanPropertyDefinition = context.getBeanPropertyDefinition();
        if (!beanPropertyDefinition.isPresent()) {
            return;
        }
        AnnotatedField field = beanPropertyDefinition.get().getField();
        if (field == null) {
            return;
        }
        descForEnumFields(context, field.getRawType());
    }

    /**
     * 为枚举字段设置注释
     */
    private void descForEnumFields(ModelPropertyContext context, Class fieldType) {

        Optional<ApiModelProperty> annotation =Optional.empty();

        if (context.getAnnotatedElement().isPresent()) {
            annotation = ApiModelProperties.findApiModePropertyAnnotation(context.getAnnotatedElement().get());

        }
        if (!annotation.isPresent()&& context.getBeanPropertyDefinition().isPresent()) {
            annotation = Annotations.findPropertyAnnotation(context.getBeanPropertyDefinition().get(), ApiModelProperty.class);
        }


        //没有@ApiModelProperty 或者 notes 属性没有值，直接返回
        if (!fieldType.isEnum()) {
            if (!annotation.isPresent()) {
                return;
            }
            if (StringUtils.isEmpty(annotation.get().notes())) {
                return;
            }
        }

        //StringUtil.isBlank((annotation.get()).notes())

        Class rawPrimaryType = null;
        if (!annotation.isPresent()) {
            rawPrimaryType = fieldType;
        } else {
            //@ApiModelProperties中的notes指定的class类型
            try {
                rawPrimaryType = Class.forName((annotation.get()).notes());
            } catch (ClassNotFoundException ignore) {
                //如果指定的类型无法转化，直接忽略

            }
        }
        if (rawPrimaryType == null) {
            rawPrimaryType = fieldType;
        }


        //如果对应的class是一个@SwaggerDisplayEnum修饰的枚举类，获取其中的枚举值

        DisplayEnum swaggerDisplayEnum = AnnotationUtils
                .findAnnotation(rawPrimaryType, DisplayEnum.class);
        if (!Enum.class.isAssignableFrom(rawPrimaryType)) {
            return;
        }


        Object[] subItemRecords = rawPrimaryType.getEnumConstants();

        String valueName = null;
        String descName = null;
        if (swaggerDisplayEnum != null) {
            //有注解以注解为准
            valueName = swaggerDisplayEnum.value();
            descName = swaggerDisplayEnum.name();
        } else {
            //没有注解
            valueName = "code";
            descName = "desc";
        }

        //从annotation中获取对应的值和描述的变量名
        if (StringUtils.isEmpty(valueName) || StringUtils.isEmpty(descName)) {
            return;
        }
        final String valueNamef = valueName;
        final String descNamef = descName;

        AtomicReference<String> exampleValue = new AtomicReference<>();
        final List<String> displayValues = Arrays.stream(subItemRecords).filter(Objects::nonNull).map(item -> {
            Class currentClass = item.getClass();

            String value;
            String desc;
            if ("string".equals(valueNamef)) {
                value = item.toString();
            } else {
                try{
                    Field valueField = currentClass.getDeclaredField(valueNamef);
                    valueField.setAccessible(true);
                    value = valueField.get(item).toString();
                }catch (NoSuchFieldException | IllegalAccessException e){
                    log.warn("获取{}枚举的属性失败, {}",currentClass, e.getMessage());
                    value = item.toString();
                }

            }

            try{
                Field descField = currentClass.getDeclaredField(descNamef);
                descField.setAccessible(true);
                desc = descField.get(item).toString();
            }catch (NoSuchFieldException | IllegalAccessException e){
                log.warn("获取{}枚举的值失败, {}",currentClass, e.getMessage());
                desc = "-";
            }
            exampleValue.set(value);
            return value + ":" + desc;
        }).collect(Collectors.toList());


        String joinText = " (" + String.join("; ", displayValues) + ")";
        PropertySpecificationBuilder specificationBuilder = context.getSpecificationBuilder();
        try {
            Field mField = PropertySpecificationBuilder.class.getDeclaredField("description");
            mField.setAccessible(true);
            Object o = mField.get(specificationBuilder);
            if (o == null) {
                o = "";
            }
            joinText = o + joinText;
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        //final ResolvedType resolvedType = context.getResolver().resolve(fieldType);
        specificationBuilder.description(joinText);
        if (exampleValue.get() != null) {
            specificationBuilder.example(exampleValue.get());
        }

    }


    @Override
    public boolean supports(DocumentationType documentationType) {
        return true;
    }

}
