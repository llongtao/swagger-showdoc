package top.aexp.swaggershowdoc.demo.enums;

import top.aexp.swaggershowdoc.springboot.annotations.DisplayEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author LILONGTAO
 * @date 2020-09-30
 */
@Getter
@DisplayEnum
@AllArgsConstructor
public enum Type {
    A("枚举A"),
    B("枚举B")
    ;
    private String desc;

}
