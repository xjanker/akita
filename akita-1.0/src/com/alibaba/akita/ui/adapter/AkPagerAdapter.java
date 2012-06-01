/*
 * Copyright 1999-2101 Alibaba Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.akita.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * Date: 12-5-31
 * Time: 下午2:14
 *
 * @author zhe.yangz
 */
public abstract class AkPagerAdapter<T> extends PagerAdapter {
    protected ArrayList<T> mData = new ArrayList();
    protected LayoutInflater mInflater;
    protected Context mContext;

    public AkPagerAdapter(Context c) {
        mContext = c;
        mInflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    public void addItem(final T item) {
        mData.add(item);
        notifyDataSetChanged();
    }

    public void addItem(int idx, final T item) {
        mData.add(idx, item);
        notifyDataSetChanged();
    }

    public void clearItems() {
        mData.clear();
        notifyDataSetChanged();
    }

    public T getItem(int position) {
        return mData.get(position);
    }
}
