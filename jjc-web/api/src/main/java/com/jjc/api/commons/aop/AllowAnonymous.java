package com.jjc.api.commons.aop;

import java.lang.annotation.*;

/**
 * @author huoquan
 * @date 2019/3/6.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AllowAnonymous {
    //描述
    String value() default "";
}
