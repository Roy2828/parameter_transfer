package com.roy.parameter_transfer;

import com.roy.annotation.ArgumentsField;

import androidx.appcompat.app.AppCompatActivity;

/**
 * desc   :
 * e-mail : 1391324949@qq.com
 * date   : 2023/5/8 14:00
 * author : Roy
 * version: 1.0
 */
public abstract class BaseActivity extends AppCompatActivity {
    @ArgumentsField(value = "base")
    public String base;

    @ArgumentsField(value = "base2")
    public String base2;
}
