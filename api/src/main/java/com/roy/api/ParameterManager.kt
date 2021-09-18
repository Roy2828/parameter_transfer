package com.roy.api

import android.util.LruCache
import com.roy.annotation.ArgumentsConfig
import java.util.*
import kotlin.collections.HashMap

/**
 *    desc   :
 *    e-mail : 1391324949@qq.com
 *    date   : 2021/9/17 13:38
 *    author : Roy
 *    version: 1.0
 */
class ParameterManager private constructor(){
    var cacheMap:LruCache<String,ArgumentsApi?> = LruCache(50)
    val splicingName = ArgumentsConfig.CLASS_APPEND+ArgumentsConfig.CLASS_ARGUMENTS

    companion object{
        @JvmStatic
        val parameterManager:ParameterManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
            ParameterManager()
        }
    }


    fun inject(any: Any?){
        any?.let {
            val className = it::class.java.simpleName;

            var argumentsApi:ArgumentsApi? = cacheMap.get(className)
            argumentsApi?:apply {
              try {
                  var clazz:Class<*>? = Class.forName(any::class.java.`package`.name+"."+className+splicingName)
                  clazz?.let {
                      argumentsApi =  clazz.newInstance() as ArgumentsApi?
                      cacheMap.put(className,argumentsApi)
                  }
              } catch (e: Exception) {
                  e.printStackTrace()
              }
            }
            argumentsApi?.inject(any)
        }
    }
}