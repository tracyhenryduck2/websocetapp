package com.siterwell.demo.commonview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.siterwell.demo.common.UnitTools;


/**
 * Created by gc-0001 on 2017/2/15.
 * 圆圈进度条控件
 */
public class RoundProgressView extends View {

    /**最外围的颜色值*/
    private int mOutRoundColor = Color.argb(0, 255, 255, 255);
    /**中心圆的颜色值*/
    private int mCenterRoundColor = Color.argb(0, 255, 255, 255);

    /**中心圆err的颜色值*/
    private int mCenterErrRoundColor = Color.argb(255, 195, 195, 195);
    /**进度的颜色*/
    private int mProgressRoundColor = Color.argb(255, 51, 167, 255);
    /**进度的背景颜色*/
    private int mProgressRoundBgColor = Color.argb(255, 206, 212, 226);

    /**进度的背景颜色2*/
    private int mProgressErrRoundBgColor = Color.argb(255, 226, 226, 226);

    /**进度条的宽度*/
    private int mProgressWidth = 20;

    /**Err的宽度*/
    private int errWidth = 10;

    private int[] colors = {Color.WHITE, Color.RED};
    /***字体颜色*/
    private int mTextColor = Color.rgb(51, 167, 255);
    private float mPencentTextSize = 100;

    private int mWidth,mHeight;
    private int mPaddingX;
    private int Screenwidth;

    private float mProgress = 0.5f;
    private float mMax = 1.0f;

    private Paint mPaint = new Paint();
    private boolean flag = false;

    public RoundProgressView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Screenwidth = UnitTools.getScreenWidth(context);
        init();
    }

    public RoundProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Screenwidth = UnitTools.getScreenWidth(context);
        init();
    }

    public RoundProgressView(Context context) {
        super(context);
        Screenwidth = UnitTools.getScreenWidth(context);
        init();
    }

    public void init(){

    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mWidth = getWidth();
        mHeight = getHeight();

        if(mWidth > mHeight){
            mPaddingX = (mWidth-mHeight)/2;
            mWidth = mHeight;
        }
        mPaint.setAntiAlias(true); // 消除锯齿
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mOutRoundColor);
        RectF oval = new RectF(new Rect(mPaddingX, 0, mWidth+mPaddingX, mHeight));
        canvas.drawArc(oval, 0, 360, true, mPaint);

        if(!flag){
            int halfWidth = mWidth/4;
            mPaint.setStrokeWidth(mProgressWidth);
            mPaint.setColor(mProgressRoundBgColor);
            mPaint.setStyle(Paint.Style.STROKE);
            oval = new RectF(new Rect(halfWidth+mPaddingX, halfWidth, halfWidth*3+mPaddingX, halfWidth*3));
            canvas.drawArc(oval, 0, 360, false, mPaint);

            mPaint.setColor(mProgressRoundColor);
            canvas.drawArc(oval, -90, 360*mProgress/mMax, false, mPaint);


            halfWidth = mWidth/4;
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mCenterRoundColor);
            oval = new RectF(new Rect(halfWidth+mPaddingX, halfWidth, halfWidth*3+mPaddingX, halfWidth*3));
            canvas.drawArc(oval, 0, 360, false, mPaint);
        }else{
            int halfWidth = mWidth/4;
            mPaint.setStrokeWidth(mProgressWidth);
            mPaint.setColor(mProgressErrRoundBgColor);
            mPaint.setStyle(Paint.Style.STROKE);
            oval = new RectF(new Rect(halfWidth+mPaddingX, halfWidth, halfWidth*3+mPaddingX, halfWidth*3));
            canvas.drawArc(oval, 0, 360, false, mPaint);


            halfWidth = mWidth/4;
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mCenterErrRoundColor);
            oval = new RectF(new Rect(halfWidth+mPaddingX, halfWidth, halfWidth*3+mPaddingX, halfWidth*3));
            canvas.drawArc(oval, 0, 360, false, mPaint);
        }



        if(!flag){
            mPaint.reset();
            mPaint.setTextSize(mPencentTextSize);
            mPaint.setColor(mTextColor);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setTextAlign(Paint.Align.CENTER);
            String number = (int)(mProgress*100/mMax)+"";
            canvas.drawText(number, mWidth/2+mPaddingX, mHeight/2, mPaint);

            float textWidth = mPaint.measureText(number);
            mPaint.setTextSize(mPencentTextSize/2);
            canvas.drawText("%", mWidth/2+mPaddingX+textWidth/2+15, mHeight/2-mPencentTextSize/8, mPaint);
        }else{
            //mPaint.reset();
            mPaint.setColor(colors[0]);
            mPaint.setStrokeWidth(errWidth);
            canvas.drawLine(Screenwidth/2-50, mHeight/2-(mHeight-mWidth)/2-50, Screenwidth/2+50, mHeight/2+50-(mHeight-mWidth)/2, mPaint);
            canvas.drawLine(Screenwidth/2-50, mHeight/2-(mHeight-mWidth)/2+50, Screenwidth/2+50, mHeight/2-(mHeight-mWidth)/2-50, mPaint);
        }


    }

    public void setMax(float mMax) {
        this.mMax = mMax;
    }

    public void setProgress(float mProgress) {
        flag = false;
        this.mProgress = mProgress;
        invalidate();
    }

    public void setErrStatus() {
        flag = true;
        this.mProgress = 0;
        invalidate();
    }

    public float getMax() {
        return mMax;
    }

    public float getProgress() {
        return mProgress;
    }
}
