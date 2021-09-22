package com.roy.compiler;

import com.google.auto.service.AutoService;
import com.roy.annotation.ArgumentsConfig;
import com.roy.annotation.ArgumentsField;
import com.roy.annotation.SerializeMode;
import com.roy.compiler.config.Constant;
import com.roy.compiler.utils.MessagerUtils;
import com.roy.compiler.utils.TypeUtils;
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
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

/**
 * desc   :
 * e-mail : 1391324949@qq.com
 * date   : 2021/8/20 11:57
 * author : Roy
 * version: 1.0
 */
// 编译器  干活的
@AutoService(Processor.class) // 编译期 绑定 干活 注册 也就相当于清单文件 编译器会去主动找ArgumentsProcessor 这个类
@SupportedAnnotationTypes({ArgumentsConfig.ARGUMENTS_FIELD_CLASS_NAME})
@SupportedSourceVersion(SourceVersion.RELEASE_8) // 必须写 指定JDK编译版本
@SupportedOptions(ArgumentsConfig.OPTIONS) // 接收值
public class ArgumentsProcessor extends AbstractProcessor {

    private Elements elementTool;// 操作Element的工具类（类，函数，属性，其实都是Element）
    private Filer filer; //文件生成器， 类 资源 等，就是最终要生成的文件 是需要Filer来完成的
    private String myValue; // （模块传递过来的）模块名  app，personal
    private Map<TypeElement, ArrayList<Element>> typeElementArrayListMap = new HashMap<>(); //分类整理每个类对应的注解信息

    //初始化
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        MessagerUtils.init(processingEnvironment.getMessager());
        filer = processingEnvironment.getFiler();
        elementTool = processingEnvironment.getElementUtils();
        myValue = processingEnvironment.getOptions().get(ArgumentsConfig.OPTIONS); //接收到模块gradle配置的值

        MessagerUtils.print("---" + myValue);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        if (annotations.isEmpty()) {
            MessagerUtils.print("并没有发现 被@ArgumentsField注解的地方呀");
            return false; 
        }
        generationAssistance(); //生成辅助工具

        // 获取被 ArgumentsField注解的地方呀注解的 "类节点信息"
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(ArgumentsField.class);
        MessagerUtils.print("---" + elements); //需要用一个集合保存 扫描到的被注解的信息

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
                TypeElement typeElement = next.getKey(); //类
                ArrayList<Element> elementsValue = next.getValue(); //类里面的注解信息
                MessagerUtils.print("----typeElement:" + typeElement.toString());
                String className = typeElement.getSimpleName().toString();
                //获取类名
                MessagerUtils.print("---" + className);
                // 定义要给类名 动态
                String finalClassName = className + ArgumentsConfig.CLASS_APPEND + ArgumentsConfig.CLASS_ARGUMENTS;


                //获取包节点信息
                String pageName = elementTool.getPackageOf(typeElement).getQualifiedName().toString();
                MessagerUtils.print("---" + pageName);
                String activity = "activity";
                // $L 引用匿名内部类   "for (int i = $L; i < $L; i++) 这种也可以
                // $N  引用方法 使用 $N 可以引用另外一个通过名字生成的方法   但是也可以用$L也可以做到
                // $T  类 可以自动导入类型的引用
                // 可以使用 $S 表示一个 string

                //为了得到 类名的具体类型
                // ClassName classNameParameter = ClassName.get(pageName, className);


                // 类
                TypeSpec myClass = TypeSpec.classBuilder(finalClassName)
                        .addMethod(generateInject(pageName, className, activity).build())
                        .addMethod(generateAssignment(pageName, className, activity, elementsValue).build())
                        .addSuperinterface(ClassName.get(ArgumentsConfig.PAGE_NAME_ARGUMENTS_API, ArgumentsConfig.ARGUMENTS_API))
                        .addModifiers(Modifier.PUBLIC)
                        .build();

