package com.jjc.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * swagger配置
 * @author Allion.li
 * @date 2020/07/10.
 */
@Configuration
@EnableSwagger2
//@ConditionalOnProperty(name = "swagger.enable",  havingValue = "true")
public class Swagger2 {



    /**
     * Every SwaggerSpringMvcPlugin bean is picked up by the swagger-mvc
     * framework - allowing for multiple swagger groups i.e. same code base
     * multiple swagger resource listings.
     */


    @Bean
    public Docket createRestApi() {
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(new ParameterBuilder()
                .name("token")
                .description("认证Token")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .required(true)
                .build());
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                //为当前包路径
                .apis(RequestHandlerSelectors.basePackage("com.jjc.api.controller"))
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(parameters);
//        return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class)).build();
    }




    private ApiInfo apiInfo()
    {
        return new ApiInfoBuilder()
                //页面标题
                .title("Api接口文档")
                //创建人
                .contact(new Contact("Allion", "", ""))
                //版本号
                .version("1.0")
                //描述
                .description("API 接口文档")
                .build();
    }

}
