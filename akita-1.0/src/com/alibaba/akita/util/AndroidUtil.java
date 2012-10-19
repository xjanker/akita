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

package com.alibaba.akita.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import com.alibaba.akita.R;

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

    public static String getAppName(Context context) {
        String verName = context.getResources().getText(R.string.app_name)  // TODO NEED PROVE
                .toString();
        return verName;
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
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }
        } else { // show IME
            try {
                ((InputMethodManager) context
                        .getSystemService(Activity.INPUT_METHOD_SERVICE))
                        .showSoftInput(context.getCurrentFocus(),
                                InputMethodManager.SHOW_IMPLICIT);
            } catch (NullPointerException npe) {
                npe.printStackTrace();
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

    public static void showNetworkFailureDlg(final Activity context) {
        try {
            AlertDialog.Builder b = new AlertDialog.Builder(context)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.check_network_no_available_network_title)
                    .setMessage(
                            R.string.check_network_no_available_network_message);
            b.setPositiveButton(android.R.string.ok, null).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
