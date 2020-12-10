package com.siterwell.demo.commonview.loadingView;


import com.siterwell.demo.commonview.loadingView.circle.SingleCircleBuilder;
import com.siterwell.demo.commonview.loadingView.text.TextBuilder;

/*
@class Z_TYPE
@autor henry
@time 2018/12/12 10:03 AM
@email xuejunju_4595@qq.com
*/
public enum Z_TYPE
{
    SINGLE_CIRCLE(SingleCircleBuilder.class),
    TEXT(TextBuilder.class),;

    private final Class<?> mBuilderClass;

    Z_TYPE(Class<?> builderClass)
    {
        this.mBuilderClass = builderClass;
    }

    <T extends ZLoadingBuilder> T newInstance()
    {
        try
        {
            return (T) mBuilderClass.newInstance();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
