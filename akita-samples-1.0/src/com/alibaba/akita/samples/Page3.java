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

package com.alibaba.akita.samples;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery;
import android.widget.TextView;
import com.alibaba.akita.samples.testdata.PhotoWallItem;
import com.alibaba.akita.ui.adapter.AkBaseAdapter;
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
public class Page3 extends Activity {
    private static final String TAG = "Page3";
    private MyCustomAdapter mAdapter;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_page3);
        ((TextView)findViewById(R.id.tv_page)).setText(TAG);

        mAdapter = new MyCustomAdapter(this);
        ArrayList<PhotoWallItem> pwis = (ArrayList<PhotoWallItem>) PhotoWallItem.mock();
        for (PhotoWallItem p : pwis) {
            mAdapter.addItem(p);
        }

        Gallery gallery1 = (Gallery)findViewById(R.id.gallery1);
        gallery1.setAdapter(mAdapter);
    }

    private class MyCustomAdapter extends AkBaseAdapter<PhotoWallItem> {

        public MyCustomAdapter(Context c) {
            super(c);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item3, null);
                holder = new ViewHolder();
                holder.textView = (TextView)convertView.findViewById(R.id.text);
                holder.imageView = (RemoteImageView)convertView.findViewById(R.id.ri);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            if (mData.get(position).getUrl()!=null) {
                holder.textView.setText(mData.get(position).getUrl());
                holder.imageView.setImageUrl(mData.get(position).getUrl());
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
