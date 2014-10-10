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


/**
 * 封装Log 
 * @author zhe.yangz 2011-11-25 下午04:06:57
 */
public class Log {
    static boolean SHOW_LOG = true;

    public static void i(String tag, String string) {
        if (SHOW_LOG) android.util.Log.i(tag, string);
    }
    public static void e(String tag, String string) {
        if (SHOW_LOG) android.util.Log.e(tag, string);
    }
    public static void e(String tag, String string, Throwable tr) {
        if (SHOW_LOG) android.util.Log.e(tag, string, tr);
    }
    public static void d(String tag, String string) {
        if (SHOW_LOG) android.util.Log.d(tag, string);
    }
    public static void w(String tag, String string) {
        if (SHOW_LOG) android.util.Log.w(tag, string);
    }
    public static void w(String tag, String string, Throwable tr) {
        if (SHOW_LOG) android.util.Log.w(tag, string, tr);
    }
    public static void v(String tag, String string) {
        if (SHOW_LOG) android.util.Log.v(tag, string);
    }
    public static void v(String tag, String string, Throwable tr) {
        if (SHOW_LOG) android.util.Log.v(tag, string, tr);
    }

}

