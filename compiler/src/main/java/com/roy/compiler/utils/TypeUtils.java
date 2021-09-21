package com.roy.compiler.utils;

import com.roy.compiler.config.Constant;

import javax.lang.model.type.TypeKind;

/**
 * desc   :
 * e-mail : 1391324949@qq.com
 * date   : 2021/9/17 15:59
 * author : Roy
 * version: 1.0
 */
public class TypeUtils {
    public static String type(int javaType,String kotlinType){
        String typeString;
        if (kotlinType.equals(Constant.INTEGER) || javaType == TypeKind.INT.ordinal()) {//兼容kotlin TypeKind.INT.ordinal()
            typeString = "getInt";
        } else if (kotlinType.equals(Constant.BOOLEAN) || javaType == TypeKind.BOOLEAN.ordinal()) { //TypeKind.BOOLEAN.ordinal()
            typeString = "getBoolean";
        } else if (kotlinType.equals(Constant.BYTE) || javaType == TypeKind.BYTE.ordinal()) { //TypeKind.BYTE.ordinal()
            typeString = "getByte";
        } else if (kotlinType.equals(Constant.SHORT) || javaType == TypeKind.SHORT.ordinal()) { //TypeKind.SHORT.ordinal()
            typeString = "getShort";
        } else if (kotlinType.equals(Constant.LONG) || javaType == TypeKind.LONG.ordinal()) { //TypeKind.LONG.ordinal()
            typeString = "getLong";
        } else if (kotlinType.equals(Constant.FLOAT) || javaType == TypeKind.FLOAT.ordinal()) { //TypeKind.FLOAT.ordinal()
            typeString = "getFloat";
        } else if (kotlinType.equals(Constant.DOUBLE) || javaType == TypeKind.DOUBLE.ordinal()) { //TypeKind.DOUBLE.ordinal()
            typeString = "getDouble";
        } else if (kotlinType.equals(Constant.CHAR) || javaType == TypeKind.CHAR.ordinal()) {//TypeKind.CHAR.ordinal()
            typeString = "getChar";
        } else {
             typeString = Constant.GET_STRING;
        }
        return typeString;
    }
}
