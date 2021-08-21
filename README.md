# swagger-showdoc

[![License](https://img.shields.io/badge/license-GPL-blue)](https://github.com/llongtao/swagger-showdoc/blob/master/LICENSE)
[![SpringBoot](https://img.shields.io/badge/SpringBoot-2.2.0.RELEASE-brightgreen.svg)](https://docs.spring.io/spring-boot/docs/2.0.3.RELEASE/reference/htmlsingle/)

#### 项目介绍
公司统一使用ShowDoc记录接口文档,为了避免重复劳动打算寻找一款swagger转化为ShowDoc的工具,可是市面上并没有找到合适的,新版ShowDoc自带的导入非常难用,于是打算自己开发一个,目前这套已满足日常使用需求,解放大家双手深受欢迎

提供两种方式生成文档
1. 启动独立应用页面填写生成,无入侵项目
2. 应用内嵌,启动项目时生成(可选择关闭)
#### 软件架构

Spring Boot 2.2.0

JDK 1.8

#### 模块介绍
##### swagger-showdoc-core
文档生成器核心模块
##### swagger-showdoc-server
独立部署模块,如果选择使用部署该应用可无需引入应用内嵌模块

使用说明:
1. 运行SwaggerShowDocApplication
2. 访问http://localhost:12345
3. 输入参数，点击同步
4. 你输入的配置会被保存在前端 

注:部署机器必须可以访问业务服务和showdoc服务
##### swagger2-showdoc-spring-boot-starter
swagger2版本的spring-boot内嵌应用模块,适用于spring-boot2.2以下版本,可一键引入swagger,启动时默认同步文档(可配置)
##### swagger3-showdoc-spring-boot-starter
swagger3版本的spring-boot内嵌应用模块,适用于spring-boot2.2及以上版本,可一键引入swagger,启动时默认同步文档(可配置)
##### swagger-showdoc-demo
使用内嵌模式的spring-boot应用demo

swagger访问地址http://localhost:12346/swagger-ui

##### 依赖引入
            <dependency>
                <groupId>top.aexp</groupId>
                <artifactId>swagger2-showdoc-spring-boot-starter</artifactId>
                <version>1.2.0-RELEASE</version>
            </dependency>

或

            <dependency>
                <groupId>top.aexp</groupId>
                <artifactId>swagger3-showdoc-spring-boot-starter</artifactId>
                <version>1.2.0-RELEASE</version>
            </dependency>
            
注意swagger3仅支持springboot2.2以上版本

##### 配置详解
```properties
#是否启用,可选default false
swagger-showdoc.enable=true

#是否启用自动同步接口,可选default true
swagger-showdoc.auto-sync=true

#服务的context-path,最终接口文档url=host+basePath+接口path,可选default /
swagger-showdoc.base-path=${server.servlet.context-path}

#访问域名,最终接口文档url=host+basePath+接口path,可选default localhost:8080
swagger-showdoc.host=localhost:${server.port}

#你想生成接口的controller包,可选default *
swagger-showdoc.base-pkg=top.aexp.swaggershowdoc.demo.controller

#文档标题,显示在swagger的文档标题
swagger-showdoc.title=测试模块接口文档,可选default null

#文档版本,显示在swagger的文档版本,可选default null
swagger-showdoc.version=1.0.0

#模块名称,最终会在showdoc生成同名一级目录,若需自动生成文档则必填
swagger-showdoc.module=测试模块

#showdoc的访问地址,若需自动生成文档则必填
swagger-showdoc.show-doc.url=192.168.7.28:8000

#showdoc项目设置-开放api里复制api_key,若需自动生成文档则必填
swagger-showdoc.show-doc.api-key=54b1ac60e55905b026c03465fa9c3c892101069755

#showdoc项目设置-开放api里复制api_token,若需自动生成文档则必填
swagger-showdoc.show-doc.api-token=4caff69f332ea5a239fae7e6e5a080f31644285862
```

## License

用户在遵循本项目协议的同时，如果用户下载、安装、使用本项目中所提供的软件，软件作者对任何原因在使用本项目中提供的软件时可能对用户自己或他人造成的任何形式的损失和伤害不承担任何责任。作者有权根据有关法律、法规的变化修改本项目协议。修改后的协议会随附于本项目的新版本中。当发生有关争议时，以最新的协议文本为准。如果用户不同意改动的内容，用户可以自行删除本项目。如果用户继续使用本项目，则视为您接受本协议的变动。

感谢大家 [Star](https://github.com/llongtao/swagger-showdoc/stargazers) & [Fork](https://github.com/llongtao/swagger-showdoc/network/members) 的支持。
