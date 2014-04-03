package org.akita.widget.resimageview;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import org.akita.util.ImageUtil;
import org.akita.widget.ResImageView;

public class ResImageLoaderHandler extends Handler {

    public static final int HANDLER_MESSAGE_ID = 0;
    public static final String BITMAP_EXTRA = "akita:extra_bitmap";
    public static final String IMAGE_URL_EXTRA = "akita:extra_image_url";

    private ImageView imageView;
    private String imageUrl;
    private int errorDrawableRes;
    private int imgMaxWidth;
    private int imgMaxHeight;
    private int roundCornerPx;

    public ResImageLoaderHandler(ImageView imageView, String imageUrl, int errorDrawable,
                                 int imgMaxWidth, int imgMaxHeigtht, int roundCornerPx) {
        this.imageView = imageView;
        this.imageUrl = imageUrl;
        this.errorDrawableRes = errorDrawable;
        this.imgMaxWidth = imgMaxWidth;
        this.imgMaxHeight = imgMaxHeigtht;
        this.roundCornerPx = roundCornerPx;
    }

    @Override
    public final void handleMessage(Message msg) {
        if (msg.what == HANDLER_MESSAGE_ID) {
            handleImageLoadedMessage(msg);
        }
    }

    protected final void handleImageLoadedMessage(Message msg) {
        Bundle data = msg.getData();
        Bitmap bitmap = data.getParcelable(BITMAP_EXTRA);
        handleImageLoaded(bitmap, msg);
    }

    /**
     * Override this method if you need custom handler logic. Note that this method can actually be
     * called directly for performance reasons, in which case the message will be null
     * 
     * @param bitmap
     *            the bitmap returned from the remoteimageview loader
     * @param msg
     *            the handler message; can be null
     * @return true if the view was updated with the new remoteimageview, false if it was discarded
     */
    protected boolean handleImageLoaded(Bitmap bitmap, Message msg) {
        // If this handler is used for loading images in a ListAdapter,
        // the thread will set the remoteimageview only if it's the right position,
        // otherwise it won't do anything.
        String forUrl = (String) imageView.getTag();
        if (imageUrl.equals(forUrl)) {
            if (bitmap == null) {
                imageView.setImageBitmap(null);
                if (errorDrawableRes != 0)
                    imageView.setBackgroundResource(errorDrawableRes);
                else if (ResImageView.DEFAULT_ERROR_DRAWABLE_RES_ID != 0)
                    imageView.setBackgroundResource(ResImageView.DEFAULT_ERROR_DRAWABLE_RES_ID);
            } else {
                // add round corner
                if (roundCornerPx > 0 && roundCornerPx <= 100) {
                    bitmap = ImageUtil.getRoundedCornerBitmap(bitmap, roundCornerPx);
                }

                if (imgMaxWidth <= 0 && imgMaxHeight <= 0) {
                    imageView.setImageBitmap(bitmap);
                } else {
                    Bitmap scaledBitmap = ImageUtil.xform(bitmap, imgMaxWidth, imgMaxHeight);
                    imageView.setImageBitmap(scaledBitmap);
                }
            }

            // remove the remoteimageview URL from the view's tag
            imageView.setTag(null);

            return true;
        }

        return false;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }
}
