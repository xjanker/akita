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
package org.akita.widget.common;

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
