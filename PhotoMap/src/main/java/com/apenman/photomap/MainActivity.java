package com.apenman.photomap;

import java.lang.reflect.Type;
import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.content.Intent;
import java.util.Date;

import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements
        OnClickListener {

    // Widget GUI
    Button btnCalendar, btnTimePicker, btnGetList;
    EditText txtDate, txtTime;
    ListView listView;

    private static ArrayList image_list = new ArrayList();
    public static ImageData selectedImage;
    public static int selectedIndex = 0;
    private Cursor cursor;
    private int imageColumnIndex;

    // Variable for storing current date and time
    private int mYear, mMonth, mDay, mHour, mMinute;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCalendar = (Button) findViewById(R.id.btnCalendar);
        btnTimePicker = (Button) findViewById(R.id.btnTimePicker);
        btnGetList = (Button) findViewById(R.id.btnGetList);

        txtDate = (EditText) findViewById(R.id.txtDate);
        txtTime = (EditText) findViewById(R.id.txtTime);

        listView = (ListView) findViewById(R.id.listview);

        btnCalendar.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);
        btnGetList.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v == btnCalendar) {

            // Process to get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            // Launch Date Picker Dialog
            DatePickerDialog dpd = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            // Display Selected date in textbox
                            txtDate.setText(dayOfMonth + "-"
                                    + (monthOfYear + 1) + "-" + year);

                        }
                    }, mYear, mMonth, mDay);
            dpd.show();
        }

        if (v == btnTimePicker) {

            // Process to get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog tpd = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            // Display Selected time in textbox
                            txtTime.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, false);
            tpd.show();
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
//        cursor = getContentResolver().query(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, as, null, null, null);
        Date date = new Date();
        /* FILTER ON BOTH DATES FROM DATE PICKER. SEE STACK OVERFLOW BOOKMARK FOR EXAMPLE */
        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, android.provider.MediaStore.MediaColumns.DATE_ADDED + "<?",
                new String[]{"" + date},
                android.provider.MediaStore.MediaColumns.DATE_ADDED + " DESC");
        imageColumnIndex = cursor.getColumnIndexOrThrow("_data");
        cursor.moveToPosition(0);
        String s;
        for (; cursor.moveToNext(); image_list.add(new ImageData(s)))
        {
            s = cursor.getString(imageColumnIndex);
        }

        if(image_list.size() > 0) {
            selectedImage = (ImageData)image_list.get(0);
            System.out.println("GOT IMAGE " + selectedImage.imagePath);
        }
    }

    private void startMapActivity() {
        getImageList();
        Intent intent = new Intent(getBaseContext(), DisplayActivity.class);
        intent.putExtra("IMAGE_LIST", image_list);
        startActivity(intent);
    }

    private void getSavedLists(ListView listView) {
        ImageMap[] currMapList;
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        String json2 = prefs.getString("json", null);

        if(json2 != null) {
//            System.out.println("*%*%*%*%*%*%*");
//            System.out.println(json2);

            Type listType = new TypeToken<List<ImageMap>>() {}.getType();
            List<ImageMap> list = new Gson().fromJson(json2, listType);

            if (list.size() > 0) {
//                System.out.println("THERE IS STUFF");
//                System.out.println("SIZE IS: " + list.size());
                currMapList = new ImageMap[list.size()];
                for (int i = 0; i < list.size(); i++) {
//                    System.out.println("CHECKING " + i);
                    ImageMap map = list.get(i);
                    if (map != null) {
//                        System.out.println("FOUND ONE AT " + Integer.toString(i));
                        currMapList[i] = map;
                    }
                    else {
//                        System.out.println("HIT NULL AT " + Integer.toString(i));
                    }
                }

            } else {
                currMapList = new ImageMap[0];
//                System.out.println("THERE IS NOTHING");
            }
        }
        else {
//            System.out.println("NULL, SET TO 0");
            currMapList = new ImageMap[0];
        }

        ArrayAdapter<ImageMap> adapter = new ArrayAdapter<ImageMap>(this,
                android.R.layout.simple_list_item_1, currMapList);
        listView.setAdapter(adapter);
    }

    private void saveListToPrefs(ImageData[] list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("json", json);
        editor.commit();
    }
}
