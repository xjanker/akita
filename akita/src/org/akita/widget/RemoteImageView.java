package org.akita.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ViewSwitcher;
import org.akita.util.AndroidUtil;
import org.akita.util.Log;
import org.akita.widget.common.ImageView_;
import org.akita.widget.remoteimageview.RemoteImageLoader;
import org.akita.widget.remoteimageview.RemoteImageLoaderHandler;


/**
 * An remoteimageview view that fetches its remoteimageview off the web using the supplied URL. While the remoteimageview is being
 * downloaded, a progress indicator will be shown. The following attributes are supported:
 * <ul>
 * <li>android:src (Drawable) -- The default/placeholder remoteimageview that is shown if no remoteimageview can be
 * downloaded, or before the remoteimageview download starts (see {@link android.R.attr#src})
 * <li>android:indeterminateDrawable (Drawable) -- The progress drawable to use while the remoteimageview is
 * being downloaded (see {@link android.R.attr#indeterminateDrawable})</li>
 * <li>ignition:imageUrl (String) -- The URL at which the remoteimageview is found online</li>
 * <li>ignition:autoLoad (Boolean) -- Whether the download should start immediately after view
 * inflation</li>
 * <li>ignition:errorDrawable (Drawable) -- The drawable to display if the remoteimageview download fails</li>
 * </ul>
 * 
 * @author Matthias Kaeppler original.
 * @author Justin Yang modified.
 *
 */
public class RemoteImageView extends ViewSwitcher {
    private static final String TAG = "akita.RemoteImageView";

    public static int DEFAULT_ERROR_DRAWABLE_RES_ID = 0;

    private String imageUrl;
    private String httpReferer;

    /**
     * remoteimageview real Width in px
     * wrap_content (<=0)
     */
    private int imgBoxWidth = 0;
    /**
     * remoteimageview real Height in px
     * wrap_content (<=0)
     */
    private int imgBoxHeight = 0;
    /**
     * 0: no round corner
     * >0: round corner px size
     */
    private int roundCornerPx = 0;
    /**
     * true的时候每次都会从网络下载，并且不Cache到本地
     */
    private boolean noCache = false;

    /**
     * if true, then use PinchZoomImageView instead.
     */
    private boolean pinchZoom;
    /**
     * fade in
     */
    private boolean fadeIn;
    /**
     * show exact progress
     */
    private boolean showProgress;

    private boolean autoLoad, isLoaded;

    /**
     * NOT USED YET
     */
    private int defaultImgRes = 0;
    /**
     * USED
     */
    private int defaultBgRes = 0;
    /**
     * NOT USED YET
     */
    private int errorImgRes = 0;
    private int errorBgRes = 0;
    private Drawable indeterminateDrawable;

    private ProgressBar loadingSpinner;
    private ImageView_ imageView;

    private RemoteImageLoader imageLoader;
    private static RemoteImageLoader sharedImageLoader;

    /**
     * Use this method to inject an remoteimageview loader that will be shared across all instances of this
     * class. If the shared reference is null, a new {@link RemoteImageLoader} will be instantiated
     * for every instance of this class.
     * 
     * @param imageLoader
     *            the shared remoteimageview loader
     */
    public static void setSharedImageLoader(RemoteImageLoader imageLoader) {
        sharedImageLoader = imageLoader;
    }

    /**
     * @param context
     *            the view's current context
     * @param imageUrl
     *            the URL of the remoteimageview to download and show
     * @param autoLoad
     *            Whether the download should start immediately after creating the view. If set to
     *            false, use {@link #loadImage()} to manually trigger the remoteimageview download.
     */
    public RemoteImageView(Context context, String imageUrl, boolean autoLoad,
                           boolean fadeIn, boolean pinchZoom, boolean showProgress) {
        super(context);
        this.imageUrl = imageUrl;
        this.autoLoad = autoLoad;
        this.fadeIn = fadeIn;
        this.pinchZoom = pinchZoom;
        this.showProgress = showProgress;
        initialize(context, null);
    }

