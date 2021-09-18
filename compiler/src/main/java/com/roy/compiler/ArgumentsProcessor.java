package com.roy.compiler;

import com.google.auto.service.AutoService;
import com.roy.annotation.ArgumentsConfig;
import com.roy.annotation.ArgumentsField;
import com.roy.annotation.SerializeMode;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * desc   :
 * e-mail : 1391324949@qq.com
 * date   : 2021/8/20 11:57
 * author : Roy
 * version: 1.0
 */
// 编译器  干活的
@AutoService(Processor.class) // 编译期 绑定 干活 注册 也就相当于清单文件 编译器会去主动找ArgumentsProcessor 这个类
@SupportedAnnotationTypes({"com.roy.annotation.ArgumentsField"})
@SupportedSourceVersion(SourceVersion.RELEASE_8) // 必须写
@SupportedOptions("myvalue") // 接收值
public class ArgumentsProcessor extends AbstractProcessor {

    // 操作Element的工具类（类，函数，属性，其实都是Element）
    private Elements elementTool;
    Messager messager;
    private Filer filer;
    private String myValue;
    private Map<TypeElement, ArrayList<Element>> typeElementArrayListMap = new HashMap<>();

    private ClassName objectClassName = ClassName.get("java.lang", "Object");
    private String objectVariable = "object";

    private ClassName bundleClassName = ClassName.get("android.os", "Bundle");
    private String bundleVariable = "bundle";

    private ClassName fragmentClassName = ClassName.get("androidx.fragment.app", "Fragment");
    private ClassName fragmentActivityClassName = ClassName.get("androidx.fragment.app", "FragmentActivity");

    private ClassName intentClassName = ClassName.get("android.content", "Intent");

    private ClassName overrideClassName = ClassName.get("java.lang", "Override");
    //初始化
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();
        elementTool = processingEnvironment.getElementUtils();
        myValue = processingEnvironment.getOptions().get(ArgumentsConfig.OPTIONS); //接收到模块gradle配置的值

        print("---" + myValue);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (set.isEmpty()) {
            print("并没有发现 被@ArgumentsField注解的地方呀");
            return false; // 我根本就没有机会处理  你还没有干活
        }
        generationAssistance(); //生成辅助工具

        // 获取被 ArgumentsField注解的地方呀注解的 "类节点信息"
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(ArgumentsField.class);
        print("---" + elements); //需要用一个集合保存 扫描到的被注解的信息

        for (Element element : elements) {
            //
            //对类的操作
            TypeElement classElement = (TypeElement) element
                    .getEnclosingElement();
            if (typeElementArrayListMap.get(classElement) == null) {
                ArrayList<Element> elementsField = new ArrayList<>();
                elementsField.add(element);
                typeElementArrayListMap.put(classElement, elementsField);
            } else {
                typeElementArrayListMap.get(classElement).add(element);
            }
        }

