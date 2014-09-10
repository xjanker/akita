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
package org.akita.cache;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * reserveTime==0  1年
 * 默认为24小时
 * @author zhe.yangz 2012-3-30 下午03:23:19
 */
public class SimpleCacheSqliteImpl implements SimpleCache {
    private SQLiteHelper mSqliteHelper = null;
    private long mReserveTime = 24 * 3600 * 1000L;
    
    /**
     * 
     */
    protected SimpleCacheSqliteImpl(Context context, String dbName, String tbName,
                                 int version, long reserveTime) {
        mSqliteHelper = new SQLiteHelper(context, dbName, null, version, tbName);
        if (reserveTime > 0) {
            mReserveTime = reserveTime;
        } else if (reserveTime == 0){
            mReserveTime = 365 * 24 * 3600 * 1000L;
        }
    }
    
    @Override
    public String get(String key) {
        CacheObject co = mSqliteHelper.getCOByKey(key);
        if (co == null) {
            return null;
        } else if ((System.currentTimeMillis()-co.cacheTime) > mReserveTime) {
            remove(key);
            return null;
        } else {
            return co.value;
        }
    }

    public ArrayList<String> getLatest(int num) {
        ArrayList<CacheObject> cos = mSqliteHelper.getLatestCOs(num);
        ArrayList<String> rets = new ArrayList<String>();
        if (cos != null) {
            for (CacheObject co : cos) {
                if ((System.currentTimeMillis()-co.cacheTime) > mReserveTime) {
                    remove(co.key);
                } else {
                    rets.add(co.value);
                }
            }
        }
        return rets;
    }

    @Override
    public String put(String key, String value) {
        String oldValue = null;
        
        CacheObject co = mSqliteHelper.getCOByKey(key);
        if (co != null) {
            oldValue = co.value;
            mSqliteHelper.updateCOByKey(key, value);
        } else {
            mSqliteHelper.insertCOByKey(key, value);
        }
        return oldValue;
    }

    @Override
    public String remove(String key) {
        String oldValue = null;
        CacheObject co = mSqliteHelper.getCOByKey(key);
        if (co != null) {
            oldValue = co.value;
            mSqliteHelper.deleteCOByKey(key);
        }
        return oldValue;
    }

    @Override
    public void removeAll() {
        mSqliteHelper.deleteAllKey();
    }

    @Override
    public void close() {
        mSqliteHelper.close();
    }

    public class SQLiteHelper extends SQLiteOpenHelper {
        public String Lock = "dblock";
        private String mTableName;
        /**
         * @param context
         * @param name
         * @param factory
         * @param version
         */
        public SQLiteHelper(Context context, String name,
                            CursorFactory factory, int version, String tbName){
            super(context, name, factory, version);
            mTableName = tbName;

            try{
                SQLiteDatabase sqLiteDatabase = getWritableDatabase();
                onCreate(getWritableDatabase());
                sqLiteDatabase.close();
            } catch (Exception e) {e.printStackTrace();}
        }

        /**
         * @param key
         * @param value
         */
        public void updateCOByKey(String key, String value) {
            synchronized(Lock) {
                SQLiteDatabase db = null;
                try {
                    db = getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("value", value);
                    values.put("cacheTime", System.currentTimeMillis());
                    db.update(mTableName, values , "key=?", new String[]{key});
                } finally {
                    if (db != null) db.close();
                }
            }
        }
        
        /**
         * @param key
         * @param value
         */
        public void insertCOByKey(String key, String value) {
            synchronized(Lock) {
                SQLiteDatabase db = null;
                try {
                    db = getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("key", key);
                    values.put("value", value);
                    values.put("cacheTime", System.currentTimeMillis());
                    db.insert(mTableName, null, values);
                } finally {
                    if (db != null) db.close();
                }
            }
        }
        
        /**
         * @param key
         */
        public void deleteCOByKey(String key) {
            synchronized(Lock) {
                SQLiteDatabase db = null;
                try {
                    db = getWritableDatabase();
                    db.delete(mTableName, "key=?", new String[]{key});
                } finally {
                    if (db != null) db.close();
                }
            }
        }

        /**
         * Clear all in this table
         */
        public void deleteAllKey() {
            synchronized(Lock) {
                SQLiteDatabase db = null;
                try {
                    db = getWritableDatabase();
                    db.delete(mTableName, null, null);
                } finally {
                    if (db != null) db.close();
                }
            }
        }

        public ArrayList<CacheObject> getLatestCOs(int num) {
            synchronized(Lock) {
                ArrayList<CacheObject> cos = new ArrayList<CacheObject>();
                Cursor c = null;
                SQLiteDatabase db = null;
                try {
                    db = getReadableDatabase();
                    c = db.rawQuery("select `key`, `value`, `cacheTime` from `" + mTableName +
                            "` order by cacheTime desc limit " + num, null);
                    if (c.moveToFirst()) {
                        while (!c.isAfterLast()) {
                            String key = c.getString(0);
                            String value = c.getString(1);
                            long cacheTime = c.getLong(2);
                            CacheObject co = new CacheObject(key, value);
                            co.cacheTime = cacheTime;
                            cos.add(co);
                            c.moveToNext();
                        }
                        return cos;
                    } else {
                        return null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return cos;
                } finally {
                    if (c != null) c.close();
                    /*if (db != null) db.close();*/
                }
            }
        }

        /**
         * @param key
         * @return
         */
        public CacheObject getCOByKey(String key) {
            synchronized(Lock) {
                Cursor c = null;
                SQLiteDatabase db = null;
                try {
                    db = getReadableDatabase();
                    c = db.query(
                            mTableName, new String[]{"value","cacheTime"}, "key=?",
                            new String[]{key}, null, null, null);
                    if (c.moveToFirst()) {
                        String value = c.getString(0);
                        long cacheTime = c.getLong(1);
                        CacheObject co = new CacheObject(key, value);
                        co.cacheTime = cacheTime;

                        return co;
                    } else {
                        return null;
                    }
                } finally {
                    if (c != null) c.close();
                    /*if (db != null) db.close();*/
                }
            }
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table if not exists "+mTableName+"("
                    + "key varchar(128) primary key,"
                    + "value varchar(4096),"
                    + "cacheTime long)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {            
        }
        
    }
  
}
