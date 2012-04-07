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

import java.io.File;
import java.io.FileOutputStream;

import com.alibaba.akita.cache.impl.*;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.alibaba.akita.cache.impl.SimpleCacheSqliteImpl;

/**
 * 客户端Cache统一解决方案 
 * @author zhe.yangz 2012-3-30 下午03:23:31
 */
public class AkCacheManager {

    public static <K, V> MemCache<K, V> newMemLruCache(int maxSize) {
        return new MemCacheLruImpl<K, V>(maxSize);
    }

    public static <K, V> MemCache<K, V> newMemSoftRefCache() {
        return new MemCacheSoftRefImpl<K, V>();
    }
    
    public static SimpleCache getSimpleCache(Context context) {
        return new SimpleCacheSqliteImpl(context, "test.db", "test", 1, 0);
    }
    
    public static FilesCache<Bitmap> getImageFilesCache(Context context) {
        return new FilesCacheSDFoldersImpl<Bitmap>(context, "image0") {

            @Override
            protected Bitmap xform(String fileAbsoPath) {
                return BitmapFactory.decodeFile(fileAbsoPath);
            }

            @Override
            protected void output(String fileAbsoPath, String fileName, Bitmap v) {
                try {
                    File dir = new File(fileAbsoPath);
                    dir.mkdirs();
                    File f = new File(dir, fileName);
                    FileOutputStream fos = new FileOutputStream(f);
                    v.compress(Bitmap.CompressFormat.PNG, 90, fos);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
