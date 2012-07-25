package com.alibaba.akita.widget.remoteimageview;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import com.alibaba.akita.cache.FilesCache;
import com.alibaba.akita.exception.AkException;
import com.alibaba.akita.io.HttpInvoker;

public class RemoteImageLoaderJob implements Runnable {

    private static final String TAG = "Ignition/ImageLoader";

    private String imageUrl;
    private String httpReferer;
    private RemoteImageLoaderHandler handler;
    private FilesCache<Bitmap> imageCache;
    private int numRetries, defaultBufferSize;

    public RemoteImageLoaderJob(String imageUrl, String httpReferer,
                                RemoteImageLoaderHandler handler, FilesCache<Bitmap> imageCache,
                                int numRetries, int defaultBufferSize) {
        this.imageUrl = imageUrl;
        this.httpReferer = httpReferer;
        this.handler = handler;
        this.imageCache = imageCache;
        this.numRetries = numRetries;
        this.defaultBufferSize = defaultBufferSize;
    }

    /**
     * The job method run on a worker thread. It will first query the remoteimageview cache, and on a miss,
     * download the remoteimageview from the Web.
     */
    @Override
    public void run() {
        Bitmap bitmap = null;

        if (imageCache != null) {
            // at this point we know the remoteimageview is not in memory, but it could be cached to SD card
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
            Bitmap bm = HttpInvoker.getBitmapFromUrl(imageUrl, httpReferer);
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
