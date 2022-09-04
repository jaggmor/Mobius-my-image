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

import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

public class ImageTransformationActivity extends AppCompatActivity {

    private ImageView imageView;
    public Bitmap bitmap;
    private MenuItem saveMenuItem;
    Dialog dialog;
    ClickSlave clickSlave;

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
        else {
            Toast.makeText(this, "Something wrong occurred while loading the image", Toast.LENGTH_SHORT).show();
        }

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

                    if (clickSlave.allPointsChosen()) {
                        imageView.setImageBitmap(clickSlave.transform());
                        saveMenuItem.setVisible(true);
                    }
                    break;
                default:
                    // Do nothing.
                    return true;
            }
            return true;
        });
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
        // Hide the save action
        saveMenuItem = menu.findItem(R.id.action_saveImage);
        saveMenuItem.setVisible(false);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_showInfo:
                // User chose the "Settings" item, show the app settings UI...
                Toast.makeText(this, "Clicked info button", Toast.LENGTH_SHORT).show();
                openInfoDialog(dialog);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void openInfoDialog(Dialog dialog) {
        dialog.setContentView(R.layout.dialog_info_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button btn_ok = dialog.findViewById(R.id.button_dialog);
        btn_ok.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
