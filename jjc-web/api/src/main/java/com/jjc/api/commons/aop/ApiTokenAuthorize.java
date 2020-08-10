package com.jjc.api.commons.aop;

import java.lang.annotation.*;

/**
 * 验证授权注解
 * @author huoquan
 * @date 2019/2/21.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiTokenAuthorize {
    //描述
    String value() default "";
}
