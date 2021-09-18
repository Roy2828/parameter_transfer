package com.roy.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * desc   :
 * e-mail : 1391324949@qq.com
 * date   : 2021/8/20 11:54
 * author : Roy
 * version: 1.0
 */
@Inherited
@Target(ElementType.FIELD) //作用在字段上
@Retention(RetentionPolicy.CLASS) //编译时执行
public @interface ArgumentsField {
    String value() default "";
    SerializeMode isSerialize() default SerializeMode.None;  //序列化
}
