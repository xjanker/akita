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
package org.akita.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Message;
import android.widget.ImageView;
import org.akita.widget.remoteimageview.RemoteImageLoader;
import org.akita.widget.remoteimageview.RemoteImageLoaderHandler;

/**
 * 封装akita的RemotoImageLoader
 */
public class ImageLoadUtil {
    private static RemoteImageLoader sharedImageLoader;

    public static void loadImage(Context c, ImageView iv, String imageUrl, int errorBgRes) {
        if (null == sharedImageLoader) {
            sharedImageLoader = new RemoteImageLoader(c);
        }
        sharedImageLoader.loadImage(imageUrl, null, false, null, iv,
                new DefaultImageLoaderHandler(iv, imageUrl, errorBgRes, 0, 0, 0));
    }

    private static class DefaultImageLoaderHandler extends RemoteImageLoaderHandler {

        public DefaultImageLoaderHandler(ImageView iv, String imageUrl, int errorBgRes,
                                         int imgMaxWidth, int imgMaxHeight, int roundCornerPx) {
            super(iv, imageUrl, errorBgRes, imgMaxWidth, imgMaxHeight, roundCornerPx);
        }

        @Override
        protected boolean handleImageLoaded(Bitmap bitmap, Message msg) {
            boolean wasUpdated = super.handleImageLoaded(bitmap, msg);
            return wasUpdated;
        }
    }
}
