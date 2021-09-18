package com.roy.parameter_transfer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.roy.annotation.ArgumentsField

class BlankFragment : Fragment() {

    @JvmField
    @ArgumentsField
    var aaa:String?=null
    @JvmField
    @ArgumentsField
    var ddd:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank, container, false)
    }


}