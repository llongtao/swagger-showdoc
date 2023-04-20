package top.aexp.swaggershowdoc.springboot.plug;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import java.lang.reflect.AnnotatedElement;
import java.util.Optional;

import static springfox.bean.validators.plugins.Validators.annotationFromBean;
import static springfox.bean.validators.plugins.Validators.annotationFromField;

/**
 * @author lilongtao 2021/8/5
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@Slf4j
@Order
@Component
public class RequiredPlugin implements ModelPropertyBuilderPlugin {

    Class<?>[] aClass = {NotEmpty.class,NotNull.class, NotBlank.class};

    /**
     * support all documentationTypes
     */
    @Override
    public boolean supports(DocumentationType delimiter) {
        // we simply support all documentationTypes!
        return true;
    }

    /**
     * read NotNull annotation
     */
    @Override
    public void apply(ModelPropertyContext context) {
        for (Class<?> c : aClass) {
            if (extractAnnotation(context,c)) {
                context.getSpecificationBuilder().required(true);
                return;
            }
        }
    }


    boolean extractAnnotation(ModelPropertyContext context,Class aClass) {
        Optional<Boolean> aBoolean = context.getAnnotatedElement().map(element -> element.isAnnotationPresent(aClass));
        return aBoolean.orElse(false);
    }
}
