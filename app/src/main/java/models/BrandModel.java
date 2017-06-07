package models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by haseeb on 9/12/16.
 */
public class BrandModel implements Parcelable {
    String name, target;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }



    public BrandModel() {

    }

    public BrandModel(Parcel in) {
        name = in.readString();
        target = in.readString();
    }



    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(target);
    }

    public int describeContents() {
        return 0;
    }

    public static final Creator<BrandModel> CREATOR = new Creator<BrandModel>() {

        public BrandModel createFromParcel(Parcel in) {
            return new BrandModel(in);
        }

        public BrandModel[] newArray(int size) {
            return new BrandModel[size];
        }
    };
}
