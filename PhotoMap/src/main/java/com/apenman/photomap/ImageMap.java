package com.apenman.photomap;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by apenman on 2/1/15.
 */
public class ImageMap {
    @SerializedName("name")
    String name;
    @SerializedName("image_list")
    List<ImageData> imageList = new ArrayList<ImageData>();
    @SerializedName("description")
    String description;

    public ImageMap(String name, List<ImageData> imageList, String description) {
        this.name = name;
        this.imageList = imageList;
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public List<ImageData> getImageList() {
        return imageList;
    }

    public String getDescription() { return description; }

    @Override
    public String toString() {
        return name + "\n" + description;
    }
}
