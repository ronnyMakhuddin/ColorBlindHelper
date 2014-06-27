package com.beanslab.colorblindhelper.helper.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.beanslab.colorblindhelper.helper.R;
import com.beanslab.colorblindhelper.helper.core.HomeDashboard;
import com.beanslab.colorblindhelper.helper.gobjects.Point;
import com.beanslab.colorblindhelper.helper.utils.Utils;

/**
 * Created by andrew on 5/06/14.
 */
public class MyImageView extends ImageView {
        private Drawable mIcon;
        private float mPosX;
        private float mPosY;

        private float mLastTouchX;
        private float mLastTouchY;

        private static final int INVALID_POINTER_ID = -1;

        private Bitmap lienzo;

        // The ‘active pointer’ is the one currently moving our object.
        private int mActivePointerId = INVALID_POINTER_ID;

        public MyImageView(Context context) {
            this(context, null, 0);
        }

        public MyImageView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public MyImageView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            mIcon = context.getResources().getDrawable(R.drawable.picker);
            mIcon.setVisible(false,true);
        }

        @Override
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            canvas.save();
            //canvas.translate(mPosX, mPosY);
            mIcon.draw(canvas);
            canvas.restore();
        }

    public void showIcon(){
        int middleX = this.getWidth()/2 - (mIcon.getIntrinsicWidth()/2);
        int middleY = this.getHeight()/2 - (mIcon.getIntrinsicHeight()/2);

        mIcon.setBounds(middleX, middleY,middleX + mIcon.getIntrinsicWidth(), middleY + mIcon.getIntrinsicHeight());
        mIcon.setVisible(true,true);

    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Let the ScaleGestureDetector inspect all events.

        final int action = ev.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                final float x = ev.getX();
                final float y = ev.getY();

                mLastTouchX = x;
                mLastTouchY = y;
                mActivePointerId = ev.getPointerId(0);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                final float x = ev.getX(pointerIndex);
                final float y = ev.getY(pointerIndex);

                // Only move if the ScaleGestureDetector isn't processing a gesture.
                    final float dx = x - mLastTouchX;
                    final float dy = y - mLastTouchY;

                    mPosX += dx;
                    mPosY += dy;

                    invalidate();


                mLastTouchX = x;
                mLastTouchY = y;

                Rect r = mIcon.getBounds();
                int xN = (int) (r.left + dx);
                int yN = (int) (r.top + dy);
                mIcon.setBounds(xN, yN,  xN + mIcon.getIntrinsicWidth(), yN + mIcon.getIntrinsicHeight());

                Matrix inverse = new Matrix();
                this.getImageMatrix().invert(inverse);
                float[] touchPoint = new float[] {r.left, r.top + mIcon.getIntrinsicHeight()};
                inverse.mapPoints(touchPoint);
                int x_ = Integer.valueOf((int)touchPoint[0]);
                int y_ = Integer.valueOf((int)touchPoint[1]);

                int pixel = -1;
                try {
                    lienzo =  ((BitmapDrawable) this.getDrawable()).getBitmap();
                     pixel = lienzo.getPixel(x_, y_);
                }catch (Exception e){
                   // e.printStackTrace();
                }

                HomeDashboard.setColorDataSet(pixel);

                break;
            }

            case MotionEvent.ACTION_UP: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK)
                        >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = ev.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = ev.getX(newPointerIndex);
                    mLastTouchY = ev.getY(newPointerIndex);
                    mActivePointerId = ev.getPointerId(newPointerIndex);
                }
                break;
            }
        }

        return true;
    }


    }