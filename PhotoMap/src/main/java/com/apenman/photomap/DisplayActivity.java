package com.apenman.photomap;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by apenman on 1/23/15.
 */
public class DisplayActivity extends FragmentActivity implements MapNameDialog.MapNameDialogListener {
    TextView text;
    Button nextButton, prevButton, saveButton, removeButton;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        setGlobalImages();

        text = (TextView) findViewById(R.id.test);
        nextButton = (Button) findViewById(R.id.nextButton);
        prevButton = (Button) findViewById(R.id.prevButton);
        saveButton = (Button) findViewById(R.id.saveButton);
        removeButton = (Button) findViewById(R.id.removeButton);

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

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("REMOVING");
                showRemoveDialog();
            }
        });

        if(GlobalList.getGlobalInstance().getCurrMap().getImageList().size() > 0) {
            text.setText(GlobalList.getGlobalInstance().getCurrImage().getImagePath());
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
        text.setText(GlobalList.getGlobalInstance().getCurrImage().getImagePath());
    }


    public void setImage() {
        if(GlobalList.getGlobalInstance().getCurrImage() != null) {

            ImageView imageView = ((ImageView) findViewById(R.id.imageView));
            if (imageView != null) {
                imageView.setImageURI(Uri.parse(GlobalList.getGlobalInstance().getCurrImage().getImagePath()));
            }
        }
    }


    /* Add the current Map to the end of Global List. Then write to shared prefs */
    private void saveMapToPrefs() {
        List<ImageMap> currMapList = GlobalList.getGlobalInstance().getCurrMapList();
        currMapList.add(GlobalList.getGlobalInstance().getCurrMap());

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

    private void showRemoveDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle("Remove Image");

        // set dialog message
        alertDialogBuilder
                .setMessage("Are You Sure You Want To Remove Image?")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        GlobalList.getGlobalInstance().removeImage();
                        updateText();
                        setImage();
                        dialog.cancel();
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    /* This is called when 'OK' is pressed on AlertDialog when saving map */
    @Override
    public void onFinishMapDialog(String mapName, String mapDescription) {
        /* Check empty string is not working currently */
        if(mapName != null && mapName != " ") {
            GlobalList.getGlobalInstance().getCurrMap().setName(mapName);
        }
        else {
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            GlobalList.getGlobalInstance().getCurrMap().setName(currentDateTimeString);
        }

        if(mapDescription != null && mapDescription != " ") {
            GlobalList.getGlobalInstance().getCurrMap().setDescription(mapDescription);
        }
        else {
            GlobalList.getGlobalInstance().getCurrMap().setDescription(" ");
        }

        saveMapToPrefs();
    }

    private void setGlobalImages() {
        ImageMap currMap = GlobalList.getGlobalInstance().getCurrMap();
        if(!currMap.isEmpty()) {
            GlobalList.getGlobalInstance().setCurrImage(currMap.getImageList().get(0));
            GlobalList.getGlobalInstance().setCurrImageIndex(0);
        }
    }
}