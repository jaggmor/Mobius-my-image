package com.ahlfregabnatsha.mobiusmyimage;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;

public class ImageTransformationActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button btn_transform;
    public Bitmap bitmap;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_transformation);

        Toolbar myChildToolbar =
                (Toolbar) findViewById(R.id.my_child_toolbar);
        setSupportActionBar(myChildToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        //Info dialog
        dialog = new Dialog(this);

        Intent intent = getIntent();
        Uri imageUri = intent.getParcelableExtra(MainActivity.URI_KEY);
        bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bitmap != null) {
            bitmap = resizeToScreenWidth(bitmap);
        }

        imageView = findViewById(R.id.photoImageView);
        imageView.setImageBitmap(bitmap);

        btn_transform = findViewById(R.id.btn_transform);
        btn_transform.setOnClickListener(new View.OnClickListener() {
            //When user clicks TRANSFORM
            @Override
            public void onClick(View v) {
                //Implement onTouch listener here.
                ComplexNumber z1 = new ComplexNumber(-1, 0);
                ComplexNumber w1 = new ComplexNumber(0, 2);

                ComplexNumber z2 = new ComplexNumber(0, 1);
                ComplexNumber w2 = new ComplexNumber(2, 0);

                ComplexNumber z3 = new ComplexNumber(1, 0);
                ComplexNumber w3 = new ComplexNumber(0, -2);

                //Transformation that will take us between bitmaps.
                MobiusTransformation mt = new MobiusTransformation(z1, z2, z3, w1, w2, w3);

                bitmap = transformBitmap(bitmap, mt);
                imageView.setImageBitmap(bitmap);
            }
        });
    }

    //Returns the transformed bitmap.
    private Bitmap transformBitmap(Bitmap bitmap, MobiusTransformation mt) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap transformedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        ComplexNumber imageCoordinates, domainCoordinates;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                //Adjust to coordinates with origo in center of picture.
                imageCoordinates = new ComplexNumber(x - width/2, height/2 - y);
                domainCoordinates = mt.transformInverse(imageCoordinates);
                if (domainCoordinates.getReal()<width/2 && domainCoordinates.getReal()>-width/2 &&
                        domainCoordinates.getImaginary()<height/2 && domainCoordinates.getImaginary()>-height/2) {

                    //interpolation method, nearest neighbor.
                    int xx = (int)Math.round(domainCoordinates.getReal()) + width/2;
                    int yy = -(int)Math.round(domainCoordinates.getImaginary()) + height/2 ;
                    int newPixel = bitmap.getPixel(xx, yy);

                    transformedBitmap.setPixel(x, y, newPixel);
                }
                else {
                    transformedBitmap.setPixel(x, y, Color.BLACK);
                }
            }
        }

        bitmap.recycle();   //Save memory
        return transformedBitmap;
    }

    //https://stackoverflow.com/questions/4837715/how-to-resize-a-bitmap-in-android
    //Rescale to functional size for transformation.
    public Bitmap resizeToScreenWidth(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        //Rescale with respect to screen width.
        float scaleFactor = ((float) getScreenWidth()) / width;

        // Use Matrix for rescaling.
        Matrix matrix = new Matrix();

        // Rescale height and width equally.
        matrix.postScale(scaleFactor, scaleFactor);

        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        bitmap.recycle();
        return resizedBitmap;
    }

    //A bit redundant with this method. We'll see...
    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_showInfo:
                // User chose the "Settings" item, show the app settings UI...
                openInfoDialog();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void openInfoDialog() {
        dialog.setContentView(R.layout.dialog_info_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button btn_ok = dialog.findViewById(R.id.button_dialog);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}