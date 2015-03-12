package com.apenman.photomap;

import java.io.IOException;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.text.ParseException;
import java.util.Calendar;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.ExifInterface;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.content.Intent;
import java.util.Date;
import android.database.Cursor;
import android.widget.AdapterView.OnItemClickListener;

import com.facebook.AppEventsLogger;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.List;


public class MainActivity extends Activity implements OnClickListener {
    // Widget GUI
    Button btnCalendarFrom, btnCalendarTo, btnTimePicker, btnGetList;
    EditText txtDateFrom, txtDateTo, txtTime;
    ListView listView;

    private static List<ImageData> image_list = new ArrayList<ImageData>();
    public static ImageData selectedImage;
    public static int selectedIndex = 0;
    private Cursor cursor;
    private int imageColumnIndex;

    // Variable for storing current date and time
    private int mYearFrom, mMonthFrom, mDayFrom, mHourFrom, mMinuteFrom;
    private int mYearTo, mMonthTo, mDayTo, mHourTo, mMinuteTo;
    private Date dateFrom = new Date();
    private Date dateTo = new Date();
//    private long longFrom = dateFrom.getTime();
//    private long longTo = dateTo.getTime();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCalendarFrom = (Button) findViewById(R.id.btnCalendarFrom);
        btnCalendarTo = (Button) findViewById(R.id.btnCalendarTo);
//        btnTimePicker = (Button) findViewById(R.id.btnTimePicker);
        btnGetList = (Button) findViewById(R.id.btnGetList);

        txtDateFrom = (EditText) findViewById(R.id.txtDateFrom);
        txtDateTo = (EditText) findViewById(R.id.txtDateTo);
        listView = (ListView) findViewById(R.id.listview);

        // Set the date fields to today
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
        String formattedDate = df.format(c.getTime());
        txtDateFrom.setText(formattedDate);
        txtDateTo.setText(formattedDate);

//        txtTime = (EditText) findViewById(R.id.txtTime);

        btnCalendarFrom.setOnClickListener(this);
        btnCalendarTo.setOnClickListener(this);
//        btnTimePicker.setOnClickListener(this);
        btnGetList.setOnClickListener(this);

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view,
                                    int position, long id) {
                /* used to clear the old image list here */
                //                if(image_list.length > 0) {
                //                    image_list.clear();
                //                }

                ImageMap clickedMap = (ImageMap) adapter.getItemAtPosition(position);
                System.out.println("THE MAP CLICKED: " + clickedMap);
                System.out.println("THE MAP CLICKED: " + clickedMap.getImageList());
                /* Set the current map. Current image and index will be set in displayactivity */
                GlobalList.getGlobalInstance().setCurrMap(clickedMap);
                Intent intent = new Intent(getBaseContext(), DisplayActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {

        if (v == btnCalendarFrom) {

            // Process to get Current Date
            final Calendar c = Calendar.getInstance();
            mYearFrom = c.get(Calendar.YEAR);
            mMonthFrom = c.get(Calendar.MONTH);
            mDayFrom = c.get(Calendar.DAY_OF_MONTH);

            // Launch Date Picker Dialog
            DatePickerDialog dpd = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            // Display Selected date in textbox
                            String dateStr = monthOfYear + 1 + "-"
                                    + (dayOfMonth + 1) + "-" + year;
                            txtDateFrom.setText(dateStr);
                            SimpleDateFormat  format = new SimpleDateFormat("MM-dd-yyyy");
                            try {
                                dateFrom = format.parse(dateStr);
                            } catch(ParseException e) {
                                e.printStackTrace();
                            }

                        }
                    }, mYearFrom, mMonthFrom, mDayFrom);
            dpd.show();
        }

        if (v == btnCalendarTo) {

            // Process to get Current Date
            final Calendar c = Calendar.getInstance();
            mYearTo = c.get(Calendar.YEAR);
            mMonthTo = c.get(Calendar.MONTH);
            mDayTo = c.get(Calendar.DAY_OF_MONTH);

            // Launch Date Picker Dialog
            DatePickerDialog dpd = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            // Display Selected date in textbox
                            String dateStr = monthOfYear + 1 + "-"
                                    + (dayOfMonth + 1) + "-" + year;
                            txtDateTo.setText(dateStr);
                            SimpleDateFormat  format = new SimpleDateFormat("MM-dd-yyyy");
                            try {
                                dateTo = format.parse(dateStr);
                            } catch(ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }, mYearTo, mMonthTo, mDayTo);
            dpd.show();
        }

        if (v == btnGetList) {
            // Get list of Images and print
            startMapActivity();
        }
    }

    private void getImageList()
    {
        System.out.println("GETTING LIST");
        String as[] = {
                "_data"
        };
        cursor = getContentResolver().query(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, as, null, null, null);

        imageColumnIndex = cursor.getColumnIndexOrThrow("_data");
        cursor.moveToPosition(0);

        String path, timestamp;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
        Date curDate;
        ExifInterface exif;

        /* clear image list here */
//        if(image_list.size() > 0) {
//            image_list.clear();
//        }
        int index = 0;
        /* can't do this until we can query with right dates.
            the current cursor count is all images
         */
//        image_list = new ImageData[cursor.getCount()];

        ArrayList templist = new ArrayList();

        while(cursor.moveToNext())
        {
            path = cursor.getString(imageColumnIndex);
            try {
                exif = new ExifInterface(path);
                timestamp = exif.getAttribute(ExifInterface.TAG_DATETIME);
                if(timestamp != null) {
                    curDate = sdf.parse(timestamp);
                    if(curDate.after(dateFrom) && dateTo.after(curDate)) {
//                        image_list[index] = new ImageData(path);
                        templist.add(new ImageData(path));
                    }
                }
            } catch(IOException e) {

            } catch(ParseException e) {

            }
            index++;
        }

        // Current image list work around
        for(int i = 0; i < templist.size(); i++) {
            image_list.add((ImageData) templist.get(i));
        }

        /* set the current map to the new map */
        ImageMap map = new ImageMap("", image_list, "");
        GlobalList.getGlobalInstance().setCurrMap(map);

        cursor.close();
    }

    private void startMapActivity() {
        getImageList();
        Intent intent = new Intent(getBaseContext(), DisplayActivity.class);
//        intent.putExtra("IMAGE_LIST", image_list);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
        getSavedLists((ListView) findViewById(R.id.listview));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void getSavedLists(ListView listView) {
        List<ImageMap> currMapList = new ArrayList<ImageMap>();

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        String json2 = prefs.getString("test", null);

        if (json2 != null) {
            System.out.println("*%*%*%*%*%*%*");
            System.out.println(json2);

            Type listType = new TypeToken<List<ImageMap>>() {
            }.getType();
            List<ImageMap> list = new Gson().fromJson(json2, listType);
            if (list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    ImageMap map = list.get(i);
                    if (map != null) {
                        currMapList.add(map);
                    } else {
                    }
                }

            }
        }

        /* Set the global saved map list */
        GlobalList.getGlobalInstance().setCurrMapList(currMapList);

        /* Set the maplist into the array adapter */
        ArrayAdapter<ImageMap> adapter = new ArrayAdapter<ImageMap>(this,
                android.R.layout.simple_list_item_1, currMapList);
        listView.setAdapter(adapter);
    }
}