package com.example.ambe.imagetoimage.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by AMBE on 12/12/2018 at 8:38 AM.
 */
public class MyImage implements Parcelable {
    private String path;
    private String name;
    private String id;
    private boolean isSelected;

    public MyImage() {
    }

    public MyImage(String path, String name, String id, boolean isSelected) {
        this.path = path;
        this.name = name;
        this.id = id;
        this.isSelected = isSelected;
    }

    protected MyImage(Parcel in) {
        path = in.readString();
        name = in.readString();
        id = in.readString();
        isSelected = in.readByte() != 0;
    }

    public static final Creator<MyImage> CREATOR = new Creator<MyImage>() {
        @Override
        public MyImage createFromParcel(Parcel in) {
            return new MyImage(in);
        }

        @Override
        public MyImage[] newArray(int size) {
            return new MyImage[size];
        }
    };

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(name);
        dest.writeString(id);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }
}
