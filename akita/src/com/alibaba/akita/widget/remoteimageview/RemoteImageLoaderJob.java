package com.alibaba.akita.widget.remoteimageview;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.widget.ProgressBar;
import com.alibaba.akita.cache.FilesCache;
import com.alibaba.akita.exception.AkException;
import com.alibaba.akita.io.HttpInvoker;

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
