package com.apenman.photomap;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.net.Uri;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by apenman on 1/23/15.
 */
public class DisplayActivity extends Activity {
    TextView text;
    Button nextButton, prevButton;
    private static ArrayList image_list = new ArrayList();
    static ImageData selectedImage;
    static int selectedIndex = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        image_list = getIntent().getStringArrayListExtra("IMAGE_LIST");

        text = (TextView) findViewById(R.id.test);
        nextButton = (Button) findViewById(R.id.nextButton);
        prevButton = (Button) findViewById(R.id.prevButton);

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
}
