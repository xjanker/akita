package com.alibaba.akita.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ViewSwitcher;
import com.alibaba.akita.Akita;
import com.alibaba.akita.util.AndroidUtil;
import com.alibaba.akita.widget.remoteimageview.RemoteImageLoader;
import com.alibaba.akita.widget.remoteimageview.RemoteImageLoaderHandler;


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

    public static final int DEFAULT_ERROR_DRAWABLE_RES_ID = android.R.drawable.ic_dialog_alert;

    private static final String ATTR_AUTO_LOAD = "autoLoad";
    private static final String ATTR_IMAGE_URL = "imageUrl";
    private static final String ATTR_ERROR_DRAWABLE = "errorDrawable";
    private static final String ATTR_IMGBOX_WIDTH = "imgBoxWidth";
    private static final String ATTR_IMGBOX_HEIGHT = "imgBoxHeight";

    private static final int[] ANDROID_VIEW_ATTRS = { android.R.attr.indeterminateDrawable };
    private static final int ATTR_INDET_DRAWABLE = 0;

    private String imageUrl;
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

    private boolean autoLoad, isLoaded;

    private ProgressBar loadingSpinner;
    private PinchZoomImageView imageView;

    private Drawable progressDrawable, errorDrawable;

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
    public RemoteImageView(Context context, String imageUrl, boolean autoLoad) {
        super(context);
        initialize(context, imageUrl, null, null, autoLoad, null);
    }

    /**
     * @param context
     *            the view's current context
     * @param imageUrl
     *            the URL of the remoteimageview to download and show
     * @param progressDrawable
     *            the drawable to be used for the {@link android.widget.ProgressBar} which is displayed while the
     *            remoteimageview is loading
     * @param errorDrawable
     *            the drawable to be used if a download error occurs
     * @param autoLoad
     *            Whether the download should start immediately after creating the view. If set to
     *            false, use {@link #loadImage()} to manually trigger the remoteimageview download.
     */
    public RemoteImageView(Context context, String imageUrl, Drawable progressDrawable,
                           Drawable errorDrawable, boolean autoLoad) {
        super(context);
        initialize(context, imageUrl, progressDrawable, errorDrawable, autoLoad, null);
    }

    public RemoteImageView(Context context, AttributeSet attributes) {
        super(context, attributes);

        // Read all Android specific view attributes into a typed array first.
        // These are attributes that are specific to RemoteImageView, but which are not in the
        // ignition XML namespace.
        TypedArray imageViewAttrs = context.getTheme().obtainStyledAttributes(attributes,
                ANDROID_VIEW_ATTRS, 0, 0);
        int progressDrawableId = imageViewAttrs.getResourceId(ATTR_INDET_DRAWABLE, 0);
        imageViewAttrs.recycle();

        int errorDrawableId = attributes.getAttributeResourceValue(Akita.XMLNS,
                ATTR_ERROR_DRAWABLE, DEFAULT_ERROR_DRAWABLE_RES_ID);
        Drawable errorDrawable = context.getResources().getDrawable(errorDrawableId);

        Drawable progressDrawable = null;
        if (progressDrawableId > 0) {
            progressDrawable = context.getResources().getDrawable(progressDrawableId);
        }

        String imageUrl = attributes.getAttributeValue(Akita.XMLNS, ATTR_IMAGE_URL);
        boolean autoLoad = attributes
                .getAttributeBooleanValue(Akita.XMLNS, ATTR_AUTO_LOAD, true);

        imgBoxWidth = AndroidUtil.dp2px(context,
                attributes.getAttributeIntValue(Akita.XMLNS, ATTR_IMGBOX_WIDTH, 0) );
        imgBoxHeight = AndroidUtil.dp2px(context,
                attributes.getAttributeIntValue(Akita.XMLNS, ATTR_IMGBOX_HEIGHT, 0) );

        initialize(context, imageUrl, progressDrawable, errorDrawable, autoLoad, attributes);
    }

    private void initialize(Context context, String imageUrl, Drawable progressDrawable,
            Drawable errorDrawable, boolean autoLoad, AttributeSet attributes) {
        this.imageUrl = imageUrl;
        this.autoLoad = autoLoad;
        this.progressDrawable = progressDrawable;
        this.errorDrawable = errorDrawable;
        if (sharedImageLoader == null) {
            this.imageLoader = new RemoteImageLoader(context);
        } else {
            this.imageLoader = sharedImageLoader;
        }

        // ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
        // 125.0f, preferredItemHeight / 2.0f);
        // anim.setDuration(500L);

        // AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        // anim.setDuration(500L);
        // setInAnimation(anim);

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
        loadingSpinner = new ProgressBar(context);
        loadingSpinner.setIndeterminate(true);
        if (this.progressDrawable == null) {
            this.progressDrawable = loadingSpinner.getIndeterminateDrawable();
        } else {
            loadingSpinner.setIndeterminateDrawable(progressDrawable);
            if (progressDrawable instanceof AnimationDrawable) {
                ((AnimationDrawable) progressDrawable).start();
            }
        }

        LayoutParams lp = new LayoutParams(progressDrawable.getIntrinsicWidth(),
                progressDrawable.getIntrinsicHeight());
        lp.gravity = Gravity.CENTER;

        addView(loadingSpinner, 0, lp);
    }

    private void addImageView(final Context context, AttributeSet attributes) {
        if (attributes != null) {
            // pass along any view attribtues inflated from XML to the remoteimageview view
            imageView = new PinchZoomImageView(context, attributes);
        } else {
            imageView = new PinchZoomImageView(context);
        }

        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER;
        addView(imageView, 1, lp);
    }

    /**
     * Use this method to trigger the remoteimageview download if you had previously set autoLoad to false.
     */
    public void loadImage() {
        if (imageUrl == null) {
            throw new IllegalStateException(
                    "remoteimageview URL is null; did you forget to set it for this view?");
        }
        setDisplayedChild(0);
        imageLoader.loadImage(imageUrl, imageView, new DefaultImageLoaderHandler(imgBoxWidth, imgBoxHeight));
    }

    public void resetDummyImage() {
        imageView.setImageResource(android.R.drawable.ic_menu_gallery);
    }


    public boolean isLoaded() {
        return isLoaded;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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
     * @param imageResourceId
     *            the resource of the placeholder remoteimageview drawable
     */
    public void setNoImageDrawable(int imageResourceId) {
        imageView.setImageDrawable(getContext().getResources().getDrawable(imageResourceId));
        setDisplayedChild(1);
    }

    @Override
    public void reset() {
        super.reset();

        this.setDisplayedChild(0);
    }

    private class DefaultImageLoaderHandler extends RemoteImageLoaderHandler {

        public DefaultImageLoaderHandler(int imgMaxWidth, int imgMaxHeight) {
            super(imageView, imageUrl, errorDrawable, imgMaxWidth, imgMaxHeight);
        }

        @Override
        protected boolean handleImageLoaded(Bitmap bitmap, Message msg) {
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
     * The drawable that should be used to indicate progress while downloading the remoteimageview.
     * Corresponds to the view attribute ignition:progressDrawable. If left blank, the platform's
     * standard indeterminate progress drawable will be used.
     *
     * @return the progress drawable
     */
    public Drawable getProgressDrawable() {
        return progressDrawable;
    }

    /**
     * The drawable that will be shown when the remoteimageview download fails. Corresponds to the view
     * attribute ignition:errorDrawable. If left blank, a stock alert icon from the Android platform
     * will be used.
     *
     * @return the error drawable
     */
    public Drawable getErrorDrawable() {
        return errorDrawable;
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
}
