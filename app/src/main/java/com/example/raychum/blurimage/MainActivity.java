package com.example.raychum.blurimage;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MainActivity extends Activity {
    private BlurImageView image1;
    private BlurImageView image2;
    private BlurImageView image3;

//    private OnPreDrawListener mPreDrawListener = new OnPreDrawListener() {
//        @Override
//        public boolean onPreDraw() {
//            ViewTreeObserver observer = mText.getViewTreeObserver();
//            if (observer != null) {
//                observer.removeOnPreDrawListener(this);
//            }
//            Drawable drawable = mImage.getDrawable();
//            if (drawable != null && drawable instanceof BitmapDrawable) {
//                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
//                if (bitmap != null) {
//                    blur(bitmap, mText, 25);
//                }
//            }
//            return true;
//        }
//    };
//
//    private OnGlobalLayoutListener mLayoutListener = new OnGlobalLayoutListener() {
//
//        @Override
//        public void onGlobalLayout() {
//            ViewTreeObserver observer = mText.getViewTreeObserver();
//            if (observer != null) {
//                observer.addOnPreDrawListener(mPreDrawListener);
//            }
//        }
//    };

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        image1 = (BlurImageView) findViewById(R.id.image1);
        image2 = (BlurImageView) findViewById(R.id.image2);
        image3 = (BlurImageView) findViewById(R.id.image3);
        Picasso.with(this).load("http://www.planwallpaper.com/static/images/canberra_hero_image.jpg").into(image1);
        Picasso.with(this).load("http://www.jpl.nasa.gov/spaceimages/images/mediumsize/PIA17011_ip.jpg").into(image2);
        Picasso.with(this).load("http://pepinemom.p.e.pic.centerblog.net/djuugzn0.jpg").into(image3);

//        if (mImage != null && mText != null) {
//            ViewTreeObserver observer = mText.getViewTreeObserver();
//            if (observer != null) {
//                observer.addOnGlobalLayoutListener(
//                        mLayoutListener);
//            }
//        }
    }

    private void blur(Bitmap bkg, View view, float radius) {

        Bitmap overlay = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bkg, view.getMeasuredWidth(), bkg.getHeight() * view.getMeasuredWidth() / bkg.getWidth(), false);
        Canvas canvas = new Canvas(overlay);
        canvas.drawBitmap(scaledBitmap, -view.getLeft(), -view.getTop(), null);

        RenderScript rs = RenderScript.create(this);

        Allocation overlayAlloc = Allocation.createFromBitmap(rs, overlay);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, overlayAlloc.getElement());

        blur.setInput(overlayAlloc);

        blur.setRadius(radius);

        blur.forEach(overlayAlloc);

        overlayAlloc.copyTo(overlay);

        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), overlay);
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(getResources().getColor(R.color.black_transparent));
        Drawable[] drawables = {bitmapDrawable, colorDrawable};
        LayerDrawable layerDrawable = new LayerDrawable(drawables);
        view.setBackgroundDrawable(layerDrawable);

        rs.destroy();
    }

}
