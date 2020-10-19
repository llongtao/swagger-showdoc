# swagger-showdoc

[![License](https://img.shields.io/badge/license-GPL-blue)](https://github.com/llongtao/swagger-showdoc/blob/master/LICENSE)
[![SpringBoot](https://img.shields.io/badge/SpringBoot-2.1.11.RELEASE-brightgreen.svg)](https://docs.spring.io/spring-boot/docs/2.0.3.RELEASE/reference/htmlsingle/)

#### 项目介绍
公司统一使用ShowDoc记录接口文档,为了避免重复劳动打算寻找一款swagger转化为ShowDoc的工具,可是找了很多都没有合适的,于是打算自己开发一个,目前这套已满足日常使用需求.

提供两种方式生成文档
1. 启动独立应用页面填写生成,无入侵项目
2. 应用内嵌,启动项目时生成(可选择关闭)
#### 软件架构

Spring Boot 2.0.3

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

注:非局域网部署的无法连接到你的局域网,请在自己局域网部署
##### swagger-showdoc-spring-boot-starter
spring-boot内嵌应用模块,一键引入swagger,启动时默认同步文档(可配置)
##### swagger-showdoc-demo
使用内嵌模式的spring-boot应用demo

swagger访问地址http://localhost:12346/swagger-ui

##### 依赖引入
            <dependency>
                <groupId>top.aexp</groupId>
                <artifactId>swagger-showdoc-spring-boot-starter</artifactId>
                <version>1.0.0-RELEASE</version>
            </dependency>
            
##### 低版本spring-boot适配 (2.2以下)
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>io.springfox</groupId>
                <artifactId>springfox-spring-web</artifactId>
                <version>3.0.0</version>
            </dependency>
            <dependency>
                <groupId>io.springfox</groupId>
                <artifactId>springfox-spi</artifactId>
                <version>3.0.0</version>
            </dependency>
            <dependency>
                <groupId>io.springfox</groupId>
                <artifactId>springfox-swagger2</artifactId>
                <version>3.0.0</version>
            </dependency>
            <dependency>
                <groupId>io.springfox</groupId>
                <artifactId>springfox-swagger-common</artifactId>
                <version>3.0.0</version>
            </dependency>
            <dependency>
                <groupId>io.springfox</groupId>
                <artifactId>springfox-bean-validators</artifactId>
                <version>3.0.0</version>
            </dependency>
            <dependency>
                <groupId>org.springframework.plugin</groupId>
                <artifactId>spring-plugin-core</artifactId>
                <version>2.0.0.RELEASE</version>
            </dependency>
            
            <dependency>
                <groupId>org.springframework.plugin</groupId>
                <artifactId>spring-plugin-metadata</artifactId>
                <version>2.0.0.RELEASE</version>
            </dependency>

        </dependencies>

    </dependencyManagement>

## License

用户在遵循本项目协议的同时，如果用户下载、安装、使用本项目中所提供的软件，软件作者对任何原因在使用本项目中提供的软件时可能对用户自己或他人造成的任何形式的损失和伤害不承担任何责任。作者有权根据有关法律、法规的变化修改本项目协议。修改后的协议会随附于本项目的新版本中。当发生有关争议时，以最新的协议文本为准。如果用户不同意改动的内容，用户可以自行删除本项目。如果用户继续使用本项目，则视为您接受本协议的变动。

感谢大家 [Star](https://github.com/llongtao/swagger-showdoc/stargazers) & [Fork](https://github.com/llongtao/swagger-showdoc/network/members) 的支持。
