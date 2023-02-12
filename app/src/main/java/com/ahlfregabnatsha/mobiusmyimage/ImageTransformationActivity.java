package com.ahlfregabnatsha.mobiusmyimage;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.UUID;

public class ImageTransformationActivity extends AppCompatActivity {

    private static final String TAG_IMAGE = "ImageActivity";

    private static final int MY_WRITE_PERMISSION_CODE = 1;

    private ImageView imageView;
    private ProgressBar spinner;
    private Menu my_menu;
    public Bitmap bitmap;
    Dialog dialog;
    ClickSlave clickSlave;
    private Handler mainHandler = new Handler();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_transformation);

        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.my_child_toolbar);
        setSupportActionBar(myChildToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(false);

        //Info dialog
        dialog = new Dialog(this);

        // Progress bar
        spinner =  (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);

        Intent intent = getIntent();
        Uri imageUri = intent.getParcelableExtra(MainActivity.URI_KEY);

        // Display intro message the first time activity is opened.
        if (MainActivity.firstTransformation) {
            Toast.makeText(this, "Draw arrows with your finger!", Toast.LENGTH_SHORT).show();
            MainActivity.firstTransformation = false;
        }

        bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (bitmap != null) {
            bitmap = resizeToScreenWidth(bitmap);
        }
        else {
            Log.e(TAG_IMAGE, "The Bitmap was null after loading it from media storage on device.");
            Toast.makeText(this, "Something went wrong when loading the image", Toast.LENGTH_SHORT).show();
        }

        if (bitmap == null) {
            Log.e(TAG_IMAGE, "The Bitmap was null after resize");
            Toast.makeText(this, "Something went wrong when resizing the image", Toast.LENGTH_SHORT).show();
        }
        Log.e(TAG_IMAGE, "Test");

        clickSlave = new ClickSlave(bitmap);
        imageView = findViewById(R.id.photoImageView);
        imageView.setImageBitmap(bitmap);

        imageView.setOnTouchListener((v, event) -> {
            if (clickSlave.done) {
                return false;
            }

            boolean outside = (event.getX() < 0 || event.getX() > bitmap.getWidth()
                            || event.getY() < 0 || event.getY() > bitmap.getHeight());

            int action = event.getAction();
            ComplexNumber z_new;
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    z_new = new ComplexNumber(
                            clickSlave.x_center((int) event.getX()),
                            clickSlave.y_center((int) event.getY()) );

                    clickSlave.addPoint(z_new);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (outside) {
                        return false;
                    }
                    z_new = new ComplexNumber(
                            clickSlave.x_center((int) event.getX()),
                            clickSlave.y_center((int) event.getY()) );
                    imageView.setImageBitmap(clickSlave.drawTempLine(
                            clickSlave.points[clickSlave.points_picked-1], z_new
                            )
                    );
                    break;
                case MotionEvent.ACTION_UP:
                    if (outside) {
                        z_new = clickSlave.z_new;
                    }
                    else {
                        z_new = new ComplexNumber(
                                clickSlave.x_center((int) event.getX()),
                                clickSlave.y_center((int) event.getY()));
                    }

                    clickSlave.addPoint(z_new);
                    imageView.setImageBitmap(clickSlave.drawPermLine(
                            clickSlave.points[clickSlave.points_picked-2],
                            clickSlave.points[clickSlave.points_picked-1])
                    );
                    my_menu.findItem(R.id.action_undo).setVisible(true);

                    if (clickSlave.allPointsChosen()) {

                        spinner.setVisibility(View.VISIBLE);
                        spinner.bringToFront();

                        TransformRunnable transformRunnable = new TransformRunnable(clickSlave);
                        new Thread(transformRunnable).start();

                    }
                    break;
                default:
                    // Do nothing.
                    return true;
            }
            return true;
        });
    }

    //Rescale to functional size for transformation.
    public Bitmap resizeToScreenWidth(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Log.v(TAG_IMAGE, String.format("Image width is %d", width));
        Log.v(TAG_IMAGE, String.format("Image height is height %d", height));

        int maxHeight = getScreenHeight() - getActionBarHeight();
        int maxWidth = getScreenWidth();
        float maxRatio = (float) maxHeight / (float) maxWidth;
        float bitmapRatio = (float) height / (float) width;

        float scaleFactor;
        if (bitmapRatio <= maxRatio) {
            scaleFactor = (float) maxWidth / (float) width;
        }
        else {
            scaleFactor = (float) maxHeight / (float) height;
        }

        // If no rescaling is required then the original bitmap is returned.
        if (scaleFactor == 1.) return bitmap;

        // Use Matrix for rescaling.
        Matrix matrix = new Matrix();

        // Rescale height and width equally.
        matrix.postScale(scaleFactor, scaleFactor);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);

        Log.v(TAG_IMAGE, String.format("Resized image width is %d", resizedBitmap.getWidth()));
        Log.v(TAG_IMAGE, String.format("Resized image height is height %d", resizedBitmap.getHeight()));

        bitmap.recycle();
        return resizedBitmap;

    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public int getActionBarHeight() {
        TypedValue tv = new TypedValue();
        if (this.getTheme().resolveAttribute(R.attr.actionBarSize, tv, true))
        {
            return TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }
        else {
            Log.w(TAG_IMAGE, "Something went wrong while retrieving the action bar height. Using 0 as default");
            return 0;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar, menu);
        // Hide the save action
        my_menu = menu;

        menu.findItem(R.id.action_undo).setVisible(false);
        menu.findItem(R.id.action_saveImage).setVisible(false);
        menu.findItem(R.id.action_reverse).setVisible(false);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_showInfo) {
            // User chose the "Settings" item, show the app settings UI...
            openInfoDialog(dialog);
        }
        else if (item.getItemId() == R.id.action_saveImage) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
                    ContextCompat.checkSelfPermission(ImageTransformationActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

                // Requesting the permission
                ActivityCompat.requestPermissions(ImageTransformationActivity.this,
                        new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                        MY_WRITE_PERMISSION_CODE);
            }
            //error here!
            else if (savePhotoToExternalStorage(UUID.randomUUID().toString(), this.bitmap)){
                //Permission already granted and we just saved
                Toast.makeText(this, "Image saved", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
        else if (item.getItemId() ==  R.id.action_reverse) {
            this.bitmap = clickSlave.original;
            imageView.setImageBitmap(this.bitmap);
            this.clickSlave = new ClickSlave(this.bitmap);
            my_menu.findItem(R.id.action_reverse).setVisible(false);
            my_menu.findItem(R.id.action_saveImage).setVisible(false);
        }
        else if (item.getItemId() == R.id.action_undo) {
            imageView.setImageBitmap(clickSlave.getPrevious());
            if (!clickSlave.undo()) {
                my_menu.findItem(R.id.action_undo).setVisible(false);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void openInfoDialog(Dialog dialog) {
        dialog.setContentView(R.layout.dialog_info_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button btn_ok = dialog.findViewById(R.id.button_dialog);
        btn_ok.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    //Saving bitmap
    private boolean savePhotoToExternalStorage(String displayName, Bitmap bitmap) {
        Uri imageCollection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        }
        else {
            imageCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        ContentResolver resolver = getContentResolver();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, displayName + ".jpg");
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        contentValues.put(MediaStore.Images.Media.WIDTH, bitmap.getWidth());
        contentValues.put(MediaStore.Images.Media.HEIGHT, bitmap.getHeight());

        try {
            //assert non null maybe
            Uri imageUri = resolver.insert(imageCollection, contentValues);
            OutputStream outputStream = resolver.openOutputStream(Objects.requireNonNull(imageUri));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            Objects.requireNonNull(outputStream);
            return true;
        }
        catch (IOException e) {
            Toast.makeText(this, "Image save failed" + e, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return false;
    }

    class TransformRunnable implements Runnable {
        ClickSlave clickSlave;

        TransformRunnable(ClickSlave clickSlave) {
            this.clickSlave = clickSlave;
        }

        @Override
        public void run() {
            Bitmap runnableBitmap = clickSlave.transform();
            mainHandler.post(() -> {
                imageView.setImageBitmap(runnableBitmap);
                spinner.setVisibility(View.GONE);

                my_menu.findItem(R.id.action_saveImage).setVisible(true);
                my_menu.findItem(R.id.action_reverse).setVisible(true);
                my_menu.findItem(R.id.action_undo).setVisible(false);
            });
        }
    }

}
