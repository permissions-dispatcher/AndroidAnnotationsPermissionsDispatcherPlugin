package com.github.aleksandermielczarek.androidannotationspermissionsdispatcherpluginexample;

import android.Manifest;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by Aleksander Mielczarek on 15.09.2016.
 */
@EActivity(R.layout.activity_main)
@RuntimePermissions
public class MainActivity extends AppCompatActivity {

    @Click(R.id.permissionButton)
    void askForPermission() {
        MainActivityPermissionsDispatcher.showCameraWithCheck(this);
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    void showCamera() {
        Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
    }

    @OnShowRationale(Manifest.permission.CAMERA)
    protected void showRationaleForSettings(PermissionRequest request) {
        Toast.makeText(this, "OnShowRationale", Toast.LENGTH_SHORT).show();
        request.proceed();
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA)
    protected void showNeverAskForSettings() {
        Toast.makeText(this, "OnNeverAskAgain", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

}
