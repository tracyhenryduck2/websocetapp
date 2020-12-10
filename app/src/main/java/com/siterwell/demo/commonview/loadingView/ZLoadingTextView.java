package com.siterwell.demo.commonview.loadingView;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.siterwell.demo.R;
import com.siterwell.demo.commonview.loadingView.text.TextBuilder;


/*
@class ZLoadingTextView
@autor henry
@time 2018/12/12 9:14 AM
@email xuejunju_4595@qq.com
*/
public class ZLoadingTextView extends ZLoadingView
{
    private String mText = "ZLoading";

    public ZLoadingTextView(Context context)
    {
        this(context, null);
    }

    public ZLoadingTextView(Context context, AttributeSet attrs)
    {
        this(context, attrs, -1);
    }

    public ZLoadingTextView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    @Deprecated
    public void setLoadingBuilder(@NonNull Z_TYPE builder)
    {
        super.setLoadingBuilder(Z_TYPE.TEXT);
    }

    public void setText(String text)
    {
        this.mText = text;
        if (mZLoadingBuilder instanceof TextBuilder)
        {
            ((TextBuilder) mZLoadingBuilder).setText(mText);
        }
    }

    private void init(Context context, AttributeSet attrs)
    {
        super.setLoadingBuilder(Z_TYPE.TEXT);
        try
        {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ZLoadingTextView);
            String text = ta.getString(R.styleable.ZLoadingTextView_z_text);
            ta.recycle();
            if (!TextUtils.isEmpty(text))
            {
                this.mText = text;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onAttachedToWindow()
    {
        setText(mText);
        super.onAttachedToWindow();
    }
}
