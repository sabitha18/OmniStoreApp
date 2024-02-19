package com.armada.storeapp.ui.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.armada.storeapp.R;

public class LoadingView {
    private RelativeLayout mRootLayout;
    private ViewGroup mTargetView;
    private View mBackground;
    private ProgressBar mProgressBar;
    private ImageView imgLoading;
    private RelativeLayout mRetryContainer;
    private Rect mMargins;
    private boolean mIsIndeterminate = true;
    private int mProgress = 0;

    @ColorInt
    private Integer mBackgroundColor;
    @ColorInt
    private Integer mProgressColor;


    private Context mContext;
    private boolean mTouchThroughDisabled = true;
    private TextView mRetryText;
    private TextView mRetryButton;
    //  private GifLoadingView mGifLoadingView;

    //private GifImageView mgiffy;
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return mTouchThroughDisabled;
        }
    };

    private LoadingView(ViewGroup target, Integer progCol, Integer backCol, boolean touchDisabled,
                        Rect margins, ProgressStyle style, boolean indeterminate, float hbarMargin, ViewGroup retryLayout) {
        this.mTargetView = target;
        this.mProgressColor = progCol;
        this.mBackgroundColor = backCol;
        this.mTouchThroughDisabled = touchDisabled;
        this.mMargins = margins;
        this.mContext = mTargetView.getContext();
        this.mIsIndeterminate = indeterminate;

        init(style, hbarMargin, retryLayout);
    }

    private void init(ProgressStyle style, final float marginPercentge, ViewGroup retryLayout) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        mRootLayout = (RelativeLayout) inflater.inflate(R.layout.rw_layout_progress, mTargetView, false);

        mBackground = mRootLayout.findViewById(R.id.rw_loadingview_background);
        mRetryContainer = mRootLayout.findViewById(R.id.rw_loadingview_retry_container);


