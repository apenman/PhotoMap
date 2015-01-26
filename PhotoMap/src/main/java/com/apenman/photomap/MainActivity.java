package com.apenman.photomap;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import android.media.ExifInterface;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.content.Intent;
import java.util.Date;
import android.database.Cursor;
import java.util.ArrayList;
import java.text.SimpleDateFormat;

import com.facebook.AppEventsLogger;

public class MainActivity extends Activity implements
        OnClickListener {

    // Widget GUI
    Button btnCalendarFrom, btnCalendarTo, btnTimePicker, btnGetList;
    EditText txtDateFrom, txtDateTo, txtTime;

    private static ArrayList image_list = new ArrayList();
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

//        if (v == btnTimePicker) {
//
//            // Process to get Current Time
//            final Calendar c = Calendar.getInstance();
//            mHour = c.get(Calendar.HOUR_OF_DAY);
//            mMinute = c.get(Calendar.MINUTE);
//
//            // Launch Time Picker Dialog
//            TimePickerDialog tpd = new TimePickerDialog(this,
//                    new TimePickerDialog.OnTimeSetListener() {
//
//                        @Override
//                        public void onTimeSet(TimePicker view, int hourOfDay,
//                                              int minute) {
//                            // Display Selected time in textbox
//                            txtTime.setText(hourOfDay + ":" + minute);
//                        }
//                    }, mHour, mMinute, false);
//            tpd.show();
//        }

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
//        longFrom = dateFrom.getTime();
//        longTo = dateTo.getTime();
        /* FILTER ON BOTH DATES FROM DATE PICKER. SEE STACK OVERFLOW BOOKMARK FOR EXAMPLE */
//        cursor = getContentResolver().query(
//                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                null, android.provider.MediaStore.Images.Media.DATE_TAKEN + ">? and "
//                        + android.provider.MediaStore.Images.Media.DATE_TAKEN + "<?",
//                new String[] { "" + dateFrom, "" + dateTo},
//                android.provider.MediaStore.MediaColumns.DATE_ADDED + " DESC");
//        cursor = getContentResolver().query(
//                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                null, android.provider.MediaStore.MediaColumns.DATE_ADDED + ">?",
//                new String[]{"" + longFrom},
//                android.provider.MediaStore.MediaColumns.DATE_ADDED + " DESC");
        imageColumnIndex = cursor.getColumnIndexOrThrow("_data");
        cursor.moveToPosition(0);

        String path, timestamp;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
        Date curDate;
        ExifInterface exif;

        if(image_list.size() > 0) {
            image_list.clear();
        }
        while(cursor.moveToNext())
        {
            path = cursor.getString(imageColumnIndex);
            try {
                exif = new ExifInterface(path);
                timestamp = exif.getAttribute(ExifInterface.TAG_DATETIME);
                if(timestamp != null) {
                    curDate = sdf.parse(timestamp);
                    if(curDate.after(dateFrom) && dateTo.after(curDate)) {
                        image_list.add(new ImageData(path));
                    }
                }
            } catch(IOException e) {

            } catch(ParseException e) {

            }
        }

        if(image_list.size() > 0) {
            selectedImage = (ImageData)image_list.get(0);
            System.out.println("GOT IMAGE " + selectedImage.imagePath);
        }
        cursor.close();
    }

    private void startMapActivity() {
        getImageList();
        Intent intent = new Intent(getBaseContext(), DisplayActivity.class);
        intent.putExtra("IMAGE_LIST", image_list);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Logs 'install' and 'app activate' App Events.
//        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
//        AppEventsLogger.deactivateApp(this);
    }
}
