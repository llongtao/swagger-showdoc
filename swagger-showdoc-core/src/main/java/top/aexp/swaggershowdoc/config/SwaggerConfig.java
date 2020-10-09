package top.aexp.swaggershowdoc.config;

import io.swagger.models.Swagger;
import lombok.Data;

/**
 * @author LILONGTAO
 * @date 2020-09-29
 */
@Data
public class SwaggerConfig {

    private String module;

    private Swagger swagger;

    public SwaggerConfig(String module, Swagger swagger) {
        this.module = module;
        this.swagger = swagger;
    }
}
