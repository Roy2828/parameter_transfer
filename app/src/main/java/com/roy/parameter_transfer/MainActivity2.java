package com.roy.parameter_transfer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.roy.annotation.ArgumentsField;
import com.roy.annotation.SerializeMode;
import com.roy.api.ParameterManager;

public class MainActivity2 extends BaseActivity {

    @ArgumentsField(value = "bb")
    public String name;
    @ArgumentsField(value = "ss")
    public String names;
    @ArgumentsField
    public int age;

    @ArgumentsField(isSerialize = SerializeMode.Serializable) //可加可不加 内部会自动化
    public Bean bean;
    @ArgumentsField(value = "parcelable")
    public ParcelableBean parcelableBean;

    public static void launch(Context context,String bb,String ss,int age,Bean bean,ParcelableBean parcelable){
        Intent intent = new Intent(context,MainActivity2.class);
        intent.putExtra("bb",bb);
        intent.putExtra("ss",ss);
        intent.putExtra("age",age);
        intent.putExtra("bean",bean);
        intent.putExtra("parcelable",parcelable);
        intent.putExtra("base","base1数据");
        intent.putExtra("base2","base2数据");
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ParameterManager.getParameterManager().inject(this);
        Log.e("aa",""+name);
        Log.e("aa",""+names);

    }
}