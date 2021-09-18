package com.roy.parameter_transfer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * desc   :
 * e-mail : 1391324949@qq.com
 * date   : 2021/9/16 19:01
 * author : Roy
 * version: 1.0
 */
public class ParcelableBean implements Parcelable {
    private String name;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
    }

    public ParcelableBean() {
    }

    protected ParcelableBean(Parcel in) {
        this.name = in.readString();
    }

    public static final Creator<ParcelableBean> CREATOR = new Creator<ParcelableBean>() {
        @Override
        public ParcelableBean createFromParcel(Parcel source) {
            return new ParcelableBean(source);
        }

        @Override
        public ParcelableBean[] newArray(int size) {
            return new ParcelableBean[size];
        }
    };
}
