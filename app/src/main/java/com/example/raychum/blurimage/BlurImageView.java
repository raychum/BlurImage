package com.example.raychum.blurimage;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by raychum on 11/8/15.
 */
public class BlurImageView extends ImageView {
    private int radius = 25;
    private int blurMargin = 0;
    private int blurMarginLeft = 0;
    private int blurMarginRight = 0;
    private int blurMarginTop = 0;
    private int blurMarginBottom = 0;
    private int maskColor = 0;

    public BlurImageView(Context context) {
        super(context);
    }

    public BlurImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(attrs);
    }

    public BlurImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributes(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BlurImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttributes(attrs);
    }

    private void initAttributes(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.BlurImageView);
        blurMargin = a.getDimensionPixelSize(R.styleable.BlurImageView_blurMargin, blurMargin);
        blurMarginLeft = a.getDimensionPixelSize(R.styleable.BlurImageView_blurMarginLeft, blurMarginLeft);
        blurMarginRight = a.getDimensionPixelSize(R.styleable.BlurImageView_blurMarginRight, blurMarginRight);
        blurMarginTop = a.getDimensionPixelSize(R.styleable.BlurImageView_blurMarginTop, blurMarginTop);
        blurMarginBottom = a.getDimensionPixelSize(R.styleable.BlurImageView_blurMarginBottom, blurMarginBottom);
        maskColor = a.getColor(R.styleable.BlurImageView_maskColor, maskColor);
        radius = a.getInteger(R.styleable.BlurImageView_blurRadius, radius);
        if (blurMargin > 0) {
            blurMarginLeft = blurMargin;
            blurMarginRight = blurMargin;
            blurMarginTop = blurMargin;
            blurMarginBottom = blurMargin;
        }
        a.recycle();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (getDrawable() != null && getDrawable() instanceof BitmapDrawable) {
            blur(canvas, ((BitmapDrawable) getDrawable()).getBitmap());
        }
    }

    private void blur(Canvas canvas, Bitmap bkg) {
        float ratioY = (float) getMeasuredHeight() / (float) bkg.getHeight();
        float ratioX = (float) getMeasuredWidth() / (float) bkg.getWidth();
        float ratio = Math.max(ratioX, ratioY);
        Bitmap b = Bitmap.createScaledBitmap(bkg, Math.round(bkg.getWidth() * ratio), Math.round(bkg.getHeight() * ratio), false);
        Bitmap overlay = Bitmap.createBitmap(b, Math.abs((getMeasuredWidth() - b.getWidth()) / 2) + blurMarginLeft,
                Math.abs((getMeasuredHeight() - b.getHeight()) / 2) + blurMarginTop,
                b.getWidth() - Math.abs(getMeasuredWidth() - b.getWidth()) - blurMarginLeft - blurMarginRight,
                b.getHeight() - Math.abs(getMeasuredHeight() - b.getHeight()) - blurMarginTop - blurMarginBottom);
        Canvas cOverlay = new Canvas(overlay);
        RenderScript rs = RenderScript.create(getContext());

        Allocation overlayAlloc = Allocation.createFromBitmap(rs, overlay);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, overlayAlloc.getElement());

        blur.setInput(overlayAlloc);

        blur.setRadius(radius);

        blur.forEach(overlayAlloc);

        overlayAlloc.copyTo(overlay);
        cOverlay.drawBitmap(overlay, 0, 0, null);
        if (maskColor > 0) {
            cOverlay.drawColor(maskColor);
        }
        canvas.drawBitmap(overlay, blurMarginLeft, blurMarginTop, null);
        rs.destroy();
    }
}
