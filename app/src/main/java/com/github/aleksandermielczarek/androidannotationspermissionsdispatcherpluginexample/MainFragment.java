package com.github.aleksandermielczarek.androidannotationspermissionsdispatcherpluginexample;

import android.Manifest;
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
    protected void askForPermission() {
        showCamera();
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    protected void showCamera() {
        Toast.makeText(getContext(), "Permission for camera granted in fragment", Toast.LENGTH_SHORT).show();
    }

    @OnShowRationale(Manifest.permission.CAMERA)
    protected void showRationaleForCamera(PermissionRequest request) {
        Toast.makeText(getContext(), "OnShowRationale for camera in fragment", Toast.LENGTH_SHORT).show();
        request.proceed();
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA)
    protected void showNeverAskForCamera() {
        Toast.makeText(getContext(), "OnNeverAskAgain for camera in fragment", Toast.LENGTH_SHORT).show();
    }

}
