package com.ec.www.utils.permission;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.ec.www.base.AbstractApplication;
import com.ec.www.utils.SPUtil;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by hefuyi on 2016/11/7.
 */

public class PermissionManager {

    private static final String fileName = "permission.conf";
    private static final String KEY_PREV_PERMISSIONS = "previous_permissions";
    private static final String KEY_IGNORED_PERMISSIONS = "ignored_permissions";
    private SPUtil spUtil;
    private ArrayList<PermissionRequest> permissionRequests = new ArrayList<>();

    private PermissionManager() {
        spUtil = SPUtil.init(fileName);
    }

    private static class PERMISSION_MANAGER_INSTANCE {
        static {
            PERMISSION_MANAGER = new PermissionManager();
        }

        private static final PermissionManager PERMISSION_MANAGER;
    }

    public static PermissionManager getInstance() {
        return PERMISSION_MANAGER_INSTANCE.PERMISSION_MANAGER;
    }

    /**
     * Check that all given permissions have been granted by verifying that each entry in the
     * given array is of the value {@link PackageManager#PERMISSION_GRANTED}.
     */
    public static boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if the Activity has access to a all given permission.
     */
    public static boolean hasPermission(Activity activity, String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    /**
     * If we override other methods, lets do it as well, and keep name same as it is already weird enough.
     * Returns true if we should show explanation why we need this permission.
     */
    public static boolean shouldShowRequestPermissionRationale(Activity activity, String permissions) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions);
    }

    /**
     * If we override other methods, lets do it as well, and keep name same as it is already weird enough.
     * Returns true if we should show explanation why we need this permission.
     */
    public static boolean shouldShowRequestPermissionRationale(Fragment fragment, String permissions) {
        return fragment.shouldShowRequestPermissionRationale(permissions);
    }

    public void askForPermission(Activity activity, String permission, PermissionCallback permissionCallback) {
        askForPermission(activity, new String[]{permission}, permissionCallback);
    }

    public void askForPermission(Fragment fragment, String permission, PermissionCallback permissionCallback) {
        askForPermission(fragment, new String[]{permission}, permissionCallback);
    }

    /**
     * 请求权限,并将PermissionRequest保存
     *
     * @param activity
     * @param permissions
     * @param permissionCallback
     */
    public void askForPermission(Activity activity, String[] permissions, PermissionCallback permissionCallback) {
        if (permissionCallback == null) {
            return;
        }
        if (hasPermission(activity, permissions)) {
            permissionCallback.permissionGranted();
            return;
        }
        PermissionRequest permissionRequest = new PermissionRequest(new ArrayList<>(Arrays.asList(permissions)), permissionCallback);
        permissionRequests.add(permissionRequest);

        ActivityCompat.requestPermissions(activity, permissions, permissionRequest.getRequestCode());
    }

    /**
     * 请求权限,并将PermissionRequest保存
     *
     * @param fragment
     * @param permissions
     * @param permissionCallback
     */
    public void askForPermission(Fragment fragment, String[] permissions, PermissionCallback permissionCallback) {
        if (permissionCallback == null) {
            return;
        }
        if (hasPermission(fragment.getActivity(), permissions)) {
            permissionCallback.permissionGranted();
            return;
        }
        PermissionRequest permissionRequest = new PermissionRequest(new ArrayList<>(Arrays.asList(permissions)), permissionCallback);
        permissionRequests.add(permissionRequest);

        fragment.requestPermissions(permissions, permissionRequest.getRequestCode());
    }

    /**
     * 根据授权结果回调,并刷新当前的权限列表
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionRequest requestResult = new PermissionRequest(requestCode);
        if (permissionRequests.contains(requestResult)) {
            PermissionRequest permissionRequest = permissionRequests.get(permissionRequests.indexOf(requestResult));
            if (verifyPermissions(grantResults)) {
                //Permission has been granted
                permissionRequest.getPermissionCallback().permissionGranted();
            } else {
                permissionRequest.getPermissionCallback().permissionRefused();

            }
            permissionRequests.remove(requestResult);
        }
        refreshMonitoredList();
    }

    /**
     * Get list of currently granted permissions, without saving it inside PermissionManager
     *
     * @return currently granted permissions
     */
    public ArrayList<String> getGrantedPermissions() {

        ArrayList<String> permissions = new ArrayList<String>();
        ArrayList<String> permissionsGranted = new ArrayList<String>();
        //Group location
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        //Group Calendar
        permissions.add(Manifest.permission.WRITE_CALENDAR);
        permissions.add(Manifest.permission.READ_CALENDAR);
        //Group Camera
        permissions.add(Manifest.permission.CAMERA);
        //Group Contacts
        permissions.add(Manifest.permission.WRITE_CONTACTS);
        permissions.add(Manifest.permission.READ_CONTACTS);
        permissions.add(Manifest.permission.GET_ACCOUNTS);
        //Group Microphone
        permissions.add(Manifest.permission.RECORD_AUDIO);
        //Group Phone
        permissions.add(Manifest.permission.CALL_PHONE);
        permissions.add(Manifest.permission.READ_PHONE_STATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            permissions.add(Manifest.permission.READ_CALL_LOG);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            permissions.add(Manifest.permission.WRITE_CALL_LOG);
        }
        permissions.add(Manifest.permission.ADD_VOICEMAIL);
        permissions.add(Manifest.permission.USE_SIP);
        permissions.add(Manifest.permission.PROCESS_OUTGOING_CALLS);
        //Group Body sensors
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            permissions.add(Manifest.permission.BODY_SENSORS);
        }
        //Group SMS
        permissions.add(Manifest.permission.SEND_SMS);
        permissions.add(Manifest.permission.READ_SMS);
        permissions.add(Manifest.permission.RECEIVE_SMS);
        permissions.add(Manifest.permission.RECEIVE_WAP_PUSH);
        permissions.add(Manifest.permission.RECEIVE_MMS);
        //Group Storage
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(AbstractApplication.getContext(), permission) == PackageManager.PERMISSION_GRANTED) {
                permissionsGranted.add(permission);
            }
        }
        return permissionsGranted;
    }

    /**
     * Refresh currently granted permission list, and save it for later comparing using @permissionCompare()
     */
    public void refreshMonitoredList() {
        spUtil.putStrListValue(KEY_PREV_PERMISSIONS, getGrantedPermissions());
    }

    /**
     * Get list of previous Permissions, from last refreshMonitoredList() call and they may be outdated,
     * use getGrantedPermissions() to get current
     */
    public ArrayList<String> getPreviousPermissions() {
        ArrayList<String> prevPermissions = new ArrayList<String>();
        prevPermissions.addAll(spUtil.getStrListValue(KEY_PREV_PERMISSIONS));
        return prevPermissions;
    }

    public ArrayList<String> getIgnoredPermissions() {
        ArrayList<String> ignoredPermissions = new ArrayList<String>();
        ignoredPermissions.addAll(spUtil.getStrListValue(KEY_IGNORED_PERMISSIONS));
        return ignoredPermissions;
    }

    /**
     * Lets see if we already ignore this permission
     */
    public boolean isIgnoredPermission(String permission) {
        if (permission == null) {
            return false;
        }
        return getIgnoredPermissions().contains(permission);
    }

    /**
     * Not that needed method but if we override others it is good to keep same.
     */
    public boolean checkPermission(String permissionName) {
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(AbstractApplication.getContext(), permissionName);
    }

}
