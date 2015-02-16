package com.apenman.photomap;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
    ImageData[] map_list;
    ImageMap map;
    Button nextButton, prevButton, saveButton;
    private static ArrayList image_list = new ArrayList();
    static ImageData selectedImage;
    static int selectedIndex = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        image_list = getIntent().getStringArrayListExtra("IMAGE_LIST");
        map_list = new ImageData[image_list.size()];

        for(int i = 0; i < image_list.size(); i++) {
            map_list[i] = (ImageData) image_list.get(i);
        }

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

        if(image_list.size() > 0) {
            text.setText(((ImageData)image_list.get(0)).imagePath);
            selectedImage = (ImageData)image_list.get(0);
            selectedIndex = 0;
        } else {
            text.setText("NONE");
            selectedImage = null;
        }

        setImage();
    }

    public void nextImage() {
        if (-1 + image_list.size() == selectedIndex)
        {
            selectedIndex = 0;
            selectedImage = (ImageData)image_list.get(selectedIndex);
        } else
        {
            selectedIndex = 1 + selectedIndex;
            selectedImage = (ImageData)image_list.get(selectedIndex);
        }
        updateText();
    }

    public void updateText() {
        text.setText(selectedImage.imagePath);
    }
    public void prevImage() {
        if (selectedIndex == 0)
        {
            selectedIndex = -1 + image_list.size();
            selectedImage = (ImageData)image_list.get(selectedIndex);
        } else
        {
            selectedIndex = -1 + selectedIndex;
            selectedImage = (ImageData)image_list.get(selectedIndex);
        }
        updateText();
    }

    public void setImage() {
        if(selectedImage != null) {
//            File file = new File(selectedImage.imagePath);
//            if (file.exists()) {
//                android.graphics.Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//                ((ImageView) view.findViewById(R.id.imageView)).setImageBitmap(bitmap);
            ImageView imageView = ((ImageView) findViewById(R.id.imageView));
            if(imageView != null) {
                imageView.setImageURI(Uri.parse(selectedImage.imagePath));
            } else {
                System.out.println("OOPS");
            }
//            }
        } else {
            System.out.println("WAS NULL");
        }
    }

    private void saveListToPrefs(ImageMap map) {
        ImageMap[] currMapList;

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
                currMapList = new ImageMap[list.size() + 1];
                for (int i = 0; i < list.size(); i++) {
                    ImageMap tempMap = list.get(i);
                    if (tempMap != null) {
                        currMapList[i] = tempMap;
                    } else {
                    }
                }
                currMapList[currMapList.length - 1] = map;

            } else {
                currMapList = new ImageMap[1];
                currMapList[0] = map;
            }
        } else {
            currMapList = new ImageMap[1];
            currMapList[0] = map;
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
            map = new ImageMap(mapName, map_list, mapDescription);
        }
        else {
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            map = new ImageMap(currentDateTimeString, map_list, mapDescription);
        }
        saveListToPrefs(map);
    }
}