    /**
     * @param context
     *            the view's current context
     * @param imageUrl
     *            the URL of the remoteimageview to download and show
     * @param progressDrawable
     *            the drawable to be used for the {@link android.widget.ProgressBar} which is displayed while the
     *            remoteimageview is loading
     * @param errorBgRes
     *            the drawable to be used if a download error occurs
     * @param autoLoad
     *            Whether the download should start immediately after creating the view. If set to
     *            false, use {@link #loadImage()} to manually trigger the remoteimageview download.
     */
    public RemoteImageView(Context context, String imageUrl, Drawable progressDrawable,
                           int errorBgRes, boolean autoLoad, boolean fadeIn,
                           boolean pinchZoom, boolean showProgress) {
        super(context);
        this.imageUrl = imageUrl;
        this.indeterminateDrawable = progressDrawable;
        this.errorBgRes = errorBgRes;
        this.autoLoad = autoLoad;
        this.fadeIn = fadeIn;
        this.pinchZoom = pinchZoom;
        this.showProgress = showProgress;
        initialize(context, null);
    }

    public RemoteImageView(Context context, AttributeSet attributes) {
        super(context, attributes);

        TypedArray a = context.getTheme().obtainStyledAttributes(attributes,
                AndroidUtil.getResourceDeclareStyleableIntArray(context, "RemoteImageView"), 0, 0);
        imageUrl = a.getString(AndroidUtil.getStyleableResourceInt(context, "RemoteImageView_imageUrl"));
        autoLoad = a.getBoolean(AndroidUtil.getStyleableResourceInt(context, "RemoteImageView_autoLoad"), false);
        fadeIn = a.getBoolean(AndroidUtil.getStyleableResourceInt(context, "RemoteImageView_fadeIn"), false);
        imgBoxHeight = (int)a.getDimension(AndroidUtil.getStyleableResourceInt(context, "RemoteImageView_imgBoxHeight"), 0.0f);
        imgBoxWidth = (int)a.getDimension(AndroidUtil.getStyleableResourceInt(context, "RemoteImageView_imgBoxWidth"), 0.0f);
        roundCornerPx = (int)a.getDimension(AndroidUtil.getStyleableResourceInt(context, "RemoteImageView_roundCorner"), 0.0f);
        noCache = a.getBoolean(AndroidUtil.getStyleableResourceInt(context, "RemoteImageView_noCache"), false);
        defaultImgRes = a.getResourceId(AndroidUtil.getStyleableResourceInt(context, "RemoteImageView_defaultImgRes"), 0);
        defaultBgRes = a.getResourceId(AndroidUtil.getStyleableResourceInt(context, "RemoteImageView_defaultBgRes"), 0);
        errorImgRes = a.getResourceId(AndroidUtil.getStyleableResourceInt(context, "RemoteImageView_errorImgRes"), 0);
        errorBgRes = a.getResourceId(AndroidUtil.getStyleableResourceInt(context, "RemoteImageView_errorBgRes"), 0);
        noCache = a.getBoolean(AndroidUtil.getStyleableResourceInt(context, "RemoteImageView_noCache"), false);
        pinchZoom = a.getBoolean(AndroidUtil.getStyleableResourceInt(context, "RemoteImageView_pinchZoom"), false);
        showProgress = a.getBoolean(AndroidUtil.getStyleableResourceInt(context, "RemoteImageView_showProgress"), false);
        int indeterminateDrawableRes = a.getResourceId(
                AndroidUtil.getStyleableResourceInt(context, "RemoteImageView_indeterminateDrawable"), 0);
        if (indeterminateDrawableRes != 0) {
            indeterminateDrawable = context.getResources().getDrawable(indeterminateDrawableRes);
        }
        a.recycle();

        initialize(context, attributes);
    }

