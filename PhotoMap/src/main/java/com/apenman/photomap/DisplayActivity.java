package com.apenman.photomap;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by apenman on 1/23/15.
 */
public class DisplayActivity extends Activity {
    TextView text;
    private static ArrayList image_list = new ArrayList();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        image_list = getIntent().getStringArrayListExtra("IMAGE_LIST");

        text = (TextView) findViewById(R.id.test);
        if(image_list.size() > 0) {
            text.setText(((ImageData)image_list.get(0)).imagePath);
        } else {
           text.setText("NONE");
        }
    }
}
