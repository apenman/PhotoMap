package com.apenman.photomap;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

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
    private static List<Marker> markerList;
    private static GoogleMap map;



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

    public static void setMarkerList(List<Marker> list) { markerList = list; }

    public static void setMap(GoogleMap setMap) { map = setMap; }

    public static boolean setNextImage() {
        int size = currMap.getImageList().size();
        if (size != 0) {
            if (-1 + size == currImageIndex) {
                currImageIndex = 0;
            } else {
                currImageIndex += 1;
            }
            currImage = currMap.getImageList().get(currImageIndex);
            map.animateCamera(CameraUpdateFactory.newLatLng(markerList.get(currImageIndex).getPosition()));
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
            map.animateCamera(CameraUpdateFactory.newLatLng(markerList.get(currImageIndex).getPosition()));
            return true;
        }

        return false;
    }

    public static void removeImage() {
        if(currImage != null) {
            currMap.getImageList().remove(currImage);
            markerList.remove(currImageIndex);

            /* Adjust current Index if the last image in list was removed */
            if(currImageIndex >= currMap.getImageList().size()) {
                currImageIndex = currMap.getImageList().size() -1;
            }
            currImage = currMap.getImageList().get(currImageIndex);
            map.animateCamera(CameraUpdateFactory.newLatLng(markerList.get(currImageIndex).getPosition()));
        }
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

    public static GoogleMap getMap() { return map; }
}