        if (typeElementArrayListMap.size() > 0) {
            Set<Map.Entry<TypeElement, ArrayList<Element>>> entries = typeElementArrayListMap.entrySet();
            Iterator<Map.Entry<TypeElement, ArrayList<Element>>> iterator =
                    entries.iterator();
            while (iterator.hasNext()) {
                Map.Entry<TypeElement, ArrayList<Element>> next = iterator.next();
                TypeElement typeElement = next.getKey();
                ArrayList<Element> elementsValue = next.getValue();
                print("----typeElement:" + typeElement.toString());
                String className = typeElement.getSimpleName().toString();
                //获取类名
                print("---" + className);
                // 定义要给类名 动态
                String finalClassName = className + ArgumentsConfig.CLASS_APPEND + ArgumentsConfig.CLASS_ARGUMENTS;
                // 1.方法
                MethodSpec.Builder inject = MethodSpec.methodBuilder("inject");

                //获取包节点信息
                String pageName = elementTool.getPackageOf(typeElement).getQualifiedName().toString();
                print("---" + pageName);

                // $L 引用匿名内部类   "for (int i = $L; i < $L; i++) 这种也可以
                // $N  引用方法
                // $T 引用类 可以自动导入类型的引用
                // 可以使用 $S 表示一个 string

                //为了得到 类名的具体类型
                // ClassName classNameParameter = ClassName.get(pageName, className);

                inject.addModifiers(Modifier.PUBLIC);
                inject.addAnnotation(overrideClassName);
                inject.addParameter(objectClassName, objectVariable);
                inject.beginControlFlow("if(object==null)");
                inject.addStatement("return");
                inject.endControlFlow();
                String activity = "activity";
                inject.addStatement("$T $L = ($T)object", ClassName.get(pageName, className), activity, ClassName.get(pageName, className));

                addParameterMethod(inject, activity, ClassName.get(ArgumentsConfig.PAGE_NAME_UTILS, ArgumentsConfig.BUNDLE_UTILS_NAME), "getBundle", objectVariable);


                MethodSpec.Builder assignment = MethodSpec.methodBuilder("assignment");
                assignment.addModifiers(Modifier.PUBLIC);
                assignment.addParameter(ClassName.get(pageName, className), activity);
                assignment.addParameter(bundleClassName, bundleVariable);
                addMethod2(activity, assignment, elementsValue);

                // 2.类
                TypeSpec myClass = TypeSpec.classBuilder(finalClassName)
                        .addMethod(inject.build())
                        .addMethod(assignment.build())
                        .addSuperinterface(ClassName.get(ArgumentsConfig.PAGE_NAME_ARGUMENTS_API, ArgumentsConfig.ARGUMENTS_API))
                        .addModifiers(Modifier.PUBLIC)
                        .build();

                javaFileWrite(finalClassName, pageName, myClass);


            }
        }

