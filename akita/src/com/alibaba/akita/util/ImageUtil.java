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

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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

    /**
     * automatically compute the inSampleSize when decode byteArray
     * @param data
     * @param offset
     * @param length
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeSampledBitmapFromByteArray(byte[] data, int offset, int length,
                                                          int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, offset, length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, offset, length, options);
    }

    /**
     * automatically compute the inSampleSize when decode from resource
     * @param res
     * @param resId
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);}


    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float)height / (float)reqHeight);
            } else {
                inSampleSize = Math.round((float)width / (float)reqWidth);
            }
        }
        return inSampleSize;
    }

}
