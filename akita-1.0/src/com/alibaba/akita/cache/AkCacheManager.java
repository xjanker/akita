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
 *
 * Written by Justin Yang
 * xjanker@gmail.com
 */

package com.alibaba.akita.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import com.alibaba.akita.util.Log;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Client Cache Universal Solution
 * @author zhe.yangz 2012-3-30 下午03:23:31
 */
public class AkCacheManager {

    public static <K, V> MemCache<K, V> newMemLruCache(int maxSize) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return new MemCacheLruImpl<K, V>(maxSize);
        } else {
            return new MemCacheDummyImpl<K, V>(maxSize);
        }
    }

    public static <K, V> MemCache<K, V> newMemSoftRefCache() {
        return new MemCacheSoftRefImpl<K, V>();
    }

    /**
     * default reserve data 24 hours (-1)
     * @param context
     * @return
     */
    public static SimpleCache getSimpleCache(Context context) {
        return new SimpleCacheSqliteImpl(context, "simplecache.db", "defaulttable", 1, -1);
    }

    public static SimpleCache getSimpleCache(Context context, String tagName) {
        return new SimpleCacheSqliteImpl(context, "simplecache.db", tagName, 1, -1);
    }

    public static SimpleCache getSimpleCache(Context context, String tagName, int reserveTimeHours) {
        return new SimpleCacheSqliteImpl(
                context, "simplecache.db", tagName, 1, reserveTimeHours * 3600 * 1000);
    }

    /**
     * reserve data 365 days（0）
     * @param context
     * @return
     */
    public static SimpleCache getAppData(Context context) {
        return new SimpleCacheSqliteImpl(context, "appdata.db", "defaulttable", 1, 0);
    }

    /**
     * reserve data 365 days（0）
     * @param context
     * @param tagName
     * @return
     */
    public static SimpleCache getAppData(Context context, String tagName) {
        return new SimpleCacheSqliteImpl(context, "appdata.db", tagName, 1, 0);
    }
    
    public static FilesCache<Bitmap> getImageFilesCache(Context context) {
        return new FilesCacheSDFoldersImpl<Bitmap>(context, "image0") {

            @Override
            protected Bitmap xform(String fileAbsoPath) {
                try {
                    return BitmapFactory.decodeFile(fileAbsoPath);
                }
                catch (OutOfMemoryError ooe) {
                    Log.e(TAG, ooe.toString(), ooe);
                }
                return null;
            }

            @Override
            protected void output(String fileAbsoPath, String fileName, Bitmap v) {
                try {
                    File dir = new File(fileAbsoPath);
                    dir.mkdirs();
                    File f = new File(dir, fileName);
                    FileOutputStream fos = new FileOutputStream(f);
                    v.compress(Bitmap.CompressFormat.JPEG, 75, fos);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }


}
