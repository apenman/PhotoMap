package com.apenman.photomap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.*;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.net.Uri;
import com.facebook.*;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import org.json.JSONObject;

import java.security.KeyException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by apenman on 1/23/15.
 */
public class DisplayActivity extends FragmentActivity implements MapNameDialog.MapNameDialogListener, FacebookUploadDialog.FacebookUploadDialogListener {
    TextView text, fileName, fileSize, dateTaken, filePath, gpsCoord;
    Button nextButton, prevButton, saveButton, removeButton, shareButton;
    EditText editDesc;
    GoogleMap map;
    Dialog facebookDialog;
    Display display;
    Point windowSize;
    int screenW;
    int screenH;
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
        clearOldData();
        setGlobalImages();
        createMapView();
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        GlobalList.getGlobalInstance().setContext(getApplicationContext());

        display = getWindowManager().getDefaultDisplay();
        windowSize = new Point();
        display.getSize(windowSize);
        screenW = windowSize.x;
        screenH = windowSize.y;

        System.out.println("W = " + screenW + "    H = " + screenH);


        text = (TextView) findViewById(R.id.test);
        /* Image Info Text Views */
        filePath = (TextView) findViewById(R.id.pathTextView);
        fileName = (TextView) findViewById(R.id.nameTextView);
        fileSize = (TextView) findViewById(R.id.sizeTextView);
        dateTaken = (TextView) findViewById(R.id.dateTextView);
        gpsCoord = (TextView) findViewById(R.id.gpsTextView);
        editDesc = (EditText) findViewById(R.id.editDescText);
        editDesc.setSelected(false);
        editDesc.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (event != null&& (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                    // NOTE: In the author's example, he uses an identifier
                    // called searchBar. If setting this code on your EditText
                    // then use v.getWindowToken() as a reference to your
                    // EditText is passed into this callback as a TextView

                    in.hideSoftInputFromWindow(v.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    // Must return true here to consume event
                    saveDescription();

                    return true;
                }
                return false;
            }
        });

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

//        if(GlobalList.getGlobalInstance().getCurrMap().getImageList().size() > 0) {
//            text.setText(GlobalList.getGlobalInstance().getCurrImage().getImagePath());
//        } else {
//            text.setText("NONE");
//        }
        updateText();
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

    /* Update image info text views */
    public void updateText() {
        String gpsString;
//        text.setText(GlobalList.getGlobalInstance().getCurrImage().getImagePath());
        ImageData currImage = GlobalList.getGlobalInstance().getCurrImage();
        if(currImage != null) {
            if (currImage.getImagePath() != null) {
                filePath.setText(currImage.getImagePath());
            } else {
                filePath.setText("");
            }
            if (currImage.getImageName() != null) {
                fileName.setText(currImage.getImageName());
            } else {
                fileName.setText("");
            }
            fileSize.setText(currImage.getImageSize() + "kb");
            if (currImage.getDateTaken() != null) {
                dateTaken.setText(currImage.getDateTaken());
            } else {
                dateTaken.setText("");
            }
            if (currImage.getLat() == null || currImage.getLng() == null) {
                gpsString = "N/A";
            } else {
                gpsString = "(" + currImage.getLat() + ", " + currImage.getLng() + ")";
            }
            gpsCoord.setText(gpsString);

            if(currImage.getDescription() != null) {
                editDesc.setText(currImage.getDescription());
            }
            else {
                editDesc.setText("");
                editDesc.setHint("Enter description for image.");
            }
        }
    }


    public void setImage() {
        if(GlobalList.getGlobalInstance().getCurrImage() != null) {
            ImageView imageView = ((ImageView) findViewById(R.id.imageView));
            if (imageView != null) {
                int newWidth, newHeight;
                /* WE HAVE TO RESIZE THE IMAGE BITMAP BEFORE SETTING.
                    If set straight from imagepath uri, bitmap will be 4x as big.
                    Get original bitmap -> resize to smaller to fit screen -> set to imageview

                    Don't forget to recycle original bitmap for garbage collection
                 */
                /* http://stackoverflow.com/questions/12250300/android-image-view-out-of-memory-error */
                Bitmap bitmapOriginal = BitmapFactory.decodeFile(GlobalList.getGlobalInstance().getCurrImage().getImagePath());
                if(bitmapOriginal.getWidth() > (screenW / 3)) {
                    newWidth = screenW / 3;
                }
                else {
                    newWidth = bitmapOriginal.getWidth();
                }
                if(bitmapOriginal.getHeight() > (screenH / 3)) {
                    newHeight = screenH / 3;
                }
                else {
                    newHeight = bitmapOriginal.getHeight();
                }
                Bitmap bitmapsimplesize = Bitmap.createScaledBitmap(bitmapOriginal, newWidth, newHeight, true);
                bitmapOriginal.recycle();
                imageView.setImageBitmap(bitmapsimplesize);
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
        createAlbum(titleText, descText);
    }

    private void setGlobalImages() {
        ImageMap currMap = GlobalList.getGlobalInstance().getCurrMap();
        if(!currMap.isEmpty()) {
            GlobalList.getGlobalInstance().setCurrImage(currMap.getImageList().get(0));
            GlobalList.getGlobalInstance().setCurrImageIndex(0);
        }
        else {
            GlobalList.getGlobalInstance().setCurrImage(null);
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
            Marker newMark = null;
            if(img.getLat() != null && img.getLng() != null) {
                newMark = GlobalList.getGlobalInstance().getMap().addMarker(new MarkerOptions().position(new LatLng(img.getLat(), img.getLng())));
            }
            markerList.add(newMark);
        }

        GlobalList.getGlobalInstance().setMarkerList(markerList);
        System.out.println("MARKER LIST SIZE IS: " + markerList.size());

        for(int i = 0; i < list.size(); i++) {
            if(markerList.get(i) != null) {
                GlobalList.getGlobalInstance().getMap().animateCamera(CameraUpdateFactory.newLatLng(markerList.get(i).getPosition()));
            }
        }

        if(list.size() > 0 && markerList.get(0) != null) {
            markerList.get(0).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
        GlobalList.getGlobalInstance().setContext(getApplicationContext());
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

    private void createAlbum(String titleText, String descText) {
        Session session = Session.getActiveSession();
        Bundle bundle = new Bundle();
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

        ImageMap map = GlobalList.getGlobalInstance().getCurrMap();
        System.out.println("MAP NAME = " + map.getName());
        System.out.println("MAP DESC = " + map.getDescription());

        if(titleText == "") {
            titleText = "My PhotoMap";
        }
        if(descText == "") {
            descText = "Posted from Alex's cool senior project";
        }

        bundle.putString("name", titleText);
        bundle.putString("message", descText);
        Request request = new com.facebook.Request(session, "me/albums", bundle, HttpMethod.POST, requestCallBack);

        RequestAsyncTask requestAsyncTask = new RequestAsyncTask(request);
        requestAsyncTask.execute();
    }

    private void saveDescription() {
        ImageData currImage = GlobalList.getGlobalInstance().getCurrImage();
        if(currImage != null) {
            GlobalList.getGlobalInstance().getCurrImage().setDescription(editDesc.getText().toString());
        }
        updateText();
    }

    private void clearOldData() {
        GlobalList.getGlobalInstance().clearOldData();
    }
}