    private void initialize(Context context, AttributeSet attributes) {

        if (sharedImageLoader == null) {
            this.imageLoader = new RemoteImageLoader(context);
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

        addLoadingSpinnerView(context);
        addImageView(context, attributes);

        if (autoLoad && imageUrl != null) {
            loadImage();
        } else {
            // if we don't have anything to load yet, don't show the progress element
            setDisplayedChild(1);
        }
    }

    private void addLoadingSpinnerView(Context context) {
        LayoutParams lp;

        if (showProgress) {
            loadingSpinner = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
            lp = new LayoutParams(AndroidUtil.dp2px(context, 36), AndroidUtil.dp2px(context, 36));
            lp.gravity = Gravity.CENTER;
        } else {
            loadingSpinner = new ProgressBar(context);
            loadingSpinner.setIndeterminate(true);
            if (this.indeterminateDrawable == null) {
                this.indeterminateDrawable = loadingSpinner.getIndeterminateDrawable();
            } else {
                loadingSpinner.setIndeterminateDrawable(indeterminateDrawable);
                if (indeterminateDrawable instanceof AnimationDrawable) {
                    ((AnimationDrawable) indeterminateDrawable).start();
                }
            }

            lp = new LayoutParams(indeterminateDrawable.getIntrinsicWidth(),
                    indeterminateDrawable.getIntrinsicHeight());
            lp.gravity = Gravity.CENTER;
        }

        addView(loadingSpinner, 0, lp);
    }

    private void addImageView(final Context context, AttributeSet attributes) {
        if (pinchZoom) {
            if (attributes != null) {
                // pass along any view attribtues inflated from XML to the remoteimageview view
                imageView = new PinchZoomImageView(context, attributes);
            } else {
                imageView = new PinchZoomImageView(context);
            }
        } else {
            if (attributes != null) {
                // pass along any view attribtues inflated from XML to the remoteimageview view
                imageView = new ImageView_(context, attributes);
            } else {
                imageView = new ImageView_(context);
            }
        }

        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER;
        addView(imageView, 1, lp);
    }

    public void setScaleType(ImageView.ScaleType scaleType) {
        if (imageView != null) {
            imageView.setScaleType(scaleType);
        }
    }

    public void setDefaultBgRes(int defaultBgRes) {
        if (imageLoader != null) {
            imageLoader.setDefaultBgRes(defaultBgRes);
        }
    }

    /**
     * Use this method to trigger the remoteimageview download if you had previously set autoLoad to false.
     */
    public void loadImage() {
        if (imageUrl == null) {
            Exception e = new IllegalStateException(
                    "remoteimageview URL is null; did you forget to set it for this view?");
            Log.e(TAG, e.toString(), e);
            return;
        }


        if (showProgress) {
            loadingSpinner.setProgress(0);
            imageLoader.loadImage(imageUrl, httpReferer, noCache, loadingSpinner, imageView,
                    defaultBgRes,
                    new DefaultImageLoaderHandler(imgBoxWidth, imgBoxHeight, roundCornerPx));
            setDisplayedChild(0);
        } else {
            imageLoader.loadImage(imageUrl, httpReferer, noCache, null, imageView,
                    defaultBgRes,
                    new DefaultImageLoaderHandler(imgBoxWidth, imgBoxHeight, roundCornerPx));
            setDisplayedChild(1);
        }
    }

    /**
     * reset dummy image
     */
    public void resetDummyImage() {
        imageView.setImageResource(android.R.drawable.ic_menu_gallery);
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
     * @param imgMaxWidth
     * @param imgMaxHeight
     */
    public void setImageBoxSize(int imgMaxWidth, int imgMaxHeight) {
        this.imgBoxWidth = imgMaxWidth;
        this.imgBoxHeight = imgMaxHeight;
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
        } catch (OutOfMemoryError ooe) {
            Log.e(TAG, ooe.toString(), ooe);
        }

        setDisplayedChild(1);
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
        setDisplayedChild(1);
    }

    @Override
    public void reset() {
        super.reset();
        this.setDisplayedChild(0);
    }

    /**
     * 一般不需要调用，会自动释放。
     * 建议对于setImageBoxSize后的riv，在页面onDestroy时调用。
     * 注：此方法对于返回的上一页面中有相同图片的不适用，慎用
     */
    public void release() {
        if (imageView == null) return;

        Drawable drawable = imageView.getDrawable();
        if (drawable != null && drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
            if(!bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }

        imageView.setImageBitmap(null);
    }

    private class DefaultImageLoaderHandler extends RemoteImageLoaderHandler {

        public DefaultImageLoaderHandler(int imgMaxWidth, int imgMaxHeight, int roundCornerPx) {
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
                setDisplayedChild(1);
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
     * to the view attribute ignition:autoLoad (default: true).
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
     * The progress bar that is shown while the remoteimageview is loaded.
     *
     * @return the {@link android.widget.ProgressBar}
     */
    public ProgressBar getProgressBar() {
        return loadingSpinner;
    }

    /**
     * 图片加载完成时，可以监听到，从而拿到图片的信息，譬如大小宽高等。
     */
    private OnImageLoadedListener onImageLoadedListener;
    public void setOnLoadOverListener(OnImageLoadedListener onImageLoadedListener) {
        this.onImageLoadedListener = onImageLoadedListener;
    }
    public interface OnImageLoadedListener{
        void onImageLoaded(Bitmap bitmap);
    }

}
