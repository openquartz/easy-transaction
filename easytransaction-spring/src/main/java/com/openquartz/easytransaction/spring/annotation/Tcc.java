package com.openquartz.easytransaction.spring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author svnee
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Tcc {

    /**
     * 提交method
     */
    String confirmMethod() default "";

    /**
     * 取消method
     */
    String cancelMethod() default "";

    /**
     * Try method 重试次数。默认 不重试
     */
    int retryCount() default 0;

    /**
     * 重试时间间隔 单位：毫秒
     */
    long retryInterval() default 0;
}
