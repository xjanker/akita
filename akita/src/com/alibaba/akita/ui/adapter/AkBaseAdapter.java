package com.alibaba.akita.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: justin
 * Date: 12-4-8
 * Time: 下午2:42
 *
 * @author Justin Yang
 */
public abstract class AkBaseAdapter<T> extends BaseAdapter {

    protected ArrayList<T> mData = new ArrayList();
    protected LayoutInflater mInflater;
    protected Context mContext;

    public AkBaseAdapter(Context c) {
        mContext = c;
        try {
            mInflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        } catch (Exception e) {e.printStackTrace();}
    }

    public void addItem(final T item) {
        mData.add(item);
        notifyDataSetChanged();
    }

    public void addItem(int idx, final T item) {
        mData.add(idx, item);
        notifyDataSetChanged();
    }

    public T removeItem(int idx) {
        if (idx < mData.size()) {
            T t =  mData.remove(idx);
            notifyDataSetChanged();
            return t;
        } else {
            return null;
        }
    }

    public ArrayList<T> getData() {
        return mData;
    }

    public void clearItems() {
        mData.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public T getItem(int position) {
        if (position < mData.size()) {
            return mData.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Example：
     *
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
    */
    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent);

}
