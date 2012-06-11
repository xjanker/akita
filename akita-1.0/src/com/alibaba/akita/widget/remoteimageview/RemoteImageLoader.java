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

package com.alibaba.akita.widget.remoteimageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import com.alibaba.akita.cache.AkCacheManager;
import com.alibaba.akita.cache.FilesCache;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Realizes a background remoteimageview loader that downloads an remoteimageview from a URL, optionally backed by a
 * two-level FIFO cache. If the remoteimageview to be loaded is present in the cache, it is set immediately on
 * the given view. Otherwise, a thread from a thread pool will be used to download the remoteimageview in the
 * background and set the remoteimageview on the view as soon as it completes.
 * 
 * @author Matthias Kaeppler
 */
public class RemoteImageLoader {

    // the default thread pool size
    private static final int DEFAULT_POOL_SIZE = 3;
    // expire images after a day
    // TODO: this currently only affects the in-memory cache, so it's quite pointless
    private static final int DEFAULT_TTL_MINUTES = 24 * 60;
    private static final int DEFAULT_NUM_RETRIES = 3;
    private static final int DEFAULT_BUFFER_SIZE = 65536;

    private static FilesCache<Bitmap> sImageCache;

    private ThreadPoolExecutor executor;
    private FilesCache<Bitmap> imageCache;
    private int numRetries = DEFAULT_NUM_RETRIES;
    private int defaultBufferSize = DEFAULT_BUFFER_SIZE;
    private long expirationInMinutes = DEFAULT_TTL_MINUTES;

    private Drawable defaultDummyDrawable, errorDrawable;

    public RemoteImageLoader(Context context) {
        this(context, true);
    }

    /**
     * Creates a new ImageLoader that is backed by an {@link FilesCache<Bitmap>}. The cache will by default
     * cache to the device's external storage, and expire images after 1 day. You can set useCache
     * to false and then supply your own remoteimageview cache instance via {@link #setImageCache(FilesCache<Bitmap>)}
     * , or fine-tune the default one through {@link #getImageCache()}.
     * 
     * @param context
     *            the current context
     * @param createCache
     *            whether to create a default {@link FilesCache<Bitmap>} used for caching
     */
    public RemoteImageLoader(Context context, boolean createCache) {
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(DEFAULT_POOL_SIZE);
        if (createCache) {
            if (sImageCache == null) {
                sImageCache = AkCacheManager.getImageFilesCache(context);
            }
            imageCache = sImageCache;
            /*imageCache.enableDiskCache(context.getApplicationContext(),
                    ImageCache.DISK_CACHE_SDCARD);*/
        }
        errorDrawable = context.getResources().getDrawable(android.R.drawable.ic_dialog_alert);
        defaultDummyDrawable = context.getResources().getDrawable(android.R.drawable.ic_menu_gallery);
    }

    /**
     * @param numThreads
     *            the maximum number of threads that will be started to download images in parallel
     */
    public void setThreadPoolSize(int numThreads) {
        executor.setMaximumPoolSize(numThreads);
    }

    /**
     * @param numAttempts
     *            how often the remoteimageview loader should retry the remoteimageview download if network connection
     *            fails
     */
    public void setMaxDownloadAttempts(int numAttempts) {
        numRetries = numAttempts;
    }

    /**
     * If the server you're loading images from does not report file sizes via the Content-Length
     * header, then you can use this method to tell the downloader how much space it should allocate
     * by default when downloading an remoteimageview into memory.
     * 
     * @param defaultBufferSize
     *            how big the buffer should be into which the remoteimageview file is read. This should be big
     *            enough to hold the largest remoteimageview you expect to download
     */
    public void setDefaultBufferSize(int defaultBufferSize) {
        this.defaultBufferSize = defaultBufferSize;
    }

    public void setDefaultDummyDrawable(Drawable drawable) {
        this.defaultDummyDrawable = drawable;
    }

    public void setDownloadFailedDrawable(Drawable drawable) {
        this.errorDrawable = drawable;
    }

    public void setImageCache(FilesCache<Bitmap> imageCache) {
        this.imageCache = imageCache;
    }

    /**
     * Clears the remoteimageview cache, if it's used. A good candidate for calling in
     * {@link android.app.Application#onLowMemory()}.
     */
    public void clearImageCache() {
        if (imageCache != null) {
            //imageCache.clearSome();
        }
    }

