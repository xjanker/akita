package com.alibaba.akita.samples;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.alibaba.akita.samples.testdata.PhotoWallItem;
import com.alibaba.akita.widget.RemoteImageView;

import java.util.ArrayList;

public class MainActivity extends ListActivity
{
    private MyCustomAdapter mAdapter;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mAdapter = new MyCustomAdapter();
        ArrayList<PhotoWallItem> pwis = (ArrayList<PhotoWallItem>) PhotoWallItem.mock();
        for (PhotoWallItem p : pwis) {
            mAdapter.addItem(p.getUrl());
        }

        setListAdapter(mAdapter);
    }

    private class MyCustomAdapter extends BaseAdapter {

        private ArrayList<String> mData = new ArrayList();
        private LayoutInflater mInflater;

        public MyCustomAdapter() {
            mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void addItem(final String item) {
            mData.add(item);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public String getItem(int position) {
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
                convertView = mInflater.inflate(R.layout.list_item1, null);
                holder = new ViewHolder();
                holder.textView = (TextView)convertView.findViewById(R.id.text);
                holder.imageView = (RemoteImageView)convertView.findViewById(R.id.ri);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            holder.textView.setText(mData.get(position));
            holder.imageView.setImageUrl(mData.get(position));
            holder.imageView.loadImage();
            return convertView;
        }
    }

    public static class ViewHolder {
        public TextView textView;
        public RemoteImageView imageView;
    }
}
