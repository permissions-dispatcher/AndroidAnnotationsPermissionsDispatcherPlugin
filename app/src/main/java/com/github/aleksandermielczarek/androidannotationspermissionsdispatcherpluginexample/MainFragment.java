package com.github.aleksandermielczarek.androidannotationspermissionsdispatcherpluginexample;

import android.Manifest;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by Aleksander Mielczarek on 15.09.2016.
 */
@EFragment(R.layout.fragment_main)
@RuntimePermissions
public class MainFragment extends Fragment {

    @Click(R.id.permissionButton)
    void askForPermission() {
        MainFragmentPermissionsDispatcher.showCameraWithCheck(this);
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    void showCamera() {
        Toast.makeText(getContext(), "Permission granted in fragment", Toast.LENGTH_SHORT).show();
    }

    @OnShowRationale(Manifest.permission.CAMERA)
    protected void showRationaleForSettings(PermissionRequest request) {
        Toast.makeText(getContext(), "OnShowRationale", Toast.LENGTH_SHORT).show();
        request.proceed();
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA)
    protected void showNeverAskForSettings() {
        Toast.makeText(getContext(), "OnNeverAskAgain", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
