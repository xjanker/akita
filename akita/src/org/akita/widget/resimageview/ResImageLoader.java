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

package org.akita.widget.resimageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import org.akita.cache.AkCacheManager;
import org.akita.cache.FilesCache;

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
public class ResImageLoader {

    // the default thread pool size
    private static final int DEFAULT_POOL_SIZE = 3;
    private static FilesCache<Bitmap> sImageCache;
    private ThreadPoolExecutor executor;
    private FilesCache<Bitmap> imageCache;

    public ResImageLoader(Context context) {
        this(context, true);
    }

    /**
     * Creates a new ImageLoader that is backed by an {@link org.akita.cache.FilesCache< android.graphics.Bitmap>}. The cache will by default
     * cache to the device's external storage, and expire images after 1 day. You can set useCache
     * to false and then supply your own remoteimageview cache instance via {@link #setImageCache(org.akita.cache.FilesCache< android.graphics.Bitmap>)}
     * , or fine-tune the default one through {@link #getImageCache()}.
     *
     * @param context
     *            the current context
     * @param createCache
     *            whether to create a default {@link org.akita.cache.FilesCache< android.graphics.Bitmap>} used for caching
     */
    public ResImageLoader(Context context, boolean createCache) {
        if ( Runtime.getRuntime() != null && Runtime.getRuntime().availableProcessors() <= 1) {
            executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        } else {
            executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(DEFAULT_POOL_SIZE);
        }
        if (createCache) {
            if (sImageCache == null) {
                sImageCache = AkCacheManager.getResImageFilesCache(context);
            }
            imageCache = sImageCache;
        }
    }

    /**
     * @param numThreads
     *            the maximum number of threads that will be started to download images in parallel
     */
    public void setThreadPoolSize(int numThreads) {
        executor.setMaximumPoolSize(numThreads);
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
     * @return the {@link org.akita.cache.FilesCache< android.graphics.Bitmap>}
     */
    public FilesCache<Bitmap> getImageCache() {
        return imageCache;
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
     * @param handler
     *            the handler that will process the bitmap after completion
     */
    public void loadImage(String imageUrl, String httpReferer, boolean noCache, ProgressBar progressBar, ImageView imageView,
                          ResImageLoaderHandler handler) {
        if (imageView != null) {
            if (imageUrl == null) {
                // In a ListView views are reused, so we must be sure to remove the tag that could
                // have been set to the ImageView to prevent that the wrong remoteimageview is set.
                imageView.setTag(null);

                return;
            }
            String oldImageUrl = (String) imageView.getTag();
            if (imageUrl.equals(oldImageUrl)) {
                // nothing to do
                return;
            } else {
                imageView.setTag(imageUrl);
            }
        }

        if (noCache) {
            // do not use cache, download image every time by passing the null value of imageCache
            executor.execute(new ResImageLoaderJob(imageUrl, httpReferer, progressBar, handler,
                    null));
        } else if (imageCache != null) {
            // do not go through message passing, handle directly instead
            Bitmap bm = imageCache.get(imageUrl);
            if (bm != null) {
                handler.handleImageLoaded(bm, null);
            } else {
                executor.execute(new ResImageLoaderJob(imageUrl, httpReferer, progressBar, handler,
                        imageCache));
            }
        }
    }
}
