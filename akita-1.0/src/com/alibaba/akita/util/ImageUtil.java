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

package com.alibaba.akita.util;

import android.graphics.Bitmap;

/**
 * Created with IntelliJ IDEA.
 * Date: 12-4-9
 * Time: 下午3:45
 *
 * @author zhe.yangz
 */
public class ImageUtil {

    /**
     * recreate the bitmap, and make it be scaled to box (maxWeight, maxHeight)
     * note: the old bitmap has not being recycled, you must do it yourself.
     * @param bitmap
     * @param boxHeight
     * @param boxWidth
     * @return the new Bitmap
     */
    public static Bitmap xform(Bitmap bitmap, int boxWidth, int boxHeight) {
        int src_w = bitmap.getWidth();
        int src_h = bitmap.getHeight();

        if (boxHeight <= 0 && boxWidth <= 0) {
            return Bitmap.createScaledBitmap(bitmap, src_w, src_h, true);
        } else if (boxHeight <= 0) {
            boxHeight = (int)(src_h / (float)src_w * boxWidth);
        } else if (boxWidth <= 0) {
            boxWidth = (int)(src_w / (float)src_h * boxHeight);
        }


        return Bitmap.createScaledBitmap(bitmap, boxWidth, boxHeight, true);
    }
}
