package com.roy.compiler.config;

import com.squareup.javapoet.ClassName;

/**
 * desc   :
 * e-mail : 1391324949@qq.com
 * date   : 2021/9/17 15:48
 * author : Roy
 * version: 1.0
 */
public class Constant {
    public static final String INTEGER = "java.lang.Integer";
    public static final String BOOLEAN = "java.lang.Boolean";
    public static final String BYTE = "java.lang.Byte";
    public static final String SHORT = "java.lang.Short";
    public static final String LONG = "java.lang.Long";
    public static final String FLOAT = "java.lang.Float";
    public static final String DOUBLE = "java.lang.Double";
    public static final String CHAR = "java.lang.Char";

    public static final String GET_STRING = "getString";



    public static ClassName objectClassName = ClassName.get("java.lang", "Object");
    public static String objectVariable = "object";

    public static ClassName bundleClassName = ClassName.get("android.os", "Bundle");
    public static String bundleVariable = "bundle";

    public static ClassName fragmentClassName = ClassName.get("androidx.fragment.app", "Fragment");
    public static ClassName fragmentActivityClassName = ClassName.get("androidx.fragment.app", "FragmentActivity");

    public static ClassName intentClassName = ClassName.get("android.content", "Intent");

    public static ClassName overrideClassName = ClassName.get("java.lang", "Override");
}
