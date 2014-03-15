package com.alibaba.akita.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.ViewSwitcher;
import com.alibaba.akita.R;
import com.alibaba.akita.util.Log;
import com.alibaba.akita.widget.common.ImageView_;
import com.alibaba.akita.widget.resimageview.ResImageLoader;
import com.alibaba.akita.widget.resimageview.ResImageLoaderHandler;


/**
 * A ResImageView is used in resource-type remote image retrieve and display.
 * <p>
 * use xmlns:akita-auto="http://schemas.android.com/apk/res-auto" <br/>
 * to add layout params.
 * </p>
 * <p>
 * eg.
 * </p>
 * <pre class="prettyprint">
 * &lt;com.alibaba.akita.widget.ResImageView
 *     android:id="@+id/resiv_main"
 *     android:layout_width="100dp"
 *     android:layout_height="100dp"
 *     akita-auto:noCache="true"
 *     akita-auto:errorBgRes="@drawable/ic_launcher"
 *     akita-auto:defaultImgRes="@drawable/ic_launcher"
 * /&gt;
 * </pre>
 *
 * @author Justin Yang
 */
public class ResImageView extends ViewSwitcher {
    private static final String TAG = "akita.ResImageView";

    public static final int DEFAULT_ERROR_DRAWABLE_RES_ID = R.drawable.ic_akita_alert;

    /**
     * image url
     */
    private String imageUrl;
    /**
     * httpReferer
     */
    private String httpReferer;
    /**
     * remoteimageview real Width in px
     * wrap_content (<=0)
     */
    private int imgBoxWidthPx = 0;
    /**
     * remoteimageview real Height in px
     * wrap_content (<=0)
     */
    private int imgBoxHeightPx = 0;
    /**
     * 0: no round corner
     * >0: round corner px size
     */
    private int roundCornerPx = 0;
    /**
     * 是否不使用图片cache，
     * true的时候每次都会从网络下载，并且不Cache到本地
     */
    private boolean noCache = false;
    /**
     * fade in
     */
    private boolean fadeIn;
    /**
     * autoLoad
     */
    private boolean autoLoad = true;
    /**
     * isLoaded
     */
    private boolean isLoaded = false;

    private int defaultImgRes = 0;
    private int defaultBgRes = 0;
    /**
     * 暂时没用到
     */
    private int errorImgRes = 0;
    private int errorBgRes = 0;

    private ImageView_ imageView;
    private ResImageLoader imageLoader;
    private static ResImageLoader sharedImageLoader;

    /**
     * Use this method to inject an remoteimageview loader that will be shared across all instances of this
     * class. If the shared reference is null, a new {@link com.alibaba.akita.widget.remoteimageview.RemoteImageLoader} will be instantiated
     * for every instance of this class.
     *
     * @param imageLoader
     *            the shared remoteimageview loader
     */
    public static void setSharedImageLoader(ResImageLoader imageLoader) {
        sharedImageLoader = imageLoader;
    }

    public ResImageView(Context context, AttributeSet attributes) {
        super(context, attributes);

        TypedArray a = context.getTheme().obtainStyledAttributes(attributes, R.styleable.ResImageView, 0, 0);
        imageUrl = a.getString(R.styleable.ResImageView_imageUrl);
        autoLoad = a.getBoolean(R.styleable.ResImageView_autoLoad, false);
        fadeIn = a.getBoolean(R.styleable.ResImageView_fadeIn, false);
        imgBoxHeightPx = (int)a.getDimension(R.styleable.ResImageView_imgBoxHeight, 0.0f);
        imgBoxWidthPx = (int)a.getDimension(R.styleable.ResImageView_imgBoxWidth, 0.0f);
        roundCornerPx = (int)a.getDimension(R.styleable.ResImageView_roundCorner, 0.0f);
        noCache = a.getBoolean(R.styleable.ResImageView_noCache, false);
        defaultImgRes = a.getResourceId(R.styleable.ResImageView_defaultImgRes, 0);
        defaultBgRes = a.getResourceId(R.styleable.ResImageView_defaultBgRes, 0);
        errorImgRes = a.getResourceId(R.styleable.ResImageView_errorImgRes, 0);
        errorBgRes = a.getResourceId(R.styleable.ResImageView_errorBgRes, 0);
        a.recycle();

        initialize(context, imageUrl, autoLoad, fadeIn, attributes);
    }

    private void initialize(Context context, String imageUrl, boolean autoLoad, boolean fadeIn,
                            AttributeSet attributes) {
        this.imageUrl = imageUrl;
        this.autoLoad = autoLoad;
        this.fadeIn = fadeIn;
        if (sharedImageLoader == null) {
            this.imageLoader = new ResImageLoader(context);
        } else {
            this.imageLoader = sharedImageLoader;
        }

        // ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
        // 125.0f, preferredItemHeight / 2.0f);
        // anim.setDuration(500L);

        if (fadeIn) {
            AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setDuration(500L);
            setInAnimation(anim);
        }

        addImageView(context, attributes);

        if (autoLoad && imageUrl != null) {
            loadImage();
        }
    }

    private void addImageView(final Context context, AttributeSet attributes) {
        if (attributes != null) {
            // pass along any view attribtues inflated from XML to the remoteimageview view
            imageView = new ImageView_(context, attributes);
        } else {
            imageView = new ImageView_(context);
        }

        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER;
        addView(imageView, 0, lp);
    }

    public void setScaleType(ImageView.ScaleType scaleType) {
        if (imageView != null) {
            imageView.setScaleType(scaleType);
        }
    }

