package org.akita.widget.resimageview;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.widget.ProgressBar;
import org.akita.cache.FilesCache;
import org.akita.exception.AkException;
import org.akita.io.HttpInvoker;

public class ResImageLoaderJob implements Runnable {

    private static final String TAG = "akita.RemoteImageLoaderJob";

    private String imageUrl;
    private String httpReferer;
    private ProgressBar progressBar;
    private ResImageLoaderHandler handler;
    private FilesCache<Bitmap> imageCache;

    public ResImageLoaderJob(String imageUrl, String httpReferer, ProgressBar progressBar,
                             ResImageLoaderHandler handler, FilesCache<Bitmap> imageCache) {
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
        message.what = ResImageLoaderHandler.HANDLER_MESSAGE_ID;
        Bundle data = new Bundle();
        data.putString(ResImageLoaderHandler.IMAGE_URL_EXTRA, url);
        Bitmap image = bitmap;
        data.putParcelable(ResImageLoaderHandler.BITMAP_EXTRA, image);
        message.setData(data);

        handler.sendMessage(message);
    }
}
