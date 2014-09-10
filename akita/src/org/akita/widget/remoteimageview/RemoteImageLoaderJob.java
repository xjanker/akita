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
package org.akita.widget.remoteimageview;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.widget.ProgressBar;
import org.akita.cache.FilesCache;
import org.akita.exception.AkException;
import org.akita.io.HttpInvoker;

public class RemoteImageLoaderJob implements Runnable {

    private static final String TAG = "akita.RemoteImageLoaderJob";

    private String imageUrl;
    private String httpReferer;
    private ProgressBar progressBar;
    private RemoteImageLoaderHandler handler;
    private FilesCache<Bitmap> imageCache;

    public RemoteImageLoaderJob(String imageUrl, String httpReferer, ProgressBar progressBar,
                                RemoteImageLoaderHandler handler, FilesCache<Bitmap> imageCache ) {
        this.imageUrl = imageUrl;
        this.httpReferer = httpReferer;
        this.progressBar = progressBar;
        this.handler = handler;
        this.imageCache = imageCache;
    }

    /**
     * The job method run on a worker thread. It will first query the remoteimageview cache, and on a miss,
     * download the remoteimageview from the Web.
     */
    @Override
    public void run() {
        Bitmap bitmap = null;

        if (imageCache != null) {
            // at this point we want to know if the remote image has been cached in SD card or in memory.
            bitmap = imageCache.get(imageUrl);
        }

        if (bitmap == null) {
            bitmap = downloadImage();
        }

        notifyImageLoaded(imageUrl, bitmap);
    }

    // use HttpInvoker to handle
    protected Bitmap downloadImage() {
        try {
            Bitmap bm = HttpInvoker.getBitmapFromUrl(imageUrl, httpReferer, progressBar);
            if (imageCache != null && bm != null) {
                imageCache.put(imageUrl, bm);
            }
            return bm;
        } catch (AkException e) {
            return null;
        }
    }

    protected void notifyImageLoaded(String url, Bitmap bitmap) {
        Message message = new Message();
        message.what = RemoteImageLoaderHandler.HANDLER_MESSAGE_ID;
        Bundle data = new Bundle();
        data.putString(RemoteImageLoaderHandler.IMAGE_URL_EXTRA, url);
        Bitmap image = bitmap;
        data.putParcelable(RemoteImageLoaderHandler.BITMAP_EXTRA, image);
        message.setData(data);

        handler.sendMessage(message);
    }
}
