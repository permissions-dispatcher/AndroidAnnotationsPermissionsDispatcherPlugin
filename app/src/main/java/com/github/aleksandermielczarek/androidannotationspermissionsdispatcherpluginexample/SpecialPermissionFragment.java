package com.github.aleksandermielczarek.androidannotationspermissionsdispatcherpluginexample;

import android.Manifest;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by Aleksander Mielczarek on 15.09.2016.
 */
@EFragment(R.layout.fragment_main)
@RuntimePermissions
public class SpecialPermissionFragment extends Fragment {

    @Click(R.id.permissionButton)
    protected void askForPermission() {
        systemAlertWindow();
    }

    @NeedsPermission(Manifest.permission.SYSTEM_ALERT_WINDOW)
    protected void systemAlertWindow() {
        Toast.makeText(getContext(), "Permission for system alert window granted", Toast.LENGTH_SHORT).show();
    }
}