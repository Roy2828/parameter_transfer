package com.roy.parameter_transfer

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.roy.annotation.ArgumentsField
import com.roy.annotation.SerializeMode
import com.roy.api.ParameterManager.Companion.parameterManager

class MainActivity : AppCompatActivity() {
    @JvmField
    @ArgumentsField(value = "age")
    var age: String? = null

    @JvmField
    @ArgumentsField
    var name:String?=null

    @JvmField
    @ArgumentsField
    var dd:Int?=0

    @JvmField
    @ArgumentsField
    var f:Float?=0f

    @JvmField
    @ArgumentsField
    var d:Double?=0.0

    @JvmField
    @ArgumentsField
    var l:Long?=0

    @JvmField
    @ArgumentsField
    var bd:Boolean?=false

    @JvmField
    @ArgumentsField(value = "parcelable", isSerialize = SerializeMode.Parcelable)
    var parcelableBean: ParcelableBean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parameterManager.inject(this)
        setContentView(R.layout.activity_main)
         findViewById<TextView>(R.id.tv3).setOnClickListener{
             MainActivity2.launch(this@MainActivity, "sd", "dsfe", 12, Bean(), ParcelableBean())
         }

    }
}