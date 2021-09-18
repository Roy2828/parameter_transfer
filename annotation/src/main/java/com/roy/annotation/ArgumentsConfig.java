package com.roy.annotation;

/**
 * desc   :
 * e-mail : 1391324949@qq.com
 * date   : 2021/9/17 16:43
 * author : Roy
 * version: 1.0
 */
public class ArgumentsConfig {
    public static final String CLASS_APPEND = "$$$$$$"; //类名添加调料为了不合被注解的类同名
    public static final String CLASS_ARGUMENTS = "Arguments";//类名添加调料为了不合被注解的类同名
    public static final String PAGE_NAME_UTILS = "com.roy.api.utils";//工具包名

    public static final String BUNDLE_UTILS_NAME = "BundleUtils"+CLASS_APPEND; //工具类名
    public static final String PAGE_NAME_ARGUMENTS_API = "com.roy.api";//注入包名
    public static final String ARGUMENTS_API="ArgumentsApi";//注入接口名

    public static final String OPTIONS = "myvalue"; //gradle文件里面配置的参数  用于后期扩展

    public static final String ARGUMENTS_FIELD_CLASS_NAME = "com.roy.annotation.ArgumentsField";

}
