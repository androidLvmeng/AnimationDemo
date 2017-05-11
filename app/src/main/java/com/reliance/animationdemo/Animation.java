package com.reliance.animationdemo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.LinearInterpolator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by sunzhishuai on 17/5/11.
 * E-mail itzhishuaisun@sina.com
 */

public class Animation extends SurfaceView implements SurfaceHolder.Callback, Runnable, ValueAnimator.AnimatorUpdateListener {

    private SurfaceHolder holder;
    private Paint paint, circlePaint;
    private Bitmap bitmap;
    private static final String TAG = "Animation";
    private static final String TAGERROR = "ERROR";
    private ExecutorService mDrawThreadPool = Executors.newFixedThreadPool(1);
    private ValueAnimator valueAnimator;
    private Paint bgPaint;

    public Animation(Context context) {


        this(context, null);
    }

    public Animation(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Animation(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.setKeepScreenOn(true);
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        holder = this.getHolder();
        holder.addCallback(this);
        holder.setFormat(PixelFormat.TRANSLUCENT);


        paint = new Paint();
        paint.setAntiAlias(true);

        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(20);

        bgPaint = new Paint();
        bgPaint.setColor(Color.WHITE);
        setFocusable(true);
        setFocusableInTouchMode(true);
        initAnimation();
    }

    private void initAnimation() {
        valueAnimator = ValueAnimator.ofFloat(0, 100).setDuration(1500);
        valueAnimator.addUpdateListener(this);
        valueAnimator.setInterpolator(new LinearInterpolator());
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        valueAnimator.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    public void draw(float percent) {
        Canvas canvas = null;
        try {
            canvas = holder.lockCanvas();
            if (canvas != null) {
                canvas.drawColor(Color.parseColor("#00cf78"));
                drawIcon(canvas, percent);
                int r = (int) (Math.max(bitmap.getWidth(), bitmap.getHeight()) * 1.5 / 2);
                drawCircle(canvas, percent, r);
            }
        } catch (Exception e) {
            Log.v(TAGERROR, "draw is Error!");
        } finally {
            holder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawCircle(Canvas canvas, float percent, int r) {
        //圆方大的半径
        float dR = r * 0.3f;
        float devide = 50;
        float currentR = r + dR * percent / 100;
        circlePaint.setColor(Color.parseColor("#4cdb94"));
        circlePaint.setAlpha(255);
        RectF rectF = new RectF(this.getWidth() / 2 - currentR, this.getHeight() / 2 - currentR,
                this.getWidth() / 2 + currentR, this.getHeight() / 2 + currentR);
        canvas.drawArc(rectF, 0, 360, false, circlePaint);
        if (percent != 100) {
            circlePaint.setStrokeWidth(10);
            circlePaint.setAlpha((int) (255 * 0.8f * (100 - percent) / 100));
            // 第一个圆圈
            canvas.drawCircle(this.getWidth() / 2, this.getHeight() / 2, currentR+devide, circlePaint);
            //第二个圆圈
            circlePaint.setAlpha((int) (255 * 0.7f * (100 - percent) / 100));
            canvas.drawCircle(this.getWidth() / 2, this.getHeight() / 2, currentR+devide*2, circlePaint);
        }
        circlePaint.setColor(Color.WHITE);
        canvas.drawArc(rectF, 0, 360 * percent / 100, false, circlePaint);


    }

    private void drawIcon(Canvas canvas, float percent) {
        paint.setColor(Color.WHITE);
        int dy = (this.getHeight() - bitmap.getHeight()) / 2;
        float currentDy = dy * percent / 100;
        int top = (int) (this.getHeight() - currentDy - bitmap.getHeight());
        canvas.drawBitmap(bitmap, this.getWidth() / 2 - bitmap.getWidth() / 2,
                top, paint);
    }

    @Override
    public void run() {
        draw(value);
    }

    private float value = 0;

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        value = (float) animation.getAnimatedValue();
        mDrawThreadPool.execute(this);
    }
}
