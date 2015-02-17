package com.apenman.photomap;

import java.util.List;

/**
 * Created by apenman on 2/15/15.
 */
public class GlobalList {
    private static GlobalList globalInstance;
    private static List<ImageMap> currMapList;
    private static ImageMap currMap;
    private static ImageData currImage;
    private static int currImageIndex;



    private GlobalList(){}

    public static GlobalList getGlobalInstance() {
        if(globalInstance == null) {
            globalInstance = new GlobalList();
        }

        return globalInstance;
    }

    public static void setCurrMapList(List<ImageMap> mapList) {
        currMapList = mapList;
    }

    public static void setCurrMap(ImageMap map) {
        currMap = map;
    }

    public static void setCurrImage(ImageData image) {
        currImage = image;
    }

    public static void setCurrImageIndex(int index) {
        currImageIndex = index;
    }

    public static boolean setNextImage() {
        int size = currMap.getImageList().size();
        if (size != 0) {
            if (-1 + size == currImageIndex) {
                currImageIndex = 0;
            } else {
                currImageIndex += 1;
            }
            currImage = currMap.getImageList().get(currImageIndex);
            return true;
        }

        return false;
    }

    public static boolean setPrevImage() {
        int size = currMap.getImageList().size();
        if (size != 0) {
            if (currImageIndex == 0) {
                currImageIndex = size - 1;
            } else {
                currImageIndex -= 1;
            }
            currImage = currMap.getImageList().get(currImageIndex);
            return true;
        }

        return false;
    }

    public static List<ImageMap> getCurrMapList() {
        return currMapList;
    }

    public static ImageMap getCurrMap() {
        return currMap;
    }

    public static ImageData getCurrImage() {
        return currImage;
    }

    public static int getCurrImageIndex() {
        return currImageIndex;
    }
}
