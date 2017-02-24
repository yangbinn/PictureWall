package com.example.picturewall.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 图库实体类
 *
 * @author youngbin
 *         2016-09-21
 */
public class Picture implements Parcelable {

    private int id;
    private String path; //路径
    private long size; //大小

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }


    public Picture() {
    }

    public Picture(String path, long size) {
        this.path = path;
        this.size = size;
    }

    public static final Creator<Picture> CREATOR = new Creator<Picture>() {

        @Override
        public Picture[] newArray(int size) {
            return new Picture[size];
        }

        @Override
        public Picture createFromParcel(Parcel source) {
            Picture picture = new Picture();
            picture.id = source.readInt();
            picture.path = source.readString();
            picture.size = source.readLong();
            return picture;
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(path);
        dest.writeLong(size);
    }

}

