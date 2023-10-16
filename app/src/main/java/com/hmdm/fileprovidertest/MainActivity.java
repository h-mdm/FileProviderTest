package com.hmdm.fileprovidertest;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST = 1000;
    private static final String FILE_PATH = "/storage/emulated/0/sample.jpg";
    private static final String FILE_URL = "file://" + FILE_PATH;
    private static final String LOG_TAG = "FileProviderTest";

    private ImageView imageView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.action);
        button.setOnClickListener(view -> openFile());
        imageView = findViewById(R.id.image);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(this)
                        .setMessage("You need to grant external storage permissions")
                        .setPositiveButton("OK", (dialogInterface, i) -> {
                        });
            } else {
                requestPermissions(new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, PERMISSIONS_REQUEST);
            }
        } else {
            File imgFile = new File(FILE_PATH);
            if(imgFile.exists()){
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imageView.setImageBitmap(bitmap);
            } else {
                Toast.makeText(this, "File does not exist!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void openFile() {
        Intent i = new Intent(Intent.ACTION_VIEW);

        Uri uri = Uri.parse(FILE_URL);

        String path = uri.getPath();
        File file = new File(path);
        try {
            uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
        } catch (/*IllegalArgument*/Exception e) {
            Toast.makeText(this, "Can't get URI for file: " + file.getPath(), Toast.LENGTH_LONG).show();
            return;
        }
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Log.d(LOG_TAG, "Opening Content Provider URI: " + uri.toString());
        i.setData(uri);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        try {
            startActivity(i);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Failed to find activity for VIEW intent!", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}