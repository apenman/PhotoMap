package com.apenman.photomap;

import java.io.Serializable;
import android.media.ExifInterface;
import com.google.android.gms.maps.model.LatLng;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Random;

/**
 * Created by apenman on 1/23/15.
 */

public class ImageData implements Serializable {
    String imagePath;
    Double lat, lng;

    public ImageData(String path) {
        imagePath = path;
        /* SET THESE TO ZERO FOR NOW */
        lat = 0.0;
        lng = 0.0;
        try {
            /*********** PRODUCING NULL RESULTS WE NEED TO FIX ************/
            ExifInterface exif = new ExifInterface(path);
            if(exif != null) {
                Random random = new Random();
//                float af[] = {
//                        -100F + 200F * random.nextFloat(), -100F + 200F * random.nextFloat()
//                };
//                exif.getLatLong(af);
//                float f = af[0];
//                float f1 = af[1];
//                latLng = new LatLng(0.0, 0.0);
//
//                String LATITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
//                String LATITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
//                String LONGITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
//                String LONGITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
//
//                System.out.println("LATITUDE = " + LATITUDE);
//                System.out.println("LONGITUDE = " + LONGITUDE);
                String dateString = exif.getAttribute(ExifInterface.TAG_DATETIME);
                System.out.println("DATE: " + dateString);
            }else
            {
                System.out.println("WAS NULL");
//                latLng = new LatLng(0.0D, 0.0D);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}