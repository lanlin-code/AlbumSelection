package com.example.matisse.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import java.lang.ref.WeakReference;

public class TouchImageView extends androidx.appcompat.widget.AppCompatImageView
        implements ScaleGestureDetector.OnScaleGestureListener {

    private ScaleGestureDetector scaleGestureDetector;
    private Matrix mMatrix;
    private float minScale = 0.5f;
    private float maxScale = 4.0f;

    public TouchImageView(Context context) {
        super(context);
        init(context);
    }


    public TouchImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TouchImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setScaleType(ScaleType.MATRIX); // 缩放模式
        scaleGestureDetector = new ScaleGestureDetector(new WeakReference<Context>(context).get(),
                new WeakReference<>(this).get());
        mMatrix = new Matrix();
    }




    /*
     *缩放进行中，返回值表示是否下次缩放需要重置，如果返回true，
     * 那么detector就会重置缩放事件，如果返回false，
     * detector会在之前的缩放上继续进行计算
     */
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        if (getDrawable() == null) return true;
        float scale = detector.getScaleFactor(); // 获取本次缩放值
        float preScale = getPreScale();
        if (preScale*scale < maxScale && preScale*scale > minScale) {
            // getFocusX()返回组成缩放手势(两个手指)中点x的位置
            mMatrix.postScale(scale, scale, detector.getFocusX(), detector.getFocusY());
            mMatrix.postScale(scale, scale, (float) (getWidth()/2.0), (float) (getHeight()/2.0));
            setImageMatrix(mMatrix);
            makeDrawableCenter();
        }

        return true;
    }

    // 缩放开始，返回值表示是否受理后续的缩放事件
    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (scaleGestureDetector != null) return scaleGestureDetector.onTouchEvent(event);
        else return false;
    }

    private float getPreScale() {
        int matrixSize = 9;
        float[] matrix = new float[matrixSize];
        mMatrix.getValues(matrix);
        return matrix[Matrix.MSCALE_X];
    }

    private void makeDrawableCenter() {
        RectF rectF = new RectF();
        Drawable drawable = getDrawable();
        if (drawable != null) {
            rectF.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            mMatrix.mapRect(rectF); // 测量rectF并将测量结果放入rectF中，返回值是判断矩形经过变换后是否仍为矩形
        }
        int width = getWidth();
        int height = getHeight();
        float dx = 0;
        float dy = 0;
        if (rectF.width() >= width) {
            if (rectF.left > 0) dx = -rectF.left;
            if (rectF.right < width) dx = width - rectF.right;
        } else dx = (float) (width/2.0 - (rectF.right - rectF.width()/2));
        if (rectF.height() >= height) {
            if (rectF.top > 0) dy = -rectF.top;
            if (rectF.bottom < height) dy = height - rectF.bottom;
        } else dy = (float) (height/2.0 - (rectF.bottom - rectF.height()/2));

        if (dx != 0 || dy != 0) {
            mMatrix.postTranslate(dx, dy);
            setImageMatrix(mMatrix);
        }

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setImageDrawable(null);
        scaleGestureDetector = null;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        scaleImage(bm);
    }



    // 放缩平移图片，使图片适合屏幕大小且位于屏幕中心
    private void scaleImage(Bitmap bitmap) {
        int width = getWidth();
        int height = getHeight();
        int imgWidth = bitmap.getWidth();
        int imgHeight = bitmap.getHeight();
        float scale = 1.0f;

        if (imgWidth > width) {

            if (imgHeight <= height) {

                scale = (float) (height/(imgHeight*1.0));
            } else {

                scale = Math.min(width/(imgWidth*1.0f), height/(imgHeight*1.0f));
            }
        } else {
            if (imgHeight > height) {
                scale = (width/(imgWidth*1.0f));
            } else {
                scale = Math.min(width/(imgWidth*1.0f), height/(imgHeight*1.0f));
            }
        }
        Matrix matrix = new Matrix();
        matrix.postTranslate((width-imgWidth)/2.0f, (height-imgHeight)/2.0f);
        matrix.postScale(scale, scale, getWidth()/2.0f, getHeight()/2.0f);
        maxScale = 4.0f*scale;
        minScale = 0.5f*scale;
        setImageMatrix(matrix);

    }
}