/*        if (mProgressDialog == null)
            mProgressDialog = new TransparentProgressDialog(mContext);
        if (mProgressDialog.isShowing())
            mProgressDialog.dismiss();
        mProgressDialog.setCancelable(true);
        int dividerId = mProgressDialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
        View divider = mProgressDialog.findViewById(dividerId);
        divider.setBackgroundColor(mProgressDialog.getContext().getResources().getColor(R.color.transparent));
        mProgressDialog.show();*/

        imgLoading = mRootLayout.findViewById(R.id.imgLoading);
        //imageViewTarget = new GlideDrawableImageViewTarget(imgLoading);
      /*  mGifLoadingView = new GifLoadingView();
        mGifLoadingView.setImageResource(R.drawable.loading);
        mGifLoadingView.show(mContext.getFragmentManager(), "");*/

        //  Glide.with(mContext).load(R.raw.loading).into(imageViewTarget);

        //mgiffy=mRootLayout.findViewById(R.id.giffy);


        if (retryLayout == null)
            retryLayout = (ViewGroup) inflater.inflate(R.layout.rw_layout_loading_retry, mRetryContainer, false);

        mRetryContainer.addView(retryLayout, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mRetryText = retryLayout.findViewById(R.id.label);
        mRetryButton = retryLayout.findViewById(R.id.button);

        if (style == ProgressStyle.HORIZONTAL) {
            mProgressBar = mRootLayout.findViewById(R.id.rw_loadingview_progress2);


            mProgressBar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mProgressBar.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mProgressBar.getLayoutParams();
                    int m = ((int) (mRootLayout.getWidth() * marginPercentge));
                    lp.setMargins(m, 0, m, 0);

                    mProgressBar.setLayoutParams(lp);
                }
            });
        } else {
            mProgressBar = mRootLayout.findViewById(R.id.rw_loadingview_progress);

        }

        mProgressBar.setVisibility(View.VISIBLE);

        mRetryContainer.setVisibility(View.GONE);

        mRootLayout.setOnTouchListener(touchListener);

        if (mProgressColor != null)
            setProgressColor(mProgressColor);

        if (mBackgroundColor != null)
            setBackgroundColor(mBackgroundColor);

        if (mMargins != null)
            setCustomMargins(mMargins.left, mMargins.top, mMargins.right, mMargins.bottom);

        setIndeterminate(mIsIndeterminate);
    }

    /**
     * Sets a margin to the coloured mBackground (not the full view)
     *
     * @param left   left margin in pixels
     * @param top    top margin in pixels
     * @param right  right margin in pixels
     * @param bottom bottom margin in pixels
     */
    public void setCustomMargins(@Px int left, @Px int top, @Px int right, @Px int bottom) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mBackground.getLayoutParams();
        lp.setMargins(left, top, right, bottom);
        mBackground.setLayoutParams(lp);
    }

    /**
     * Same as {@link LoadingView#setCustomMargins(int, int, int, int)}.
     * '0' can be passed to indicate no margins.
     *
     * @param left   left margin dimension
     * @param top    top margin dimension
     * @param right  right margin dimension
     * @param bottom bottom margin dimension
     * @see LoadingView#setCustomMargins(int, int, int, int)
     */
    public void setCustomMarginDimensions(@DimenRes int left, @DimenRes int top, @DimenRes int right, @DimenRes int bottom) {
        Resources resources = mContext.getResources();

        int l = left > 0 ? resources.getDimensionPixelSize(left) : 0;
        int t = top > 0 ? resources.getDimensionPixelSize(top) : 0;
        int r = right > 0 ? resources.getDimensionPixelSize(right) : 0;
        int b = bottom > 0 ? resources.getDimensionPixelSize(bottom) : 0;

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mBackground.getLayoutParams();
        lp.setMargins(l, t, r, b);
        mBackground.setLayoutParams(lp);
    }

    /**
     * Enable or disable touching the views underneath the LoadingView. Disabled by default.
     *
     * @param enabled pass 'true' to allow touches and 'false' to disable
     */
    public void setTouchThroughEnabled(boolean enabled) {
        this.mTouchThroughDisabled = !enabled;
    }

    /**
     * Show the loadingview by adding it to the top of the given parent view or root
     * view of the given activity
     */
    public void show() {
        hide();
        mProgressBar.setVisibility(View.VISIBLE);
        // mgiffy.setImageResource(R.drawable.loading);
        // ((GifDrawable) mgiffy.getDrawable()).setLoopCount(0);
        //   ((GifDrawable) mgiffy.getDrawable()).start();
        mTargetView.addView(mRootLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mRetryContainer.setVisibility(View.GONE);


        //  mProgressDialog.show();
    }

    /**
     * Remove the displayed loadingview
     */
    public void hide() {
        //mProgressDialog.dismiss();
       /* if ((GifDrawable) mgiffy.getDrawable() != null) {

            ((GifDrawable) mgiffy.getDrawable()).stop();
            ((GifDrawable) mgiffy.getDrawable()).recycle();
        }*/

        mTargetView.removeView(mRootLayout);
    }

    /**
     * Sets the color of the loadingview mBackground
     *
     * @param color mBackground color
     */
    public void setBackgroundColor(@ColorInt int color) {
        mBackground.setBackgroundColor(color);
    }

    /**
     * Sets the mBackground color
     *
     * @param color mBackground color resource
     */
    public void setBackgroundColorResource(@ColorRes int color) {
        mBackground.setBackgroundColor(ContextCompat.getColor(mContext, color));
    }

    /**
     * Resets the mBackground color to the default.
     * Default color is {@link Color#WHITE} or what was passed in the constructor
     */
    public void resetBackgroundColor() {
        mBackground.setBackgroundColor(Color.TRANSPARENT);
    }

    /**
     * sets the mProgressBar spinner color
     *
     * @param color mProgressBar color
     */
    public void setProgressColor(@ColorInt int color) {
        if (color == -1)
            resetProgressColor();
        else {
            Drawable drawable = mProgressBar.getIndeterminateDrawable();
            drawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        }
    }

    /**
     * sets the mProgressBar spinner color
     *
     * @param color mProgressBar color resource
     */
    public void setProgressColorResource(@ColorRes int color) {
        setProgressColor(ContextCompat.getColor(mContext, color));
    }

    /**
     * resets the mProgressBar spinner color to the default.
     * Default is {@link Color#RED} or what was passed in via the constructor
     */
    public void resetProgressColor() {
        Drawable drawable = mProgressBar.getIndeterminateDrawable();
        drawable.clearColorFilter();
    }

    /**
     * same as {@link ProgressBar#setIndeterminate(boolean)}
     *
     * @param indeterminate
     */
    public void setIndeterminate(boolean indeterminate) {
        mProgressBar.setIndeterminate(indeterminate);
        mIsIndeterminate = indeterminate;
    }

    /**
     * same as {@link ProgressBar#setProgress(int)}
     *
     * @param progress
     */
    public void setProgress(int progress) {
        mProgressBar.setProgress(progress);
        mProgress = progress;
    }

    public void setLoadingFailed(final OnRefreshClickListener listener) {
        setLoadingFailed(null, null, listener);
    }

    public void setLoadingFailed(@Nullable String message, @Nullable String buttonText, final OnRefreshClickListener listener) {
        mProgressBar.setVisibility(View.GONE);
        mRetryContainer.setVisibility(View.VISIBLE);

        if (message == null)
            message = mContext.getString(R.string.load_failed);

        if (buttonText == null)
            buttonText = mContext.getString(R.string.retry_btn);

        mRetryText.setText(message);
        mRetryButton.setText(buttonText);

        mRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onRefreshClicked();

                mRetryContainer.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    public enum ProgressStyle {
        HORIZONTAL, CYCLIC
    }

    public interface OnRefreshClickListener {
        void onRefreshClicked();
    }

    public static class Builder {
        private Integer mBckClr;
        private Integer mProgClr;
        private boolean mTouchDsbld = true;
        private ViewGroup mTarget;
        private Context ctx;
        private Rect mMrgns;
        private boolean isParent = false;
        private ProgressStyle mStyl = ProgressStyle.CYCLIC;
        private boolean mIndtrmnt = true;
        private float mHBarMarginPer = 0.1f;
        private ViewGroup mRtryLyt;
        private int statusbar_height;
        private int top_margin;


        public Builder(Context c, boolean is_parent) {
            this.ctx = c;
            this.isParent = is_parent;
            statusbar_height = getStatusBarHeight();/*+(int)ctx.getResources().getDimension(R.dimen.subtitle_desc_margin);*/
            top_margin = (int) ctx.getResources().getDimension(R.dimen.abc_action_bar_default_height_material) + (isParent ? (int) statusbar_height : (int) ctx.getResources().getDimension(R.dimen.corner_radius));
        }


        public int getStatusBarHeight() {
            int result = 0;
            int resourceId = ctx.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = ctx.getResources().getDimensionPixelSize(resourceId);
            }
            return result;
        }

        public Builder setProgressColor(@ColorInt int color) {
            mProgClr = color;
            return this;
        }

        public Builder setProgressColorResource(@ColorRes int color) {
            mProgClr = ContextCompat.getColor(ctx, color);
            return this;
        }

        public Builder setBackgroundColor(@ColorInt int color) {
            mBckClr = color;
            return this;
        }

        public Builder setBackgroundColorRes(@ColorRes int color) {
            mBckClr = ContextCompat.getColor(ctx, color);
            return this;
        }


        public Builder setCustomMargins(@Px int left, @Px int top, @Px int right, @Px int bottom) {
            mMrgns = new Rect(left, top_margin, right, bottom);
            return this;
        }

        public Builder setProgressStyle(ProgressStyle style) {
            mStyl = style;
            return this;
        }

        public Builder setIndeterminate(boolean indeterminate) {
            mIndtrmnt = indeterminate;
            return this;
        }

        public Builder setCustomRetryLayout(ViewGroup retryLayout) {
            if (retryLayout.findViewById(R.id.label) == null || retryLayout.findViewById(R.id.button) == null)
                throw new RuntimeException("Invalid layout. Should contain atleast two views named 'label' and 'button'");

            mRtryLyt = retryLayout;
            return this;
        }

        public Builder setCustomRetryLayoutResource(@LayoutRes int resId) {
            ViewGroup v = (ViewGroup) LayoutInflater.from(ctx).inflate(resId, null);

            return setCustomRetryLayout(v);
        }

        public Builder setCustomMarginDimensions(@DimenRes int left, @DimenRes int top, @DimenRes int right, @DimenRes int bottom) {
            Resources resources = ctx.getResources();

            int l = left > 0 ? resources.getDimensionPixelSize(left) : 0;
            int t = top > 0 ? resources.getDimensionPixelSize(top) : 0;
            int r = right > 0 ? resources.getDimensionPixelSize(right) : 0;
            int b = bottom > 0 ? resources.getDimensionPixelSize(bottom) : 0;

            mMrgns = new Rect(l, t, r, b);
            return this;
        }

        public Builder setHorizontalBarMarginPercentage(float percentage) {
            if (percentage < 0.41f)
                mHBarMarginPer = percentage;

            return this;
        }

        public LoadingView attachTo(Activity act) {
            mTarget = act.findViewById(android.R.id.content);

            return attachTo(mTarget);
        }


        public LoadingView attachTo(ViewGroup target) {
            mTarget = target;

            if (mTarget instanceof RelativeLayout || mTarget instanceof FrameLayout || mTarget instanceof ConstraintLayout) {
                return new LoadingView(mTarget, mProgClr, mBckClr, mTouchDsbld, mMrgns, mStyl, mIndtrmnt, mHBarMarginPer, mRtryLyt);
            } else
                throw new RuntimeException("Unsupported target view. Parent should be one of RelativeLayout, FrameLayout or ConstraintLayout");
        }
    }
}
