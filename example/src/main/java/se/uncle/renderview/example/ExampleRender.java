package se.uncle.renderview.example;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import se.uncle.renderview.RenderView;

public class ExampleRender implements RenderView.RenderEngine {

    private static final float MAX_SPEED      = 800f;
    private static final float HALF_MAX_SPEED = MAX_SPEED / 2;

    private Bitmap mWhiteDot;
    private Paint  mPaint;

    private List<Ball> mBallList;
    private int        mWidth;
    private int        mHeight;
    private int        mHalfDotWidth;
    private int        mHalfDotHeight;

    @Override
    public void initialize(final Resources resources) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));

        mBallList = new ArrayList<>();
        mWhiteDot = BitmapFactory.decodeResource(resources, R.drawable.dot);

        mHalfDotWidth = mWhiteDot.getWidth() / 2;
        mHalfDotHeight = mWhiteDot.getHeight() / 2;
    }

    @Override
    public void update(float d) {
        for (Ball ball : mBallList) {
            ball.update(d);
            if (ball.x > mWidth - mHalfDotWidth && ball.vx > 0 || ball.x < mHalfDotWidth && ball.vx < 0) {
                ball.vx = 0 - ball.vx;
            }
            if (ball.y > mHeight - mHalfDotHeight && ball.vy > 0 || ball.y < mHalfDotHeight && ball.vy < 0) {
                ball.vy = 0 - ball.vy;
            }
        }
    }

    @Override
    public void render(final Canvas canvas) {
        for (Ball ball : mBallList) {
            canvas.drawBitmap(
                    mWhiteDot,
                    ball.x - mHalfDotWidth,
                    ball.y - mHalfDotHeight,
                    mPaint
            );
        }
    }

    @Override
    public void setDimension(final int width, final int height) {
        mWidth = width;
        mHeight = height;
        if (mBallList.isEmpty()) {
            for (int i = 0; i < 10; i++) {
                Ball ball = new Ball();
                ball.x = (float) (Math.random() * width);
                ball.y = (float) (Math.random() * height);
                ball.vx = -HALF_MAX_SPEED + (float) (Math.random() * MAX_SPEED);
                ball.vy = -HALF_MAX_SPEED + (float) (Math.random() * MAX_SPEED);

                mBallList.add(ball);
            }
        }
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        return false;
    }

    private class Ball {

        public float x;
        public float y;
        public float vx;
        public float vy;

        public Ball() {
        }

        public void update(float delta) {
            x += vx * delta;
            y += vy * delta;
        }
    }
}