    /**
     * Use this method to trigger the resimageview download if you had previously set autoLoad to false.
     */
    public void loadImage() {
        if (imageUrl == null) {
            Exception e = new IllegalStateException(
                    "resimageview URL is null; did you forget to set it for this view?");
            Log.e(TAG, e.toString(), e);
            return;
        }

        // set default img and bg
        if (defaultImgRes != 0)
            imageView.setImageResource(defaultImgRes);
        if (defaultBgRes != 0)
            imageView.setBackgroundResource(defaultBgRes);

        imageLoader.loadImage(imageUrl, httpReferer, noCache, null, imageView,
                new DefaultImageLoaderHandler(errorBgRes, imgBoxWidthPx, imgBoxHeightPx, roundCornerPx));
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    /**
     * set the url of remote image.
     * use this method, then call loadImage().
     * @param imageUrl
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * to that kind of image which must be filled with referring url
     * @param httpReferer referring url
     */
    public void setHttpReferer(String httpReferer) {
        this.httpReferer = httpReferer;
    }

    /**
     * Set noCache or not
     * @param noCache If true, use no cache every loading
     */
    public void setNoCache(boolean noCache) {
        this.noCache = noCache;
    }

    /**
     * Box size in px.
     * wrap_contant: <=0
     * Set it to scale the remoteimageview using this box
     * @param imgMaxWidthPx
     * @param imgMaxHeightPx
     */
    public void setImageBoxSize(int imgMaxWidthPx, int imgMaxHeightPx) {
        this.imgBoxWidthPx = imgMaxWidthPx;
        this.imgBoxHeightPx = imgMaxHeightPx;
    }

    /**
     * fadein when loaded
     * @param fadeIn
     */
    public void setFadeIn(boolean fadeIn) {
        this.fadeIn = fadeIn;
    }

    /**
     * round corner of the image in px
     * @param roundCornerPx
     */
    public void setRoundCornerPx(int roundCornerPx) {
        this.roundCornerPx = roundCornerPx;
    }

    /**
     * default image resouce before loaded
     * if not set, then no image set.
     * set it in loadImage();
     * @param defaultImgRes
     */
    public void setDefaultImageResouce(int defaultImgRes) {
        this.defaultImgRes = defaultImgRes;
    }

    /**
     * default background resouce before loaded
     * if not set, then no image set.
     * set it in loadImage();
     * @param defaultBgRes
     */
    public void setDefaultBackgroundResource(int defaultBgRes) {
        this.defaultBgRes = defaultBgRes;
    }

    /**
     * background resouce when have error
     * if not set, then default error image ic_akita_image_alert.png set.
     * @param errorBgRes
     */
    public void setErrorBackgroundResource(int errorBgRes) {
        this.errorBgRes = errorBgRes;
    }

    /**
     * Often you have resources which usually have an remoteimageview, but some don't. For these cases, use
     * this method to supply a placeholder drawable which will be loaded instead of a web remoteimageview.
     *
     * Use this method to set local image.
     *
     * @param imageResourceId
     *            the resource of the placeholder remoteimageview drawable
     */
    public void setLocalImage(int imageResourceId) {
        try {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageResourceId);
            imageView.setImageBitmap(bitmap);
            imageView.setTag(R.id.ll_griditem, bitmap);
        } catch (OutOfMemoryError ooe) {
            Log.e(TAG, ooe.toString(), ooe);
        }

        setDisplayedChild(0);
    }

    /**
     * Often you have resources which usually have an remoteimageview, but some don't. For these cases, use
     * this method to supply a placeholder bitmap which will be loaded instead of a web remoteimageview.
     *
     * Use this method to set local image.
     *
     * @param bitmap
     *            the bitmap of the placeholder remoteimageview drawable
     */
    public void setLocalImage(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
        setDisplayedChild(0);
    }

    /**
     * 对于setImageBoxSize后的riv，必须在页面onDestroy时调用。
     * 对于返回的上一页面中有相同
     */
    public void release() {
        Bitmap bitmap = (Bitmap) imageView.getTag(R.id.ll_griditem);
        if (bitmap != null && !bitmap.isRecycled()) bitmap.recycle();
    }

    private class DefaultImageLoaderHandler extends ResImageLoaderHandler {

        public DefaultImageLoaderHandler(int errorBgRes, int imgMaxWidth, int imgMaxHeight, int roundCornerPx) {
            super(imageView, imageUrl, errorBgRes, imgMaxWidth, imgMaxHeight, roundCornerPx);
        }

        @Override
        protected boolean handleImageLoaded(Bitmap bitmap, Message msg) {
            if(onImageLoadedListener != null ){
                onImageLoadedListener.onImageLoaded(bitmap);
            }
            boolean wasUpdated = super.handleImageLoaded(bitmap, msg);
            if (wasUpdated) {
                isLoaded = true;
                setDisplayedChild(0);
            }
            return wasUpdated;
        }
    }

    /**
     * Returns the URL of the remoteimageview to show. Corresponds to the view attribute ignition:imageUrl.
     *
     * @return the remoteimageview URL
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Whether or not the remoteimageview should be downloaded immediately after view inflation. Corresponds
     * to the view attribute akita:autoLoad (default: true).
     *
     * @return true if auto downloading of the remoteimageview is enabled
     */
    public boolean isAutoLoad() {
        return autoLoad;
    }

    /**
     * The remoteimageview view that will render the downloaded remoteimageview.
     *
     * @return the {@link android.widget.ImageView}
     */
    public ImageView getImageView() {
        return imageView;
    }

    /**
     * 图片加载完成时，可以监听到，从而拿到图片的信息，譬如大小宽高等。
     */
    private OnImageLoadedListener onImageLoadedListener;
    public void setOnImageLoadedListener(OnImageLoadedListener onImageLoadedListener) {
        this.onImageLoadedListener = onImageLoadedListener;
    }
    public interface OnImageLoadedListener{
        void onImageLoaded(Bitmap bitmap);
    }

}
