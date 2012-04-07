/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.akita.cache.impl;

import java.io.File;
import java.lang.ref.SoftReference;

import android.graphics.Bitmap;
import android.os.Environment;
import com.alibaba.akita.cache.FilesCache;
import com.alibaba.akita.cache.MemCache;
import com.alibaba.akita.util.HashUtil;

import android.content.Context;

/**
 * 在SD卡中存储文件夹的实现 
 * 要求key.length >= 4
 * @author zhe.yangz 2012-3-31 上午09:51:01
 */
public abstract class FilesCacheSDFoldersImpl<V> implements FilesCache<V> {
    private String mCacheTag;
    private MemCache<String, V> mSoftBitmapCache;
    protected Context mContext;
    
    /**
     * 
     */
    public FilesCacheSDFoldersImpl(Context context, String cacheTag){
        mContext = context;
        mCacheTag = cacheTag;
        mSoftBitmapCache = new MemCacheSoftRefImpl<String, V>();
    }

    protected abstract V xform(String fileAbsoPathAndName);
    protected abstract void output(String fileAbsoPath, String fileName, V v);
    
    private String mapRule(String key) {
        return HashUtil.md5(key);
    }

    private String getSpecifiedCacheFileName(String hashedKey) {
        return hashedKey + ".cache";
    }

    private String getSpecifiedCacheFilePath(String hashedKey) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/Android/data/" + mContext.getPackageName() + "/cache";
        path += "/" + mCacheTag + "/" + hashedKey.substring(0, 2)
                + "/" + hashedKey.substring(2, 4) + "/";
        return path;
    }
    
    @Override
    public V get(String key) {
        V bm = mSoftBitmapCache.get(key);
        if (bm != null) {
            return bm;
        } else {
            bm = doLoad(mapRule(key));
            mSoftBitmapCache.put(key, bm);
            return bm;
        }
    }

    private V doLoad(String hashedKey) {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {    // We can read and write the media

        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {    // We can only read the media

        } else {    // Something else is wrong. It may be one of many other states, but all we need
                    // to know is we can neither read nor write
              return null;
        }

        String pathAndName = getSpecifiedCacheFilePath(hashedKey)
                + getSpecifiedCacheFileName(hashedKey);
        File f = new File(pathAndName);
        if (f.exists()) {
            return xform(pathAndName);
        } else {
            return null;
        }
    }

    @Override
    public V put(String key, V value) {
        if (value != null) {
            V oldV = remove(key);
            doSave(mapRule(key), value);
            mSoftBitmapCache.put(key, value);
            return oldV;
        }
        return null;
    }

    /**
     * @param hashedKey
     * @param value
     */
    private void doSave(String hashedKey, V value) {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {                     // We can read and write the media

        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {    // We can only read the media
            return;
        } else {    // Something else is wrong. It may be one of many other states, but all we need
                    // to know is we can neither read nor write
            return;
        }

        // doSave V to the sd cache filesystem
        String path = getSpecifiedCacheFilePath(hashedKey);
        output(path, getSpecifiedCacheFileName(hashedKey), value);
    }

    @Override
    public V remove(String key) {
        return doDelete(mapRule(key));
    }

    private V doDelete(String hashedKey) {
        V oldV = null;
        String pathAndName = getSpecifiedCacheFilePath(hashedKey)
                + getSpecifiedCacheFileName(hashedKey);
        File f = new File(pathAndName);
        if (f.exists()) {
            oldV = doLoad(hashedKey);
            f.delete();
        }
        return oldV;
    }
}
