/*
 * Copyright 1999-2101 Alibaba Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.akita.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: justin
 * Date: 12-4-3
 * Time: 下午8:41
 *
 * @author Justin Yang
 */
public class AndroidUtil {
    private static final String TAG = "AndroidUtil";
    /**
     * Dp float value xform to Px int value
     * @param context
     * @param dpValue
     * @return int px value
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int getVerCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            Log.e(TAG, "Cannot find package and its version info.");
            return -1;
        }
    }

    public static String getVerName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(
                    context.getPackageName() , 0).versionName;
        } catch (Exception e) {
            Log.e(TAG, "Cannot find package and its version info.");
            return "no version name";
        }
    }

    /**
     * 获取DeviceId
     *
     * @param context
     * @return 当获取到的TelephonyManager为null时，将返回"null"
     */
    public static String getDeviceId(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (tm == null) {
            return "null";
        } else {
            String id = tm.getDeviceId();
            return id == null? "null" : id;
        }
    }

    /**
     * 显示或隐藏IME
     *
     * @param context
     * @param bHide
     */
    public static void hideIME(Activity context, boolean bHide) {
        if (bHide) {
            try {
                ((InputMethodManager) context
                        .getSystemService(Activity.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(context.getCurrentFocus()
                                .getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
            } catch (Throwable tr) {
                Log.i(TAG, tr.toString()+"");
            }
        } else { // show IME
            try {
                ((InputMethodManager) context
                        .getSystemService(Activity.INPUT_METHOD_SERVICE))
                        .showSoftInput(context.getCurrentFocus(),
                                InputMethodManager.SHOW_IMPLICIT);
            } catch (Throwable tr) {
                Log.i(TAG, tr.toString()+"");
            }
        }
    }

    /**
     * 在dialog开启前确定需要开启后跳出IME
     *
     * @param dialog
     */
    public static void showIMEonDialog(AlertDialog dialog) {
        try {
            Window window = dialog.getWindow();
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * 判断一个apk是否安装
     *
     * @param ctx
     * @param packageName
     * @return
     */
    public static boolean isPkgInstalled(Context ctx, String packageName) {
        PackageManager pm = ctx.getPackageManager();
        try {
            pm.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    public static boolean isGooglePlayInstalled(Context ctx) {
        return isAndroidMarketInstalled(ctx);
    }

    /**
     * @deprecated
     * use isGooglePlayInstalled(Context ctx) instead
     * @param ctx
     * @return
     */
    public static boolean isAndroidMarketInstalled(Context ctx) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://search?q=foo"));
        PackageManager pm = ctx.getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(intent, 0);
        if (list.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static void installApkWithPrompt(File apkFile, Context context) {
        Intent promptInstall = new Intent(Intent.ACTION_VIEW);
        promptInstall.setDataAndType(Uri.fromFile(apkFile),
                "application/vnd.android.package-archive");
        context.startActivity(promptInstall);
    }

    /**
     * @param context used to check the device version and DownloadManager information
     * @return true if the download manager is available
     */
    public static boolean isDownloadManagerAvailable(Context context) {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
                return false;
            }
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setClassName("com.android.providers.downloads.ui", "com.android.providers.downloads.ui.DownloadList");
            List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
                    PackageManager.MATCH_DEFAULT_ONLY);
            return list.size() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Note: Make sure isDownloadManagerAvailable return is true before use this method.
     * @param apkName Apk File Name
     * @param fullApkUrl url of full
     * @param context Context
     */
    public static void downloadApkByDownloadManager(String apkName, String fullApkUrl, Context context) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fullApkUrl));
        request.setDescription(fullApkUrl);
        request.setTitle(apkName);

        // in order for this if to run, you must use the android 3.2 to compile your app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, apkName);
        request.setVisibleInDownloadsUi(false);
        request.setMimeType("application/vnd.android.package-archive");

        // get download service and enqueue file
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    /**
     * 判断是否有网络
     * @param context context
     * @return true or false
     */
    public static boolean networkStatusOK(final Context context) {
        boolean netStatus = false;

        try{
            ConnectivityManager connectManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null) {
                if (activeNetworkInfo.isAvailable() && activeNetworkInfo.isConnected()) {
                    netStatus = true;
                }
            }
        } catch (Exception e) {e.printStackTrace();}

        return netStatus;
    }

    public static final int NETWORK_TYPE_NONE = -0x1;  // 断网情况
    public static final int NETWORK_TYPE_WIFI = 0x1;   // WiFi模式
    public static final int NETWOKR_TYPE_MOBILE = 0x2; // 2g 3g 4g...模式

    /**
     * 获取当前网络状态的类型
     * @param context
     * @return 返回网络类型
     */
    public static int getCurrentNetworkType(Context context){
        try {
            ConnectivityManager connManager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI); // wifi
            NetworkInfo mobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE); // mobile
            if (wifi != null && wifi.getState() == NetworkInfo.State.CONNECTED) {
                Log.d(TAG, "Current net type:  WIFI.");
                return NETWORK_TYPE_WIFI;
            } else if (mobile != null && mobile.getState() == NetworkInfo.State.CONNECTED){
                Log.d(TAG, "Current net type:  MOBILE.");
                return NETWOKR_TYPE_MOBILE;
            }
        } catch (Exception e) {
            Log.e(TAG, "Current net type:  NONE.");
        }

        return NETWORK_TYPE_NONE;
    }

    public static void showNetworkFailureDlg(final Activity context) {
        try {
            AlertDialog.Builder b = new AlertDialog.Builder(context)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("No available network")
                    .setMessage("Please note that there\\'s some problem on your network status. " +
                            "You must set your network properly first.");
            b.setPositiveButton(android.R.string.ok, null).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*********************************************************************************
     *   Returns the resource-IDs for all attributes specified in the
     *   given <declare-styleable>-resource tag as an int array.
     *
     *   @param  context     The current application context.
     *   @param  name        The name of the <declare-styleable>-resource-tag to pick.
     *   @return             All resource-IDs of the child-attributes for the given
     *                       <declare-styleable>-resource or <code>null</code> if
     *                       this tag could not be found or an error occured.
     *********************************************************************************/
    public static final int[] getResourceDeclareStyleableIntArray( Context context, String name ) {
        try {
            //use reflection to access the resource class
            Field[] fields2 = Class.forName( context.getPackageName() + ".R$styleable" ).getFields();

            //browse all fields
            for ( Field f : fields2 )
            {
                //pick matching field
                if ( f.getName().equals( name ) )
                {
                    //return as int array
                    int[] ret = (int[])f.get( null );
                    return ret;
                }
            }
        }
        catch ( Throwable t )
        {
        }

        return null;
    }

    /**
     * Dynamically load R.styleable.name
     * @param context
     * @param name
     * @return
     */
    public static int getStyleableResourceInt(Context context, String name) {
        if (context == null) return 0;
        try {
            //use reflection to access the resource class
            Field[] fields2 = Class.forName( context.getPackageName() + ".R$styleable" ).getFields();

            //browse all fields
            for ( Field f : fields2 ) {
                //pick matching field
                if ( f.getName().equals( name ) )
                {
                    //return as int array
                    int ret = (Integer)f.get( null );
                    return ret;
                }
            }
        } catch ( Throwable t ) { /*no op*/ }
        return 0;
    }
}
