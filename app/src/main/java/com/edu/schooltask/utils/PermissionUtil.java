package com.edu.schooltask.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by 夜夜通宵 on 2017/9/11.
 */

//权限工具类
public class PermissionUtil {
    public static final int LOCATION_PERMISSION = 0;
    public static final int READ_STORAGE_PERMISSION = 1;
    public static final int WRITE_STORAGE_PERMISSION = 1;
    public static final int CAMERA_PERMISSION = 3;

    public static boolean checkLocationPermission(Activity activity){
        boolean result = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
        if (!result){   //请求权限
            ActivityCompat.requestPermissions(activity, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
        }
        return result;
    }

    public static boolean checkReadStoragePermission(Activity activity){
        boolean result = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
        if(!result) ActivityCompat.requestPermissions(activity, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_PERMISSION);
        return result;
    }

    public static boolean checkWriteStoragePermission(Activity activity){
        boolean result = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
        if(!result) ActivityCompat.requestPermissions(activity, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_STORAGE_PERMISSION);
        return result;
    }

    public static boolean checkCameraPermission(Activity activity){
        boolean result = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
        if(!result) ActivityCompat.requestPermissions(activity, new String[]{
                Manifest.permission.CAMERA}, CAMERA_PERMISSION);
        return result;
    }
}
