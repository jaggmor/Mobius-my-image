package com.ahlfregabnatsha.mobiusmyimage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;

public class ImageTransformationActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button btn_transform;
    public Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_transformation);

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

}