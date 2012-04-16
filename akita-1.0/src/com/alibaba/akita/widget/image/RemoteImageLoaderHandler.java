package com.alibaba.akita.widget.image;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import com.alibaba.akita.R;
import com.alibaba.akita.util.ImageUtil;

public class RemoteImageLoaderHandler extends Handler {

    public static final int HANDLER_MESSAGE_ID = 0;
    public static final String BITMAP_EXTRA = "akita:extra_bitmap";
    public static final String IMAGE_URL_EXTRA = "akita:extra_image_url";

    private ImageView imageView;
    private String imageUrl;
    private Drawable errorDrawable;
    private int imgMaxWidth;
    private int imgMaxHeight;

    public RemoteImageLoaderHandler(ImageView imageView, String imageUrl, Drawable errorDrawable,
                                        int imgMaxWidth, int imgMaxHeigtht) {
        this.imageView = imageView;
        this.imageUrl = imageUrl;
        this.errorDrawable = errorDrawable;
        this.imgMaxWidth = imgMaxWidth;
        this.imgMaxHeight = imgMaxHeigtht;
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
     *            the bitmap returned from the image loader
     * @param msg
     *            the handler message; can be null
     * @return true if the view was updated with the new image, false if it was discarded
     */
    protected boolean handleImageLoaded(Bitmap bitmap, Message msg) {
        // If this handler is used for loading images in a ListAdapter,
        // the thread will set the image only if it's the right position,
        // otherwise it won't do anything.
        String forUrl = (String) imageView.getTag();
        if (imageUrl.equals(forUrl)) {
            if (bitmap == null)
                imageView.setImageDrawable(errorDrawable);
            else {
                Bitmap scaledBM = (Bitmap) imageView.getTag(R.id.ll_loading1);
                if (scaledBM != null) {
                    scaledBM.recycle();
                    imageView.setTag(R.id.ll_loading1, null);
                }
                if (imgMaxWidth <= 0 && imgMaxHeight <= 0) {
                    imageView.setImageBitmap(bitmap);
                } else {
                    Bitmap scaledBitmap = ImageUtil.xform(bitmap, imgMaxWidth, imgMaxHeight);
                    imageView.setImageBitmap(scaledBitmap);
                    imageView.setTag(R.id.ll_loading1, scaledBitmap);
                }
            }

            // remove the image URL from the view's tag
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
