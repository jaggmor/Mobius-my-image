package com.ahlfregabnatsha.mobiusmyimage;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
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
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

public class ImageTransformationActivity extends AppCompatActivity {

    private ImageView imageView;
    //private Button btn_transform;
    public Bitmap bitmap;
    public int bitmapWidth;
    public int bitmapHeight;

    private ComplexNumber z1;
    private ComplexNumber w1;

    private ComplexNumber z2;
    private ComplexNumber w2;

    private ComplexNumber z3;
    private ComplexNumber w3;

    private ComplexNumber[] complexNumberPoints = new ComplexNumber[6];

    int points_picked = 0;

    Dialog dialog;

    @SuppressLint("ClickableViewAccessibility")
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

        this.bitmapWidth = bitmap.getWidth();
        this.bitmapHeight = bitmap.getHeight();

        Toast.makeText(ImageTransformationActivity.this,
                Integer.toString(bitmapWidth), Toast.LENGTH_SHORT).show();
        Toast.makeText(ImageTransformationActivity.this,
                Integer.toString(bitmapHeight), Toast.LENGTH_SHORT).show();


        imageView = findViewById(R.id.photoImageView);
        imageView.setImageBitmap(bitmap);
        
        
        imageView.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN && points_picked < 6){
                    complexNumberPoints[points_picked] = new ComplexNumber(
                            x_center((int) event.getX()), y_center((int) event.getY()));
                    Toast.makeText(ImageTransformationActivity.this,
                            complexNumberPoints[points_picked].toString(), Toast.LENGTH_SHORT) .show();
                    points_picked++;
                    return true;
                }
                else if (points_picked == 6){
                    z1 = complexNumberPoints[0];
                    w1 = complexNumberPoints[1];
                    z2 = complexNumberPoints[2];
                    w2 = complexNumberPoints[3];
                    z3 = complexNumberPoints[4];
                    w3 = complexNumberPoints[5];
                    MobiusTransformation mt = new MobiusTransformation(z1, z2, z3, w1, w2, w3);

                    bitmap = transformBitmap(bitmap, mt);
                    imageView.setImageBitmap(bitmap);
                    points_picked++;
                    return false;
                }
                else {
                    return false;
                }

            }
        });
        
        

        //btn_transform = findViewById(R.id.btn_transform);
        
         /*
        btn_transform.setOnClickListener(new View.OnClickListener() {
            //When user clicks TRANSFORM

            @Override
            public void onClick(View v) {
                //Implement onTouch listener here.
                z1 = new ComplexNumber(-100, 0);
                w1 = new ComplexNumber(0, 100);

                z2 = new ComplexNumber(0, 100);
                w2 = new ComplexNumber(120, 0);

                z3 = new ComplexNumber(100, 0);
                w3 = new ComplexNumber(0, -100);


                //Transformation that will take us between bitmaps.
                MobiusTransformation mt = new MobiusTransformation(z1, z2, z3, w1, w2, w3);

                bitmap = transformBitmap(bitmap, mt);
                imageView.setImageBitmap(bitmap);

                //test rotation and zoom transformation
                
                imageView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        int action = event.getAction();
                        if (action == MotionEvent.ACTION_DOWN && points_picked < 6){
                            complexNumberPoints[points_picked] = new ComplexNumber(
                                    x_center((int) event.getX()), y_center((int) event.getY()));
                            Toast.makeText(ImageTransformationActivity.this,
                                    complexNumberPoints[points_picked].toString(), Toast.LENGTH_SHORT) .show();
                            points_picked++;
                            return true;
                        }
                        else if (points_picked == 6){
                            z1 = complexNumberPoints[0];
                            w1 = complexNumberPoints[1];
                            z2 = complexNumberPoints[2];
                            w2 = complexNumberPoints[3];
                            z3 = complexNumberPoints[4];
                            w3 = complexNumberPoints[5];
                            MobiusTransformation mt = new MobiusTransformation(z1, z2, z3, w1, w2, w3);

                            bitmap = transformBitmap(bitmap, mt);
                            imageView.setImageBitmap(bitmap);
                            return true;
                        }
                        else {
                            return false;
                        }

                    }
                });

            }
        });
        */
    }

    //Returns the transformed bitmap.
    private Bitmap transformBitmap(Bitmap bitmap, MobiusTransformation mt) {
        Bitmap transformedBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);

        ComplexNumber imageCoordinates, domainCoordinates;
        for (int x = 0; x < bitmapWidth; x++) {
            for (int y = 0; y < bitmapHeight; y++) {
                //Adjust to coordinates with origin in center of picture.
                //imageCoordinates = new ComplexNumber(x - bitmapWidth / 2, bitmapHeight / 2 - y);
                imageCoordinates = new ComplexNumber(x_center(x), y_center(y));
                domainCoordinates = mt.transformInverse(imageCoordinates);
                if (Math.round(domainCoordinates.getReal()) < bitmapWidth/2 && Math.round(domainCoordinates.getReal()) > -bitmapWidth/2 &&
                        Math.round(domainCoordinates.getImaginary()) < bitmapHeight / 2 && Math.round(domainCoordinates.getImaginary()) > -bitmapHeight/2) {

                    //interpolation method, nearest neighbor.
                    //int xx = (int) Math.round(domainCoordinates.getReal()) + bitmapWidth / 2;
                    //int yy = -(int) Math.round(domainCoordinates.getImaginary()) + bitmapHeight / 2;
                    int xx = x_normal((int) Math.round(domainCoordinates.getReal()));
                    int yy = y_normal((int) Math.round(domainCoordinates.getImaginary()));
                    int newPixel = bitmap.getPixel(xx, yy);

                    transformedBitmap.setPixel(x, y, newPixel);
                } else {
                    transformedBitmap.setPixel(x, y, Color.BLACK);
                }
            }
        }

        bitmap.recycle();   //Save memory
        return transformedBitmap;
    }

    private int x_center(int x) {
        return x - (bitmapWidth/2);
    }

    private int y_center(int y) {
        return (bitmapHeight/2) - y;
    }

    private int x_normal(int x_c) {
        return x_c + (bitmapWidth/2);
    }

    private int y_normal(int y_c) {
        return (bitmapHeight/2) - y_c;
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
