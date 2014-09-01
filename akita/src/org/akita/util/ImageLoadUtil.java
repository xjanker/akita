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
