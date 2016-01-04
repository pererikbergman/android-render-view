/**
 * Copyright 2011 Per-Erik Bergman (bergman@uncle.se)
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.uncle.renderview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RenderView extends SurfaceView implements SurfaceHolder.Callback {

    private RenderEngine mRenderEngine;

    private RenderThread mRenderThread;

    public RenderView(Context context) {
        super(context);
    }

    public RenderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RenderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setRenderEngine(RenderEngine renderEngine) {
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        mRenderEngine = renderEngine;
        mRenderEngine.initialize(getContext().getResources());
        mRenderThread = new RenderThread(
                holder,
                mRenderEngine
        );
        setFocusable(true);
        setOnTouchListener(renderEngine);
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        if (mRenderEngine != null) {
            mRenderEngine.setDimension(width, height);
        }
    }

    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        boolean retry = true;
        mRenderThread.setRunning(false);
        while (retry) {
            try {
                mRenderThread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }

    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (!mRenderThread.isRunning()) {
            mRenderThread = new RenderThread(getHolder(),
                    mRenderEngine);
            mRenderThread.start();
        } else {
            mRenderThread.start();
        }
    }

    private class RenderThread extends Thread {

        private SurfaceHolder mSurfaceHolder;
        private Paint         mBackgroundColor;
        private RenderEngine  mRenderEngine;
        private boolean mRunning   = true;
        private long    mSleepTime = 0;
        private long    mDelay     = 20;
        private long mLastTime;

        public RenderThread(SurfaceHolder surfaceHolder,
                            RenderEngine renderEngine) {
            mSurfaceHolder = surfaceHolder;
            mBackgroundColor = new Paint();
            mBackgroundColor.setARGB(255, 0, 0, 0);
            mRenderEngine = renderEngine;
            mLastTime = System.currentTimeMillis();
        }

        @Override
        public void run() {
            while (mRunning) {
                long delta = System.currentTimeMillis() - mLastTime;
                mLastTime = System.currentTimeMillis();
                long beforeTime = System.nanoTime();
                mRenderEngine.update(delta / 1000f);

                Canvas canvas = null;
                try {
                    canvas = mSurfaceHolder.lockCanvas(null);
                    synchronized (mSurfaceHolder) {
                        canvas.drawRect(0, 0, canvas.getWidth(),
                                canvas.getHeight(), mBackgroundColor);
                        mRenderEngine.render(canvas);
                    }
                } finally {
                    if (canvas != null) {
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }

                mSleepTime = mDelay - ((System.nanoTime() - beforeTime) / 1000000L);

                try {
                    if (mSleepTime > 0) {
                        Thread.sleep(mSleepTime);
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(RenderThread.class.getName()).log(
                            Level.SEVERE, null, ex);
                }
            }
        }

        public boolean isRunning() {
            return mRunning;
        }

        public void setRunning(boolean value) {
            mRunning = value;
        }
    }

    public interface RenderEngine extends OnTouchListener {

        public void initialize(Resources resources);

        public void update(float delta);

        public void render(Canvas canvas);

        public void setDimension(int width, int height);

    }

}