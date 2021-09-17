package com.yzp.roundprogressbar.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.yzp.roundprogressbar.R;

public class RoundProgressBar extends View {
    private Paint mbgPaint = null;
    private Paint mfgPaint = null;
    private Paint mPaint = null;
    //圆形颜色
    private int circleBgColor = Color.GRAY;
    //圆形宽度
    private int circleWidth = 50;
    //圆心
    float centerXY = 600;
    //半径
    float radius = centerXY / 2;
    //圆形大小
    private RectF oval = null;

    //渐变类
    private Shader sweepGradient;
    //渐变颜色
    private int startColor = getResources().getColor(R.color.startColor);
    private int endColor = getResources().getColor(R.color.endColor);
    //
    private float maxAngle = 260;
    private float currentProgress = 10;
    private float maxProgress = 100;
    private float startAngle = 140;
    //变化角度
    private float sweepAngle = 0;


    public RoundProgressBar(Context context) {
        this(context, null);
    }

    public RoundProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttribute();
    }

    private void initAttribute() {
        mbgPaint = new Paint();
        mbgPaint.setColor(circleBgColor);
        mbgPaint.setStrokeWidth(circleWidth);
        mbgPaint.setAntiAlias(true);
        oval = new RectF(centerXY - radius, centerXY - radius, centerXY + radius, centerXY + radius);

        //背景扇形画笔设置
        mfgPaint = new Paint();
        mfgPaint.setStrokeWidth(circleWidth);
        mfgPaint.setColor(startColor);
        mfgPaint.setAntiAlias(true);
        sweepAngle = maxAngle * currentProgress / maxProgress;

        mPaint = new Paint();
        mPaint.setStrokeWidth(circleWidth);
        mPaint.setColor(startColor);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画底部扇形
        mbgPaint.setStyle(Paint.Style.STROKE);
        canvas.drawArc(oval, startAngle, maxAngle, false, mbgPaint);

        //底图最终圆角
        float endX = (float) (centerXY + radius * Math.cos((startAngle + maxAngle) * Math.PI / 180));
        float endY = (float) (centerXY + radius * Math.sin((startAngle + maxAngle) * Math.PI / 180));
        mbgPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(endX, endY, circleWidth / 2, mbgPaint);

        //进度圆形
        float progressX = (float) (centerXY + radius * Math.cos((sweepAngle + startAngle) * Math.PI / 180));
        float progressY = (float) (centerXY + radius * Math.sin((sweepAngle + startAngle) * Math.PI / 180));
        sweepGradient = new SweepGradient(progressX, progressY, startColor, endColor);
        mfgPaint.setShader(sweepGradient);
        mfgPaint.setStyle(Paint.Style.STROKE);
        canvas.drawArc(oval, startAngle, sweepAngle, false, mfgPaint);

        //进度条结尾部圆角
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(progressX, progressY, circleWidth / 2, mPaint);

        //进度条开始圆角
        float startX = (float) (centerXY + radius * Math.cos((startAngle) * Math.PI / 180));
        float startY = (float) (centerXY + radius * Math.sin((startAngle) * Math.PI / 180));
        mfgPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(startX, startY, circleWidth / 2, mfgPaint);
    }

    public void setProgress(float progress) {
        this.currentProgress = currentProgress + progress;
        if (sweepAngle < maxAngle) {
            sweepAngle = maxAngle * currentProgress / maxProgress;
            invalidate();
        }
    }
}
