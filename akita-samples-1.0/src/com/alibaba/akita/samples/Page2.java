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

package com.alibaba.akita.samples;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.alibaba.akita.exception.AkInvokeException;
import com.alibaba.akita.exception.AkServerStatusException;
import com.alibaba.akita.proxy.ProxyFactory;
import com.alibaba.akita.samples.api.WeiboApi;
import com.alibaba.akita.samples.api.weibo.Statuses;
import com.alibaba.akita.util.Log;
import com.alibaba.akita.widget.RemoteImageView;



import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: justin
 * Date: 12-4-3
 * Time: 下午7:51
 *
 * @author Justin Yang
 */
public class Page2 extends ListActivity {
    private static final String TAG = "Page2";
    private MyCustomAdapter mAdapter;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_page2);

        mAdapter = new MyCustomAdapter();
        WeiboApi weiboApi = ProxyFactory.getProxy(WeiboApi.class);
        try {
            Statuses statuses = weiboApi.suggestions_statuses_hot(3, 1, 100, 1);
            for (Statuses.StatusWrap s : statuses.statuses) {
                mAdapter.addItem(s);
            }
        } catch (AkInvokeException e) {
            Log.e(TAG, e.toString(), e);
        } catch (AkServerStatusException e) {
            Log.e(TAG, e.toString(), e);
        }

        setListAdapter(mAdapter);
    }

    private class MyCustomAdapter extends BaseAdapter {

        private ArrayList<Statuses.StatusWrap> mData = new ArrayList();
        private LayoutInflater mInflater;

        public MyCustomAdapter() {
            mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void addItem(final Statuses.StatusWrap item) {
            mData.add(item);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Statuses.StatusWrap getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item2, null);
                holder = new ViewHolder();
                holder.textView = (TextView)convertView.findViewById(R.id.text);
                holder.imageView = (RemoteImageView)convertView.findViewById(R.id.ri);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            holder.textView.setText(mData.get(position).status.text);
            if (mData.get(position).status.original_pic!=null) {
                holder.imageView.setImageUrl(mData.get(position).status.original_pic);
                holder.imageView.loadImage();
            }
            return convertView;
        }
    }

    public static class ViewHolder {
        public TextView textView;
        public RemoteImageView imageView;
    }
}
