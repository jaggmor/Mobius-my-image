package com.ahlfregabnatsha.mobiusmyimage;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // Defining my codes for camera
    private static final int MY_CAMERA_PERMISSION_CODE = 1;
    private static final int MY_FILE_PERMISSION_CODE = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 3;

    // Declaring ImageButtons (!=Button)
    private ImageButton camera, folder;

    //
    ActivityResultLauncher<Intent> activityResultLauncherPhoto;
    ActivityResultLauncher<String> mGetContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        camera = findViewById(R.id.btn_camera);
        folder = findViewById(R.id.btn_folder);

        activityResultLauncherPhoto = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null){
                            Uri uri = result.getData().getData();


                            //Bundle bundle = result.getData().getExtras();
                            //Bitmap bitmap = (Bitmap) bundle.get("data");
                        }
                    }
                });

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        Toast.makeText(MainActivity.this, uri.getPath(), Toast.LENGTH_SHORT) .show();
                    }
                });


        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {

                    // Requesting the permission
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[] { Manifest.permission.CAMERA }, MY_CAMERA_PERMISSION_CODE);
                }
                else {
                    Toast.makeText(MainActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
                    activityResultLauncherPhoto.launch(intent);
                }
            }
        });

        folder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

                    // Requesting the permission
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, MY_FILE_PERMISSION_CODE);
                }
                else {
                    Toast.makeText(MainActivity.this,
                            "Permission already granted", Toast.LENGTH_SHORT).show();
                    mGetContent.launch("image/*");
                }
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
}