                javaFileWrite(finalClassName, pageName, myClass);


            }
        }

        return true; //能不能给其他注解处理器使用 true表示还可以交给其他的注解处理器使用 false不可以给其他注解处理器
    }


    /**
     * 生成inject方法
     *
     * @param pageName  包名
     * @param className 类名
     * @param activity  //引用
     * @return
     */
    public MethodSpec.Builder generateInject(String pageName, String className, String activity) {
        // 方法
        MethodSpec.Builder inject = MethodSpec.methodBuilder("inject");
        inject.addModifiers(Modifier.PUBLIC);
        inject.addAnnotation(Constant.overrideClassName);
        inject.addParameter(Constant.objectClassName, Constant.objectVariable);
        inject.beginControlFlow("if(object==null)");
        inject.addStatement("return");
        inject.endControlFlow();

        inject.addStatement("$T $L = ($T)object", ClassName.get(pageName, className), activity, ClassName.get(pageName, className));

        addCallParameterMethod(inject, activity, ClassName.get(ArgumentsConfig.PAGE_NAME_UTILS, ArgumentsConfig.BUNDLE_UTILS_NAME), "getBundle", Constant.objectVariable);
        return inject;
    }

    /**
     * 生成assignment方法
     *
     * @param pageName      包名
     * @param className     类名
     * @param activity      引用
     * @param elementsValue
     * @return
     */
    public MethodSpec.Builder generateAssignment(String pageName, String className, String activity, ArrayList<Element> elementsValue) {

        MethodSpec.Builder assignment = MethodSpec.methodBuilder("assignment");
        assignment.addModifiers(Modifier.PUBLIC);
        assignment.addParameter(ClassName.get(pageName, className), activity);
        assignment.addParameter(Constant.bundleClassName, Constant.bundleVariable);
        assignment.beginControlFlow("if($L !=null)",Constant.bundleVariable);
        addMethodAssignmentContent(activity, assignment, elementsValue);
        assignment.endControlFlow();
        return assignment;
    }

    /**
     * 生成类文件
     *
     * @param finalClassName
     * @param pageName
     * @param myClass
     */
    private void javaFileWrite(String finalClassName, String pageName, TypeSpec myClass) {
        // 包
        JavaFile packf = JavaFile.builder(pageName, myClass).build();
        // 开始生成
        try {
            packf.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
           MessagerUtils.print(finalClassName + "创建失败");
        }
    }


    /**
     * 生成辅助工具 无需依赖外界代码
     */
    private void generationAssistance() {
        // 方法
        MethodSpec.Builder inject = MethodSpec.methodBuilder("getBundle");
        inject.addModifiers(Modifier.STATIC, Modifier.PUBLIC);

        inject.returns(Constant.bundleClassName);
        inject.addParameter(Constant.objectClassName, Constant.objectVariable);
        //Bundle bundle = null;
        inject.addStatement("$T bundle = null", Constant.bundleClassName);
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
        inject.beginControlFlow("if(object instanceof $T)", Constant.fragmentActivityClassName);
        // Intent intent = ((FragmentActivity)object).getIntent();
        inject.addStatement("$T intent = ((FragmentActivity)object).getIntent()", Constant.intentClassName);
        inject.beginControlFlow("if(intent !=null)");
        inject.addStatement(" $L = intent.getExtras()", Constant.bundleVariable);
        inject.endControlFlow();
        inject.endControlFlow();
        inject.addCode("else if(object instanceof $T)", Constant.fragmentClassName);
        inject.beginControlFlow("");
        //Bundle bundle = ((Fragment)object).getArguments()
        inject.addStatement(" $L = ((Fragment)object).getArguments()", Constant.bundleVariable);

        inject.endControlFlow();
        inject.addStatement("return " + Constant.bundleVariable);
        // 类
        TypeSpec myClass = TypeSpec.classBuilder(ArgumentsConfig.BUNDLE_UTILS_NAME)
                .addMethod(inject.build())
                .addModifiers(Modifier.PUBLIC)
                .build();


        // 包
        javaFileWrite(ArgumentsConfig.BUNDLE_UTILS_NAME, ArgumentsConfig.PAGE_NAME_UTILS, myClass);
    }

    /**
     * 添加assignment方法内容
     *
     * @param variable
     * @param assignment
     * @param elements
     */
    private void addMethodAssignmentContent(String variable, MethodSpec.Builder assignment, ArrayList<Element> elements) {

        for (Element element : elements) {
            TypeMirror typeMirror = element.asType();
            MessagerUtils.print("---3" + element.asType());

            //对方法的操作
            if (element instanceof ExecutableElement) {
                ExecutableElement executableElement = (ExecutableElement) element;
                MessagerUtils.print("---2" + executableElement.getSimpleName());
            }

            //获取被注解作用的字段名称
            String argumentsFiled = element.getSimpleName().toString();
            String argumentsFiledKey = element.getAnnotation(ArgumentsField.class).value();
            SerializeMode isSerialize = element.getAnnotation(ArgumentsField.class).isSerialize();
            MessagerUtils.print("---filed" + argumentsFiled);

            // addMethod2(variable,typeMirror);
            MessagerUtils.print("----类型：" + typeMirror);
            int javaType = typeMirror.getKind().ordinal();
            String type = typeMirror.toString(); //typeMirror.getKind().ordinal();

            if (isSerialize == SerializeMode.None) {
                String typeString = TypeUtils.type(javaType, type);
                addParameter(variable, assignment, argumentsFiled, argumentsFiledKey, typeString);

            } else if (isSerialize == SerializeMode.Serializable) {
                // activity.bean = (Bean)bundle.getSerializable("bean");
                assignment.addStatement("$L.$L = ($L)" + Constant.bundleVariable + ".$L(\"$L\")", variable, argumentsFiled, type, "getSerializable", argumentsFiledKey == null || argumentsFiledKey.length() == 0 ? argumentsFiled : argumentsFiledKey);

            } else if (isSerialize == SerializeMode.Parcelable) {
                assignment.addStatement("$L.$L = ($L)" + Constant.bundleVariable + ".$L(\"$L\")", variable, argumentsFiled, type, "getParcelable", argumentsFiledKey == null || argumentsFiledKey.length() == 0 ? argumentsFiled : argumentsFiledKey);

            } else {
                MessagerUtils.print("不支持其他类型");
            }

        }


    }


    private void addParameter(String variable, MethodSpec.Builder assignment, String argumentsFiled, String argumentsFiledKey, String type) {
        assignment.addStatement("$L.$L = " + Constant.bundleVariable + ".$L(\"$L\")", variable, argumentsFiled, type, argumentsFiledKey == null || argumentsFiledKey.length() == 0 ? argumentsFiled : argumentsFiledKey);

    }


    /**
     * 添加调用assignment方法
     *
     * @param inject
     * @param variable
     * @param className
     * @param method
     * @param object
     */
    private void addCallParameterMethod(MethodSpec.Builder inject, String variable, Object className, String method, String object) {

        if (inject != null) {
            //assignment(activity,getBundle(object));
            inject.addStatement("assignment($L,$T.$L($L))", variable, className, method, object);
        }

    }





    /**
     * 泛型代码生成  Map<String,Class<? extends List>> a;
     assignment.addStatement("$T<String,$T> a",ClassName.get("java.util","Map"), ParameterizedTypeName.get(ClassName.get(Class.class),
     WildcardTypeName.subtypeOf(ClassName.get("java.util","List"))));

     */
}
