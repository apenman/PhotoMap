package com.apenman.photomap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.net.Uri;
import com.facebook.*;


import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.model.GraphObject;
import com.facebook.widget.LoginButton;
import com.google.android.gms.maps.*;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by apenman on 1/23/15.
 */
public class DisplayActivity extends FragmentActivity implements MapNameDialog.MapNameDialogListener, FacebookUploadDialog.FacebookUploadDialogListener {
    TextView text;
    Button nextButton, prevButton, saveButton, removeButton, shareButton;
    GoogleMap map;
    Dialog facebookDialog;
    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback =
            new Session.StatusCallback() {
                @Override
                public void call(Session session,
                                 SessionState state, Exception exception) {
                }
            };


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        setGlobalImages();
        createMapView();
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);

        text = (TextView) findViewById(R.id.test);
        nextButton = (Button) findViewById(R.id.nextButton);
        prevButton = (Button) findViewById(R.id.prevButton);
        saveButton = (Button) findViewById(R.id.saveButton);
        removeButton = (Button) findViewById(R.id.removeButton);
        shareButton = (Button) findViewById(R.id.shareButton);

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

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("SHARING");
                showShareDialog();
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

    /* display the facebook upload dialog */
    private void showFacebookUploadDialog(){
        FragmentManager fm = getSupportFragmentManager();
        FacebookUploadDialog facebookDialog = FacebookUploadDialog.newInstance("title", "idk");
        facebookDialog.show(fm, "fragment_map_name");
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

    private void showShareDialog() {
        Session session = Session.getActiveSession();

        if (session != null && session.isOpened()) {
            // if the session is already open,
            // try to show the selection fragment
            System.out.println("HOW DOES THIS HAPPEN???");
            showFacebookUploadDialog();
        } else {
            // otherwise present the splash screen
            // and ask the person to login.
            System.out.println("NO SESSION");
            // custom dialog
            facebookDialog = new Dialog(this);
            facebookDialog.setContentView(R.layout.facebook_login);
            facebookDialog.setTitle("Facebook Login");

            LoginButton login = (LoginButton) facebookDialog.findViewById(R.id.login_button);
            login.setPublishPermissions(Arrays.asList("publish_actions"));
            facebookDialog.show();
        }
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

    @Override
    public void onFinishFacebookDialog(String titleText, String descText) {
        System.out.println("PRESSED OKAY");
//        byte[] data = null;
//        try {
//            ContentResolver cr = this.getContentResolver();
//            InputStream fis = new FileInputStream(GlobalList.getGlobalInstance().getCurrImage().getImagePath());
//            Bitmap bi = BitmapFactory.decodeStream(fis);
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            bi.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//            data = baos.toByteArray();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        Bundle params = new Bundle();
//        params.putString("method", "photos.upload");
//        params.putByteArray("picture", data);
//
//        AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(facebook);
//        mAsyncRunner.request(null, params, "POST", new SampleUploadListener());
//        postPhoto();
        createAlbum();
    }

    private void setGlobalImages() {
    ImageMap currMap = GlobalList.getGlobalInstance().getCurrMap();
    if(!currMap.isEmpty()) {
        GlobalList.getGlobalInstance().setCurrImage(currMap.getImageList().get(0));
        GlobalList.getGlobalInstance().setCurrImageIndex(0);
    }
    }

    private void createMapView() {
        /**
         * Catch the null pointer exception that
         * may be thrown when initialising the map
         */
        try {
            if(null == map){
               map = ((MapFragment) getFragmentManager().findFragmentById(
                        R.id.mapView)).getMap();
               GlobalList.getGlobalInstance().setMap(map);
                /**
                 * If the map is still null after attempted initialisation,
                 * show an error to the user
                 */
                if(null == map) {

                }
            }
        } catch (NullPointerException exception){
        }

        /* Create marker list */
        List<Marker> markerList = new ArrayList<Marker>();
        List<ImageData> list = GlobalList.getGlobalInstance().getCurrMap().getImageList();

        for (ImageData img : list) {
            Marker newMark = GlobalList.getGlobalInstance().getMap().addMarker(new MarkerOptions().position(new LatLng(img.getLat(), img.getLng())));
            markerList.add(newMark);
        }

        GlobalList.getGlobalInstance().setMarkerList(markerList);
        System.out.println("MARKER LIST SIZE IS: " + markerList.size());
        if(list.size() > 0) {
            GlobalList.getGlobalInstance().getMap().animateCamera(CameraUpdateFactory.newLatLng(markerList.get(0).getPosition()));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
        Session session = Session.getActiveSession();

        if(facebookDialog != null && session != null && session.isOpened()) {
            facebookDialog.dismiss();
            showFacebookUploadDialog();
        }
    }

    private void createAlbum() {
        Request.Callback requestCallBack = null;
        try {
            requestCallBack = new Request.Callback() {
                @Override
                public void onCompleted(Response response) {
                    JSONObject graphResponse = response.getGraphObject().getInnerJSONObject();
                    String postId = null;
                    try {
                        postId = graphResponse.getString("id");
                        /* if successful, iterate over map and add each photo to the album */
                        GlobalList.getGlobalInstance().resetUploadCounter();
                        GlobalList.getGlobalInstance().uploadImages(postId, Session.getActiveSession());
                    } catch(Exception e) {
                        System.out.println("RETRIEVING POST ID FAILED");
                    }
                }
            };
        } catch(Exception e) {
            System.out.println("CALLBACK FAILED");
        }
        Session session = Session.getActiveSession();
        Bundle bundle = new Bundle();
        bundle.putString("name", "My Test Album");
        bundle.putString("message", "Working on senior project lol :-)");
        Request request = new com.facebook.Request(session, "me/albums", bundle, HttpMethod.POST, requestCallBack);

        RequestAsyncTask requestAsyncTask = new RequestAsyncTask(request);
        requestAsyncTask.execute();
    }
}