package com.ahlfregabnatsha.mobiusmyimage;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    // Defining my codes for camera
    private static final int MY_CAMERA_PERMISSION_CODE = 1;
    private static final int MY_FILE_PERMISSION_CODE = 2;

    // Data to be sent to ImageTransformationActivity
    public static final String URI_KEY = "com.ahlfregabnatsha.KEY";

    //
    ActivityResultLauncher<Uri> activityResultLauncherPhoto;
    ActivityResultLauncher<String> mGetContent;

    //dialogInfo is the information displayed when pressing the info button in the toolbar.
    Dialog dialogInfo;

    // Keeping track of the first time the user opens ImageTransformationActivity.
    // If so, then a toast will be shown.
    public static boolean firstTransformation = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        dialogInfo = new Dialog(this);

        // Declaring ImageButtons (!=Button)
        ImageButton camera = findViewById(R.id.btn_camera);
        ImageButton folder = findViewById(R.id.btn_folder);

        File file = new File(getFilesDir(), "picFromCamera");
        Uri uri = FileProvider.getUriForFile(this,
                getApplicationContext().getPackageName() + ".provider", file);

        //Requires pathing in the file provider
        activityResultLauncherPhoto = registerForActivityResult(new ActivityResultContracts.TakePicture(),
                result -> {
                if (result) beginImageTransformation(uri);
                });

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                this::beginImageTransformation);

        camera.setOnClickListener(v -> {
            //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {

                // Requesting the permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[] { Manifest.permission.CAMERA }, MY_CAMERA_PERMISSION_CODE);
            }
            else {
                //Permission already granted
                activityResultLauncherPhoto.launch(uri);
            }
        });



        folder.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

                // Requesting the permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, MY_FILE_PERMISSION_CODE);
            }
            else {
                //Permission already granted
                mGetContent.launch("image/*");
            }
        });
    }

    // This function is called when the user accepts or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when the user is prompt for permission.
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Camera Permission Granted", Toast.LENGTH_SHORT) .show();
            }
            else {
                Toast.makeText(MainActivity.this, "Camera Permission Denied", Toast.LENGTH_SHORT) .show();
            }
        }
        else if (requestCode == MY_FILE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void beginImageTransformation(Uri uri) {
        Intent intent = new Intent(this, ImageTransformationActivity.class);
        intent.putExtra(URI_KEY, uri);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar, menu);
        // Hide the save action
        menu.findItem(R.id.action_undo).setVisible(false);
        menu.findItem(R.id.action_saveImage).setVisible(false);
        menu.findItem(R.id.action_reverse).setVisible(false);


        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_showInfo:
                openInfoDialog();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void openInfoDialog() {
        dialogInfo.setContentView(R.layout.dialog_info_layout);
        dialogInfo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button btn_ok = dialogInfo.findViewById(R.id.button_dialog);
        btn_ok.setOnClickListener(v -> dialogInfo.dismiss());
        dialogInfo.show();
    }


}