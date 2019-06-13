package com.example.guoshijie.newsreader;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class CheckPermissions {
    public static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE

    };
    //请求状态码
    private static int REQUEST_PERMISSION_CODE = 1;

    private Context context;
    private Activity activity;
    public CheckPermissions(Context context, Activity activity){
        this.context=context;
        this.activity=activity;

    }

    public boolean checklacks(){
        if(lacksPermissions(context,PERMISSIONS_STORAGE)){
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            return true;
        }
        return false;

    }

    private  boolean lacksPermissions(Context mContexts, String[] PERMISSIONS_STORAGE) {
        for (String permission : PERMISSIONS_STORAGE) {
            if (lacksPermission(mContexts,permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否缺少权限
     */
    private  boolean lacksPermission(Context mContexts, String permission) {
        return ContextCompat.checkSelfPermission(mContexts, permission) ==
                PackageManager.PERMISSION_DENIED;
    }
}