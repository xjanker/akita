package com.alibaba.akita.widget.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 较安全的ImageView
 * 在onDraw阶段会判断bitmao是否被回收
 */
public class ImageView_ extends ImageView {

    public ImageView_(Context context) {
        super(context);
    }
    public ImageView_(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public ImageView_(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if(drawable != null && drawable instanceof BitmapDrawable){
            Bitmap bmp = ((BitmapDrawable) drawable).getBitmap();
            if(bmp != null && bmp.isRecycled()){
                setImageBitmap(null);
            }
        }
        super.onDraw(canvas);
    }

}
