package com.apenman.photomap;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by apenman on 1/23/15.
 */
public class DisplayActivity extends FragmentActivity implements MapNameDialog.MapNameDialogListener {
    TextView text;
    Button nextButton, prevButton, saveButton;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        text = (TextView) findViewById(R.id.test);
        nextButton = (Button) findViewById(R.id.nextButton);
        prevButton = (Button) findViewById(R.id.prevButton);
        saveButton = (Button) findViewById(R.id.saveButton);

        if(nextButton == null) {
            System.out.println("UHOH");
        } else {
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nextImage();
                    setImage();
                }
            });
        }

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevImage();
                setImage();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("SAVING");
                showEditDialog();
            }
        });

        if(GlobalList.getGlobalInstance().getCurrMap().getImageList().size() > 0) {
            text.setText(GlobalList.getGlobalInstance().getCurrImage().imagePath);
        } else {
            text.setText("NONE");
        }

        setImage();
    }

    public void nextImage() {
        if(GlobalList.getGlobalInstance().setNextImage()) {
            updateText();
        }
    }

    public void prevImage() {
        if(GlobalList.getGlobalInstance().setPrevImage()) {
            updateText();
        }
    }

    public void updateText() {
        text.setText(GlobalList.getGlobalInstance().getCurrImage().imagePath);
    }


    public void setImage() {
        if(GlobalList.getGlobalInstance().getCurrImage() != null) {

            ImageView imageView = ((ImageView) findViewById(R.id.imageView));
            if (imageView != null) {
                imageView.setImageURI(Uri.parse(GlobalList.getGlobalInstance().getCurrImage().imagePath));
            }
        }
    }


    /******** EXAMINE THIS ******/
    private void saveMapToPrefs() {
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
                System.out.println("THERE IS STUFF");
                System.out.println("SIZE IS: " + list.size());
                for (int i = 0; i < list.size(); i++) {
                    ImageMap tempMap = list.get(i);
                    if (tempMap != null) {
                        currMapList.add(tempMap);
                    } else {
                    }
                }
                currMapList.add(GlobalList.getGlobalInstance().getCurrMap());

            } else {
                currMapList.add(GlobalList.getGlobalInstance().getCurrMap());
            }
        } else {
            currMapList.add(GlobalList.getGlobalInstance().getCurrMap());
        }

        Gson gson = new Gson();
        String json = gson.toJson(currMapList);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("test", json);
        editor.commit();
    }

    /* display the edit map name dialog */
    private void showEditDialog(){
        FragmentManager fm = getSupportFragmentManager();
        MapNameDialog mapNameDialog = MapNameDialog.newInstance("Set Map Name");
        mapNameDialog.show(fm, "fragment_map_name");
    }

    /* This is called when 'OK' is pressed on AlertDialog when saving map */
    @Override
    public void onFinishMapDialog(String mapName, String mapDescription) {
        /* Check empty string is not working currently */
        if(mapName != null && mapName != " ") {
            GlobalList.getGlobalInstance().getCurrMap().setName(mapName);
            GlobalList.getGlobalInstance().getCurrMap().setDescription(mapDescription);
        }
        else {
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            GlobalList.getGlobalInstance().getCurrMap().setName(currentDateTimeString);
        }
        saveMapToPrefs();
    }
}