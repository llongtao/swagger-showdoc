package top.aexp.swaggershowdoc.demo;

import top.aexp.swaggershowdoc.springboot.annotations.EnableSwaggerShowDoc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author LILONGTAO
 * Date: 2018-07-14
 */
@EnableSwaggerShowDoc
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
