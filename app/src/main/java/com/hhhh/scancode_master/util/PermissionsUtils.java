package com.hhhh.scancode_master.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by xhy on 2018/5/24 0024.
 *
 * @author xhy
 * @data 2018/5/24 0024
 * 申请权限工具类
 */

public class PermissionsUtils {
    private static PermissionsUtils sInstance;
    private OnPermissionListener onPermissionListener;

    //没有授权
    public static final int State_No_Granted = 0;
    //已经拒绝
    public static final int State_Has_Denied = -1;
    //已经允许
    public static final int State_Has_Allow = 1;

    private PermissionsUtils() {
    }

    public interface OnPermissionListener {
        void onRequestPermissionSuccess(int requestCode);

        void onRequestPermissionFailure(int requestCode);
    }

    public static PermissionsUtils getInstance() {
        if (sInstance == null) {
            synchronized (PermissionsUtils.class) {
                if (sInstance == null) {
                    sInstance = new PermissionsUtils();
                }
            }
        }
        return sInstance;
    }

    public void requestPermission(@NonNull Activity context, @NonNull String permission,
                                  int requestCode, OnPermissionListener onPermissionListener) {
        requestPermission(context,new String[]{permission}, requestCode, onPermissionListener);
    }

    /**
     * 申请权限的方法
     *
     * @param context              当前界面
     * @param permissions           需要申请的权限列表
     * @param requestCode          请求标识
     * @param onPermissionListener 回调监听
     */
    public void requestPermission(@NonNull Activity context, @NonNull String[] permissions,
                                  int requestCode, OnPermissionListener onPermissionListener) {
        this.onPermissionListener = onPermissionListener;

        int state = State_Has_Allow;
        final int permissionCount = permissions.length;
        for (int i = 0; i < permissionCount; i++) {

            //只要有一个没有授权，就要申请权限
            if (ContextCompat.checkSelfPermission(context,
                    permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                state = State_No_Granted;
                break;
            }else if (ContextCompat.checkSelfPermission(context,
                    permissions[i])
                    == PackageManager.PERMISSION_DENIED) {
                state = State_Has_Denied;
                break;
            }
        }

        /***
         * 是否是询问权限
         */
        if (state == State_No_Granted) {
            /**
             * 权限询问
             */
            /**
             * 权限询问
             */
            ActivityCompat.requestPermissions(context, permissions, requestCode);
        } else if (state == State_Has_Denied) {
            /**
             * 权限拒绝了
             */
            if (onPermissionListener != null) {
                onPermissionListener.onRequestPermissionFailure(requestCode);
            }
        } else {
            /**
             * 权限允许
             */
            if (onPermissionListener != null) {
                onPermissionListener.onRequestPermissionSuccess(requestCode);
            }
        }
    }

    /**
     * 处理权限回调结果
     * activity需要在系统自带的回调方法onRequestPermissionsResult中调用该方法
     *
     * @param requestCode          请求标识
     * @param grantResults         是否允许了权限
     * @param onPermissionListener 回调监听
     */
    public void onRequestPermissionsResult(int requestCode, int[] grantResults, OnPermissionListener onPermissionListener) {
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (onPermissionListener != null) {
                onPermissionListener.onRequestPermissionSuccess(requestCode);
            }
        } else {
            if (onPermissionListener != null) {
                onPermissionListener.onRequestPermissionFailure(requestCode);
            }
        }
    }
}
