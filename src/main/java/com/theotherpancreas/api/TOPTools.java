package com.theotherpancreas.api;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class TOPTools {
    public static void requestReadPermission(Activity activity) {
        String permission = "com.theotherpancreas.api.permission.READ";
        boolean granted = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
        Log.v("PERMISSIONS", "The permission \"" + permission + "\" has previously " + (granted ? " been" : " NOT been") + " granted");
        if (!granted) {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, 0);
        }
    }

    public static void requestWritePermission(Activity activity) {
        String permission = "com.theotherpancreas.api.permission.WRITE";
        boolean granted = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
        Log.v("PERMISSIONS", "The permission \"" + permission + "\" has previously " + (granted ? " been" : " NOT been") + " granted");
        if (!granted) {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, 0);
        }
    }

    public static void requestReadAndWritePermission(Activity activity) {
        String readPermission = "com.theotherpancreas.api.permission.READ";
        String writePermission = "com.theotherpancreas.api.permission.WRITE";
        boolean granted = ContextCompat.checkSelfPermission(activity, readPermission) == PackageManager.PERMISSION_GRANTED;
        granted &= ContextCompat.checkSelfPermission(activity, writePermission) == PackageManager.PERMISSION_GRANTED;

        Log.v("PERMISSIONS", "The permissions \"" + readPermission + "\" and \"" + writePermission + "\" have previously " + (granted ? " been" : " NOT been") + " granted");

        if (!granted) {
            ActivityCompat.requestPermissions(activity, new String[]{readPermission, writePermission}, 0);
        }
    }
}
