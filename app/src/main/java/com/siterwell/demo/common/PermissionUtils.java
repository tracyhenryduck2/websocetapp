package com.siterwell.demo.common;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtils {

    public static boolean checkHasSavePermission(Activity activity, String... perms) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        for (String perm : perms) {
            boolean hasPerm = ContextCompat.checkSelfPermission(activity, perm) == PackageManager.PERMISSION_GRANTED;
            if (!hasPerm) {
                return false;
            }
        }
        return true;
    }

    public static boolean requestPermission(Activity activity, String[] perms, int requestCode){
        boolean hasPermission = checkHasSavePermission(activity,perms);
        if(!hasPermission){
            ActivityCompat.requestPermissions(activity, perms, requestCode);
            return false;
        }else {
            return true;
        }
    }

    //是否是临时拒绝
    public static boolean shouldShowRequestPermissionRationale(Activity activity, String perm){
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, perm);
    }

    public static void startToSetting(Activity activity){
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivity(intent);
    }

}
