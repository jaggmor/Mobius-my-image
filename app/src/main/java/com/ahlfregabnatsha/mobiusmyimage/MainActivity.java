package com.ahlfregabnatsha.mobiusmyimage;

import androidx.activity.result.ActivityResultCallback;
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
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    // Defining my codes for camera
    private static final int MY_CAMERA_PERMISSION_CODE = 1;
    private static final int MY_FILE_PERMISSION_CODE = 2;
    public static final String URI_KEY = "com.ahlfregabnatsha.KEY";

    // Declaring ImageButtons (!=Button)
    private ImageButton camera, folder;

    //
    ActivityResultLauncher<Uri> activityResultLauncherPhoto;
    ActivityResultLauncher<String> mGetContent;

    File photoFile;
    Uri photoUri;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        dialog = new Dialog(this);

        camera = findViewById(R.id.btn_camera);
        folder = findViewById(R.id.btn_folder);

        File file = new File(getFilesDir(), "picFromCamera");
        Uri uri = FileProvider.getUriForFile(this,
                getApplicationContext().getPackageName() + ".provider", file);

        //Requires pathing in the file provider
        activityResultLauncherPhoto = registerForActivityResult(new ActivityResultContracts.TakePicture(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        beginImageTransformation(uri);
                        // do what you need with the uri here ...
                    }
                    //public void onActivityResult(ActivityResult result) {
                        //if (result.getResultCode() == RESULT_OK && result.getData() != null){
                        //    //Toast.makeText(MainActivity.this, uri.getPath(), Toast.LENGTH_SHORT) .show();
                        //    Bundle bundle = result.getData().getExtras();
                        //    Bitmap bitmap = (Bitmap) bundle.get("data");

                            //beginImageTransformation(uri);
                        //}
                });

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        Toast.makeText(MainActivity.this, uri.getPath(), Toast.LENGTH_SHORT) .show();
                        beginImageTransformation(uri);
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
                    //Toast.makeText(MainActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();

                    activityResultLauncherPhoto.launch(uri);
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
                    //Toast.makeText(MainActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
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
                //Toast.makeText(MainActivity.this, "Camera Permission Granted", Toast.LENGTH_SHORT) .show();
            }
            else {
                Toast.makeText(MainActivity.this, "Camera Permission Denied", Toast.LENGTH_SHORT) .show();
            }
        }
        else if (requestCode == MY_FILE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(MainActivity.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
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