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
    @SerializedName("description")
    String description;

    public ImageMap(String name, ImageData[] imageList, String description) {
        this.name = name;
        this.imageList = imageList;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public ImageData[] getImageList() {
        return imageList;
    }

    public String getDescription() { return description; }

    @Override
    public String toString() {
        return name + "\n" + description;
    }
}
