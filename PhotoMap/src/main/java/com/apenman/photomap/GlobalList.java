package com.apenman.photomap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
    private static int uploadCounter;
    private static int successUploadCounter;



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

            if(markerList.get(currImageIndex) != null) {
                map.animateCamera(CameraUpdateFactory.newLatLng(markerList.get(currImageIndex).getPosition()));
                // Zoom in, animating the camera.
//                map.animateCamera(CameraUpdateFactory.zoomIn());
                // Zoom out to zoom level 10, animating with a duration of 2 seconds.
//                map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
//                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            }
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

            if(markerList.get(currImageIndex) != null) {
                map.animateCamera(CameraUpdateFactory.newLatLng(markerList.get(currImageIndex).getPosition()));
                // Zoom in, animating the camera.
//                map.animateCamera(CameraUpdateFactory.zoomIn());
                // Zoom out to zoom level 10, animating with a duration of 2 seconds.
//                map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
//                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            }
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
            if(markerList.get(currImageIndex) != null) {
                map.animateCamera(CameraUpdateFactory.newLatLng(markerList.get(currImageIndex).getPosition()));
                // Zoom in, animating the camera.
//                map.animateCamera(CameraUpdateFactory.zoomIn());
                // Zoom out to zoom level 10, animating with a duration of 2 seconds.
//                map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
//                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            }
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

    public static void resetUploadCounter() { uploadCounter = 1; successUploadCounter = 1; }

    public static boolean uploadImages(final String albumId, final Session session) {
        Bitmap bi;
        ByteArrayOutputStream stream;
        InputStream fis;
        Request request;
        String albumPath = albumId + "/photos";

        System.out.println("UPLOADING IMAGE #" + uploadCounter);
        if(uploadCounter <= currMap.getImageList().size()) {
            String imgPath = currMap.getImageList().get(uploadCounter).getImagePath();
            uploadCounter++;

            Request.Callback requestCallBack = null;
            try {
                requestCallBack = new Request.Callback() {
                    @Override
                    public void onCompleted(Response response) {
                        JSONObject graphResponse = response.getGraphObject().getInnerJSONObject();
                        String postId = null;
                        try {
                            /* if successful, iterate over map and add each photo to the album */
                            uploadImages(albumId, session);
                            checkCounter();
                        } catch(Exception e) {
                            System.out.println("RETRIEVING POST ID FAILED");
                            checkCounter();
                        }
                    }
                };
            } catch(Exception e) {
                System.out.println("CALLBACK FAILED");
            }


            byte[] b;
            Bitmap bm;
            ByteArrayOutputStream baos;
            try {
                System.out.println("NEW IMG");

                fis = new FileInputStream(imgPath);

                bm = BitmapFactory.decodeStream(fis);
                baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                bm.recycle();

                b = baos.toByteArray();

                fis.close();
                baos.close();

                Bundle bundle = new Bundle();
                bundle.putByteArray("source", b);
                request = new Request(session, albumPath, bundle, HttpMethod.POST, requestCallBack);
                RequestAsyncTask requestAsyncTask = new RequestAsyncTask(request);
                requestAsyncTask.execute();
                System.out.println("DONE!!!");
            } catch (FileNotFoundException e) {
                System.out.println("FILE NOT FOUND");
            } catch (IOException e) {
                System.out.println("IO EXCEPTION");
            }
        }

        return true;
    }

    private static boolean checkCounter() {
        successUploadCounter++;
        System.out.println("CHECKING UPLOAD #" + successUploadCounter + " of " + currMap.getImageList().size());

        if(successUploadCounter == currMap.getImageList().size()) {
            System.out.println("COMPLETELY DONE");
            return true;
        }

        return false;
    }
}
