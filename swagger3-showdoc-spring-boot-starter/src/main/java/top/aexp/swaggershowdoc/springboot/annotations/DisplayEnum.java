package top.aexp.swaggershowdoc.springboot.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author LILONGTAO
 * @date 2020-03-12
 */
@Target({ TYPE})
@Retention(RUNTIME)
public @interface DisplayEnum {
    /**
     * 默认取 desc 字段值
     */
    String name() default "desc";

    /**
     * 默认取enum 的name
     */
    String value() default "string";
}