        return true;
    }

    /**
     * 生成类文件
     *
     * @param finalClassName
     * @param pageName
     * @param myClass
     */
    private void javaFileWrite(String finalClassName, String pageName, TypeSpec myClass) {
        // 3.包
        JavaFile packf = JavaFile.builder(pageName, myClass).build();
        // 开始生成
        try {
            packf.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
            messager.printMessage(Diagnostic.Kind.NOTE, finalClassName + "创建失败");
        }
    }


    /**
     * 生成辅助工具 无需依赖外界代码
     */
    private void generationAssistance() {
        // 1.方法
        MethodSpec.Builder inject = MethodSpec.methodBuilder("getBundle");
        inject.addModifiers(Modifier.STATIC, Modifier.PUBLIC);

        inject.returns(bundleClassName);
        inject.addParameter(objectClassName, objectVariable);
        //Bundle bundle = null;
        inject.addStatement("$T bundle = null", bundleClassName);
        /**
         *  if(object instanceof FragmentActivity) {
         *      *       Intent intent = ((FragmentActivity)object).getIntent();
         *      *       if(intent !=null) {
         *      *          bundle = intent.getExtras();
         *      *       }
         *      *     }
         *      *     else if(object instanceof Fragment) {
         *      *       bundle = ((Fragment)object).getArguments();
         *      *     }
         */
        inject.beginControlFlow("if(object instanceof $T)", fragmentActivityClassName);
        // Intent intent = ((FragmentActivity)object).getIntent();
        inject.addStatement("$T intent = ((FragmentActivity)object).getIntent()", intentClassName);
        inject.beginControlFlow("if(intent !=null)");
        inject.addStatement(" $L = intent.getExtras()", bundleVariable);
        inject.endControlFlow();
        inject.endControlFlow();
        inject.addCode("else if(object instanceof $T)", fragmentClassName);
        inject.beginControlFlow("");
        //Bundle bundle = ((Fragment)object).getArguments()
        inject.addStatement(" $L = ((Fragment)object).getArguments()", bundleVariable);

        inject.endControlFlow();
        inject.addStatement("return " + bundleVariable);
        // 2.类
        TypeSpec myClass = TypeSpec.classBuilder(ArgumentsConfig.BUNDLE_UTILS_NAME)
                .addMethod(inject.build())
                .addModifiers(Modifier.PUBLIC)
                .build();


        // 3.包
        javaFileWrite(ArgumentsConfig.BUNDLE_UTILS_NAME, ArgumentsConfig.PAGE_NAME_UTILS, myClass);
    }

    private void addMethod2(String variable, MethodSpec.Builder assignment, ArrayList<Element> elements) {

     /*   ClassName classNameParameter = ClassName.get("java.lang", "Object");

        assignment.addParameter()*/

        for (Element element : elements) {


            TypeMirror typeMirror = element.asType();
            print("---3" + element.asType());

            //对方法的操作
            if (element instanceof ExecutableElement) {
                ExecutableElement executableElement = (ExecutableElement) element;
                print("---2" + executableElement.getSimpleName());
            }

            //获取被注解作用的字段名称
            String argumentsFiled = element.getSimpleName().toString();
            String argumentsFiledKey = element.getAnnotation(ArgumentsField.class).value();
            SerializeMode isSerialize = element.getAnnotation(ArgumentsField.class).isSerialize();
            print("---filed" + argumentsFiled);


            // addMethod2(variable,typeMirror);

             print("----类型："+typeMirror);
            int javaType = typeMirror.getKind().ordinal();
            String type = typeMirror.toString(); //typeMirror.getKind().ordinal();
            String typeString = "getString";
            if (type.equals("java.lang.Integer") || javaType == TypeKind.INT.ordinal()) {//兼容kotlin TypeKind.INT.ordinal()
                typeString = "getInt";
            } else if (type.equals("java.lang.Boolean") || javaType == TypeKind.BOOLEAN.ordinal()) { //TypeKind.BOOLEAN.ordinal()
                typeString = "getBoolean";
            } else if (type.equals("java.lang.Byte") || javaType == TypeKind.BYTE.ordinal()) { //TypeKind.BYTE.ordinal()
                typeString = "getByte";
            } else if (type.equals("java.lang.Short") || javaType == TypeKind.SHORT.ordinal()) { //TypeKind.SHORT.ordinal()
                typeString = "getShort";
            } else if (type.equals("java.lang.Long") || javaType == TypeKind.LONG.ordinal()) { //TypeKind.LONG.ordinal()
                typeString = "getLong";
            } else if (type.equals("java.lang.Float") || javaType == TypeKind.FLOAT.ordinal()) { //TypeKind.FLOAT.ordinal()
                typeString = "getFloat";
            } else if (type.equals("java.lang.Double") || javaType == TypeKind.DOUBLE.ordinal()) { //TypeKind.DOUBLE.ordinal()
                typeString = "getDouble";
            } else if (type.equals("java.lang.Char") || javaType == TypeKind.CHAR.ordinal()) {//TypeKind.CHAR.ordinal()
                typeString = "getChar";
            } else {
                //操作反列化

            }
            if (isSerialize == SerializeMode.None) {
                addParameter(variable, assignment, argumentsFiled, argumentsFiledKey, typeString);
            } else if (isSerialize == SerializeMode.Serializable) {
                // activity.bean = (Bean)bundle.getSerializable("bean");
                assignment.addStatement("$L.$L = ($L)" + bundleVariable + ".$L(\"$L\")", variable, argumentsFiled, type, "getSerializable", argumentsFiledKey == null || argumentsFiledKey.length() == 0 ? argumentsFiled : argumentsFiledKey);

            } else if (isSerialize == SerializeMode.Parcelable) {
                assignment.addStatement("$L.$L = ($L)" + bundleVariable + ".$L(\"$L\")", variable, argumentsFiled, type, "getParcelable", argumentsFiledKey == null || argumentsFiledKey.length() == 0 ? argumentsFiled : argumentsFiledKey);

            } else {
                print("不支持其他类型");
            }

        }


    }


    private void addParameter(String variable, MethodSpec.Builder assignment, String argumentsFiled, String argumentsFiledKey, String type) {
        assignment.addStatement("$L.$L = " + bundleVariable + ".$L(\"$L\")", variable, argumentsFiled, type, argumentsFiledKey == null || argumentsFiledKey.length() == 0 ? argumentsFiled : argumentsFiledKey);

    }


    private void addParameterMethod(MethodSpec.Builder inject, String variable, Object className, String method, String object) {

        if (inject != null) {
            //assignment(activity,getBundle(object));

            inject.addStatement("assignment($L,$T.$L($L))", variable, className, method, object);
        }

    }

    private void print(String s) {
        messager.printMessage(Diagnostic.Kind.NOTE, s);
    }

}
