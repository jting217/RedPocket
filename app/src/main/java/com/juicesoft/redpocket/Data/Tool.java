package com.juicesoft.redpocket.Data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jting on 2017/3/2.
 */

public class Tool  implements Parcelable {
    private int product;
    private int times;

    public Tool(Parcel in) {
        product = in.readInt();
        times = in.readInt();
    }

    public static final Creator<Tool> CREATOR = new Creator<Tool>() {
        @Override
        public Tool createFromParcel(Parcel in) {
            return new Tool(in);
        }

        @Override
        public Tool[] newArray(int size) {
            return new Tool[size];
        }
    };

    public Tool() {

    }

    public int getProduct() {
        return product;
    }

    public void setProduct(int product) {
        this.product = product;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(product);
        dest.writeInt(times);
    }
}
