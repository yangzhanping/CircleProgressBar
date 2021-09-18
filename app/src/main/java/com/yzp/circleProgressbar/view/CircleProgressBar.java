package com.yzp.circleProgressbar.view;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.Nullable;

import com.yzp.circleProgressbar.R;

public class CircleProgressBar extends View {
    /*默认View大小*/
    private static final int DEFAULT_VIEW_WIDTH = 300;

    /*全局参数-----------------*/
    private int circleWidth = 50;
    private float centerXY;
    private float radius;
    private RectF circleOval = null;
    private float maxAngle = 260;
    private float startAngle = 140;
    private float maxProgress = 100;
    private float currentProgress = 10;
    private float sweepAngle = 0;

    /*白色圆形*/
    private Paint mbgCirclePaint = null;
    private int mBgColor = getResources().getColor(R.color.white);
    private int mbgCircleWidth = 20;
    private float bgMargin = 20;

    /*背景扇形参数--------------*/
    private Paint mbgPaint = null;
    private int circleBgColor = getResources().getColor(R.color.circleBgColor);
    private float rectStartXY = circleWidth + bgMargin;

    /*进度条扇形参数------------*/
    private Paint mfgPaint = null;
    private Shader sweepGradient;
    private int startColor = getResources().getColor(R.color.startColor);
    private int endColor = getResources().getColor(R.color.endColor);

    public CircleProgressBar(Context context) {
        this(context, null);
    }

    public CircleProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttribute();
    }

    private void initAttribute() {
        //背景扇形画笔设置
        mbgCirclePaint = new Paint();
        mbgCirclePaint.setColor(mBgColor);
        mbgCirclePaint.setStyle(Paint.Style.FILL);
        mbgCirclePaint.setAntiAlias(true);
        mbgCirclePaint.setDither(true);

        //背景扇形画笔设置
        mbgPaint = new Paint();
        mbgPaint.setColor(circleBgColor);
        mbgPaint.setStyle(Paint.Style.STROKE);
        mbgPaint.setStrokeCap(Paint.Cap.ROUND);
        mbgPaint.setStrokeWidth(circleWidth);
        mbgPaint.setAntiAlias(true);
        mbgPaint.setDither(true);

        //进度条扇形画笔参数设置
        mfgPaint = new Paint();
        mfgPaint.setColor(startColor);
        mfgPaint.setStyle(Paint.Style.STROKE);
        mfgPaint.setStrokeCap(Paint.Cap.ROUND);
        mfgPaint.setStrokeWidth(circleWidth);
        mfgPaint.setAntiAlias(true);
        mfgPaint.setDither(true);
        sweepAngle = maxAngle * currentProgress / maxProgress;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int width = measure(widthMeasureSpec);
        final int height = measure(heightMeasureSpec);
        final int viewSize = Math.min(width, height);
        setMeasuredDimension(viewSize, viewSize);
    }

    private int measure(int measureSpec) {
        int defaultWidth = DEFAULT_VIEW_WIDTH;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.AT_MOST:
                defaultWidth = Math.min(defaultWidth, specSize);
                break;
            case MeasureSpec.EXACTLY:
                defaultWidth = specSize;
                break;
        }
        return defaultWidth;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        centerXY = getWidth();
        radius = centerXY / 2;

        //底部白色圆形
        canvas.drawCircle(radius, radius, radius, mbgCirclePaint);

        //底部背景扇形
        circleOval = new RectF(rectStartXY, rectStartXY, centerXY - rectStartXY, centerXY - rectStartXY);
        canvas.drawArc(circleOval, startAngle, maxAngle, false, mbgPaint);

        //进度扇形
        float progressX = (float) (radius + (radius - rectStartXY) * Math.cos((sweepAngle + startAngle) * Math.PI / 180));
        float progressY = (float) (radius + (radius - rectStartXY) * Math.sin((sweepAngle + startAngle) * Math.PI / 180));

        sweepGradient = new SweepGradient(progressX, progressY, startColor, endColor);
        mfgPaint.setShader(sweepGradient);
        canvas.drawArc(circleOval, startAngle, sweepAngle, false, mfgPaint);
    }

    /**
     * 设置进度
     *
     * @param progress
     */
    public void setProgress(float progress) {
        this.currentProgress = currentProgress + progress;
        if (sweepAngle < maxAngle) {
            sweepAngle = maxAngle * currentProgress / maxProgress;
            invalidate();
        }
    }

    /**
     * 设置进度并增加动画效果
     *
     * @param progress
     * @param animTime
     */
    public void setProgress(float progress, long animTime) {
        if (animTime <= 0) {
            setProgress(progress);
        } else {
            ValueAnimator animator = ValueAnimator.ofFloat(currentProgress, progress + currentProgress);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    currentProgress = (Float) animation.getAnimatedValue();
                    if (sweepAngle < maxAngle) {
                        sweepAngle = maxAngle * currentProgress / maxProgress;
                        invalidate();
                    }
                }
            });
            animator.setInterpolator(new DecelerateInterpolator());
            animator.setDuration(animTime);
            animator.start();
        }
    }

    /**
     * 进度条开始颜色
     *
     * @param color
     */
    public void setProgressStartColor(int color) {
        this.startColor = color;
        invalidate();
    }

    /**
     * 进度条结束颜色
     *
     * @param color
     */
    public void setProgressEndColor(int color) {
        this.endColor = color;
        invalidate();
    }

    /**
     * 设置进度底图默认颜色
     *
     * @param color
     */
    public void setProgressDefaultColor(int color) {
        this.circleBgColor = color;
        invalidate();
    }
}
