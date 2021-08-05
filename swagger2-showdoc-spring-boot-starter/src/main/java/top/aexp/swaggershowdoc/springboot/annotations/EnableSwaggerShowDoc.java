package top.aexp.swaggershowdoc.springboot.annotations;

import top.aexp.swaggershowdoc.springboot.SwaggerShowDocAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author LILONGTAO
 * @date 2020-09-29
 */
@Target({ TYPE})
@Retention(RUNTIME)
@Import(SwaggerShowDocAutoConfiguration.class)
public @interface EnableSwaggerShowDoc {
}
