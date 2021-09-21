package com.roy.compiler.utils;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

/**
 * desc   :
 * e-mail : 1391324949@qq.com
 * date   : 2021/9/17 16:41
 * author : Roy
 * version: 1.0
 */
public class MessagerUtils {

    private static Messager messager;


    public static void init(Messager messager){
        MessagerUtils.messager = messager;
    }


    public static void print(String s) {
        messager.printMessage(Diagnostic.Kind.NOTE, s);
    }


    public static void print(Diagnostic.Kind var1, CharSequence var2){
        messager.printMessage(var1, var2);
    }

}
