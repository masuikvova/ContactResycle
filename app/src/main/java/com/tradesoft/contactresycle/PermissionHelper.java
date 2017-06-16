package com.tradesoft.contactresycle;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;


public class PermissionHelper {
    public static final int REQUEST_CODE_PERMISSIONS = 0;

    @TargetApi(23)
    public static void requestContactsPermission(Activity activity) {
        int hasPermission = activity.checkSelfPermission(Manifest.permission.READ_CONTACTS);

        List<String> permissionList = new ArrayList<>();
        if (hasPermission != PackageManager.PERMISSION_GRANTED)
            permissionList.add(Manifest.permission.READ_CONTACTS);

        if (permissionList.contains(Manifest.permission.READ_CONTACTS))
            ActivityCompat.requestPermissions(activity, permissionList.toArray(new String[permissionList.size()]), REQUEST_CODE_PERMISSIONS);
    }

    @TargetApi(23)
    public static void requestStoragePermission(Activity activity) {
        int hasReadStoragePermission = activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> permissionList = new ArrayList<>();
        if (hasReadStoragePermission != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (permissionList.contains(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(activity, permissionList.toArray(new String[permissionList.size()]), REQUEST_CODE_PERMISSIONS);
        }
    }

    @TargetApi(23)
    private void requestCameraPermission(Activity activity) {
        int hasCameraPermission = ContextCompat.checkSelfPermission(activity, android.Manifest.permission.CAMERA);
        int hasWriteStoragePermission = ContextCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> permissionList = new ArrayList<>();

        if (hasCameraPermission != PackageManager.PERMISSION_GRANTED)
            permissionList.add(Manifest.permission.CAMERA);

        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED)
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionList.contains(Manifest.permission.CAMERA) || permissionList.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE))
            ActivityCompat.requestPermissions(activity, permissionList.toArray(new String[permissionList.size()]), REQUEST_CODE_PERMISSIONS);
    }

    @TargetApi(23)
    private void requestCallPermission(Activity activity) {
        int hasPermission = activity.checkSelfPermission(Manifest.permission.CALL_PHONE);

        List<String> permissionList = new ArrayList<>();
        if (hasPermission != PackageManager.PERMISSION_GRANTED)
            permissionList.add(Manifest.permission.CALL_PHONE);

        if (permissionList.contains(Manifest.permission.CALL_PHONE))
            ActivityCompat.requestPermissions(activity, permissionList.toArray(new String[permissionList.size()]), REQUEST_CODE_PERMISSIONS);
    }
}
