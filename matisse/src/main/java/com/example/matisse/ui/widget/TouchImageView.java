package com.example.matisse.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;

import java.lang.ref.WeakReference;



public class TouchImageView extends androidx.appcompat.widget.AppCompatImageView
        implements ScaleGestureDetector.OnScaleGestureListener, View.OnTouchListener {


    private ScaleGestureDetector scaleGestureDetector;
    private Matrix mMatrix;
    private float initScale;
    private float minScale = 0.5f;

    private float maxScale = 4.0f;

    private float midScale = 2.0f;
    private GestureDetector gestureDetector;
    private int mLastPointerCount;
    private float mLastX;
    private float mLastY;
    private boolean isCanDrag;
    private boolean isCheckLeftAndRight;
    private boolean isCheckTopAndBottom;
    private int mTouchSlop;
    private float startX;

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
        //获取系统默认缩放

        setScaleType(ScaleType.MATRIX); // 缩放模式
        scaleGestureDetector = new ScaleGestureDetector(new WeakReference<Context>(context).get(),
                new WeakReference<>(this).get());
        mMatrix = new Matrix();
        setOnTouchListener(this);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {

                float scale = getPreScale();
                if (scale < midScale) {
                    mMatrix.postScale(midScale/scale, midScale/scale,
                            getWidth()/2.0f, getHeight()/2.0f);
                    setImageMatrix(mMatrix);
                } else {
                    //计算将图片移动至中间距离
                    int dx = getWidth()/2 - getDrawable().getIntrinsicWidth()/2;
                    int dy = getHeight()/2 - getDrawable().getIntrinsicHeight()/2;
                    mMatrix.reset();
                    mMatrix.postTranslate(dx, dy);
                    mMatrix.postScale(initScale, initScale, getWidth()/2.0f, getHeight()/2.0f);
                    setImageMatrix(mMatrix);
                }
                return true;
            }
        });
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
        } else {
            Drawable drawable = getDrawable();
            resetMatrix(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), getWidth(), getHeight());

            setImageMatrix(mMatrix);
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
        else return true;
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
        gestureDetector = null;
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
        midScale = 2.0f*scale;
        initScale = scale;
        setImageMatrix(matrix);

    }


    private void resetMatrix(int imgWidth, int imgHeight, int width, int height) {
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
        mMatrix.reset();
        mMatrix.postTranslate((width-imgWidth)/2.0f, (height-imgHeight)/2.0f);
        mMatrix.postScale(scale, scale, getWidth()/2.0f, getHeight()/2.0f);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (gestureDetector != null && gestureDetector.onTouchEvent(event)) {
            return true;
        }
        scaleGestureDetector.onTouchEvent(event);
        // 计算多指触控中心点
        float currentX = 0;
        float currentY = 0;
        int pointCount = event.getPointerCount();
        for (int i = 0; i < pointCount; i ++) {
            currentX += event.getX(i);
            currentY += event.getY(i);
        }
        currentX /= pointCount;
        currentY /= pointCount;

        if (mLastPointerCount != pointCount) {
            isCanDrag = false;
            mLastX = currentX;
            mLastY = currentY;
        }
        mLastPointerCount = pointCount;
        RectF rectF = getMatrixRectF();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                break;
//                if (rectF.width() > getWidth() || rectF.height() > getHeight()) {
//                    getParent().requestDisallowInterceptTouchEvent(true);
//                }
//                break;

            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
//                if (Math.abs(moveX - startX) < 70) {
//                    getParent().requestDisallowInterceptTouchEvent(true);
//                } else {
//                    getParent().requestDisallowInterceptTouchEvent(false);
//                }
                if (Math.abs(moveX - startX) > 70) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                    break;
                }
                if (rectF.width() > (getWidth() + 0.01) ||
                        rectF.height() > (getHeight() + 0.01)) {
                    getParent().requestDisallowInterceptTouchEvent(true);

                } else {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                float dx = currentX - mLastX;
                float dy = currentY - mLastY;
                if (!isCanDrag) {
                    isCanDrag = isMoveAction(dx, dy);
                }

                if (isCanDrag) {
                    RectF rectf = getMatrixRectF();
                    if (getDrawable() != null) {
                        isCheckLeftAndRight = isCheckTopAndBottom = true;
                        //如果宽度小于控件宽度,不允许横向移动
                        if (rectf.width() < getWidth()) {
                            dx = 0;
                            isCheckLeftAndRight = false;
                        }
                        //若高度小于控件高度,不允许纵向移动
                        if (rectf.height() < getHeight()) {
                            dy = 0;
                            isCheckTopAndBottom = false;
                        }
                        mMatrix.postTranslate(dx, dy);
                        checkBorderTranslate();
                        setImageMatrix(mMatrix);

                    }
                }
                break;
            //结束时,将手指数量置0
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mLastPointerCount = 0;
                break;
        }
        return true;
    }


    /**
     * 当移动时,进行边界检查.
     */
    private void checkBorderTranslate() {
        RectF rectF = getMatrixRectF();
        float dx = 0;
        float dy = 0;
        int width = getWidth();
        int height = getHeight();
        if (isCheckTopAndBottom) {
            if (rectF.top > 0) {
                dy = -rectF.top;
            }
            if (rectF.bottom < height) {
                dy = height - rectF.bottom;
            }
        }
        if (isCheckLeftAndRight) {
            if (rectF.left > 0) {
                dx = -rectF.left;
            }
            if (rectF.right < width) {
                dx = width - rectF.right;
            }
        }

        mMatrix.postTranslate(dx, dy);
    }

    /**
     * 判断当前移动距离是否大于系统默认最小移动距离
     *
     * @param dx 横轴移动距离
     * @param dy 纵轴移动距离
     * @return
     */
    private boolean isMoveAction(float dx, float dy) {
        return Math.sqrt(dx*dx + dy*dy) > mTouchSlop;
    }

    // 获得图片放大缩小以后的宽和高
    private RectF getMatrixRectF() {
        RectF rectF = new RectF();
        Drawable drawable = getDrawable();
        if (drawable != null) {
            rectF.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            mMatrix.mapRect(rectF);
        }

        return rectF;
    }
}
