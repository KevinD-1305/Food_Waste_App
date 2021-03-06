package com.example.foodwasteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;
import javax.xml.transform.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BarcodeScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int Request_camera = 1;
    private ZXingScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(checkPermission())
            {
                Toast.makeText(BarcodeScanner.this, "Permission Granted", Toast.LENGTH_LONG).show();
            }
            else {
                requestPermission();
            }
        }
    }

    private boolean checkPermission()
    {
        return (ContextCompat.checkSelfPermission(BarcodeScanner.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }
    private void requestPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA_SERVICE}, Request_camera);
    }
    public void onRequestPermissionsResult(int requestCode, String permission[], int grantResults[]) {
        switch(requestCode)
        {
            case Request_camera :
                if (grantResults.length > 0)
                {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted)
                    {
                        Toast.makeText(BarcodeScanner.this, "Permission Granted", Toast.LENGTH_LONG).show();
                    }
                    else
                        {
                            Toast.makeText(BarcodeScanner.this, "Permission Denied", Toast.LENGTH_LONG).show();
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                            {
                                if(shouldShowRequestPermissionRationale(CAMERA_SERVICE))
                                {
                                    displayAlertMessage("You need to allow access for permissions", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            requestPermissions(new String[]{CAMERA_SERVICE}, Request_camera);
                                        }
                                    });
                                    return;
                                }
                            }
                        }
                    }
                    break;
            }
    }
    @Override
    public void onResume()
    {
        super.onResume();
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                if(checkPermission())
                {
                    if(scannerView == null)
                    {
                        scannerView = new ZXingScannerView(this);
                        setContentView(scannerView);
                    }
                    scannerView.setResultHandler(this);
                    scannerView.startCamera();
                }
                else
                {
                    requestPermission();
                }
            }
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        scannerView.stopCamera();
    }
    public void displayAlertMessage(String message, DialogInterface.OnClickListener listener)
    {
        new AlertDialog.Builder(BarcodeScanner.this)
                .setMessage(message)
                .setPositiveButton("OK", listener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    @Override
    public void handleResult(com.google.zxing.Result result)
    { final String scanResult = result.getText();
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Scan Result");
    builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
    {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            scannerView.resumeCameraPreview(BarcodeScanner.this);
        }
    });
    builder.setNeutralButton("Visit", new DialogInterface.OnClickListener()
    {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(scanResult));
            startActivity(intent);
        }
    });
    builder.setMessage(scanResult);
    AlertDialog alert = builder.create();
    alert.show();
    }
}