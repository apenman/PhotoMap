package com.apenman.photomap;

import com.google.gson.annotations.SerializedName;


/**
 * Created by apenman on 2/1/15.
 */
public class ImageMap {
    @SerializedName("name")
    String name;
    @SerializedName("image_list")
    ImageData[] imageList;

    public ImageMap(String name, ImageData[] imageList) {
        this.name = name;
        this.imageList = imageList;
    }

    public String getName() {
        return name;
    }

    public ImageData[] getImageList() {
        return imageList;
    }

    @Override
    public String toString() {
        return name;
    }
}