    /**
     * Returns the remoteimageview cache backing this remoteimageview loader.
     * 
     * @return the {@link FilesCache<Bitmap>}
     */
    public FilesCache<Bitmap> getImageCache() {
        return imageCache;
    }

    /**
     * Triggers the remoteimageview loader for the given remoteimageview and view. The remoteimageview loading will be performed
     * concurrently to the UI main thread, using a fixed size thread pool. The loaded remoteimageview will be
     * posted back to the given ImageView upon completion. This method will the default
     * {@link RemoteImageLoaderHandler} to process the bitmap after downloading it.
     * 
     * @param imageUrl
     *            the URL of the remoteimageview to download
     * @param imageView
     *            the ImageView which should be updated with the new remoteimageview
     */
    public void loadImage(String imageUrl, ImageView imageView) {
        loadImage(imageUrl, imageView, defaultDummyDrawable, new RemoteImageLoaderHandler(
                imageView, imageUrl, errorDrawable, 0, 0));
    }

    /**
     * Triggers the remoteimageview loader for the given remoteimageview and view. The remoteimageview loading will be performed
     * concurrently to the UI main thread, using a fixed size thread pool. The loaded remoteimageview will be
     * posted back to the given ImageView upon completion. This method will the default
     * {@link RemoteImageLoaderHandler} to process the bitmap after downloading it.
     *
     * @param imageUrl
     *            the URL of the remoteimageview to download
     * @param imageView
     *            the ImageView which should be updated with the new remoteimageview
     * @param dummyDrawable
     *            the Drawable to be shown while the remoteimageview is being downloaded.
     */
    public void loadImage(String imageUrl, ImageView imageView, Drawable dummyDrawable) {
        loadImage(imageUrl, imageView, dummyDrawable, new RemoteImageLoaderHandler(
                imageView, imageUrl, errorDrawable, 0 , 0));
    }

    /**
     * Triggers the remoteimageview loader for the given remoteimageview and view. The remoteimageview loading will be performed
     * concurrently to the UI main thread, using a fixed size thread pool. The loaded remoteimageview will be
     * posted back to the given ImageView upon completion.
     * 
     * @param imageUrl
     *            the URL of the remoteimageview to download
     * @param imageView
     *            the ImageView which should be updated with the new remoteimageview
     * @param handler
     *            the handler that will process the bitmap after completion
     */
    public void loadImage(String imageUrl, ImageView imageView, RemoteImageLoaderHandler handler) {
        loadImage(imageUrl, imageView, defaultDummyDrawable, handler);
    }

    /**
     * Triggers the remoteimageview loader for the given remoteimageview and view. The remoteimageview loading will be performed
     * concurrently to the UI main thread, using a fixed size thread pool. The loaded remoteimageview will be
     * posted back to the given ImageView upon completion. While waiting, the dummyDrawable is
     * shown.
     * 
     * @param imageUrl
     *            the URL of the remoteimageview to download
     * @param imageView
     *            the ImageView which should be updated with the new remoteimageview
     * @param dummyDrawable
     *            the Drawable to be shown while the remoteimageview is being downloaded.
     * @param handler
     *            the handler that will process the bitmap after completion
     */
    public void loadImage(String imageUrl, ImageView imageView, Drawable dummyDrawable,
            RemoteImageLoaderHandler handler) {
        if (imageView != null) {
            if (imageUrl == null) {
                // In a ListView views are reused, so we must be sure to remove the tag that could
                // have been set to the ImageView to prevent that the wrong remoteimageview is set.
                imageView.setTag(null);
                if (dummyDrawable != null) {
                    imageView.setImageDrawable(dummyDrawable);
                }
                return;
            }
            String oldImageUrl = (String) imageView.getTag();
            if (imageUrl.equals(oldImageUrl)) {
                // nothing to do
                return;
            } else {
                if (dummyDrawable != null) {
                    // Set the dummy remoteimageview while waiting for the actual remoteimageview to be downloaded.
                    imageView.setImageDrawable(dummyDrawable);
                }
                imageView.setTag(imageUrl);
            }
        }

        if (imageCache != null) {
            // do not go through message passing, handle directly instead
            Bitmap bm = imageCache.get(imageUrl);
            if (bm != null) {
                handler.handleImageLoaded(bm, null);
            } else {
                executor.execute(new RemoteImageLoaderJob(imageUrl, handler, imageCache, numRetries,
                        defaultBufferSize));
            }
        }
    }
}
