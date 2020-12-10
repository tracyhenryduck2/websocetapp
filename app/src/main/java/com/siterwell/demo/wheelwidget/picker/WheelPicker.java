package com.siterwell.demo.wheelwidget.picker;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.LinearLayout;

import com.siterwell.demo.wheelwidget.popup.ConfirmPopup;


/**
 * 滚动轮子选择器。
 *
 */
public abstract class WheelPicker<T> extends ConfirmPopup<LinearLayout> {
    private LinearLayout view;
    private OnWheelListener<T> onWheelListener;

    public WheelPicker(Activity activity) {
        super(activity);
        view = initWheelView();
    }

    @Override
    protected final LinearLayout initContentView() {
        return view;
    }

    @Override
    protected void setContentViewAfter(View contentView) {
        checkMaxHeight(view);
        if (onWheelListener != null) {
            super.setOnConfirmListener(new ConfirmPopup.OnConfirmListener() {
                @Override
                public void onConfirm() {
                    onWheelListener.onSubmit(getCurrentItem());
                }

                @Override
                public void onCancel() {
                    onWheelListener.onCancel();
                }
            });
        }
    }

    protected abstract LinearLayout initWheelView();

    protected abstract T getCurrentItem();

    /**
     * 设置是否循环滚动。
     * 如果为true的话，建议设置阴影
     */
    public abstract void setCyclic(boolean cyclic);

    /**
     * 设置阴影
     * {@link WheelView#setShadow(GradientDrawable, GradientDrawable)}
     */
    public void setShadow(GradientDrawable topShadow, GradientDrawable bottomShadow) {
        throw new RuntimeException("please implement the method");
    }

    /**
     * 滑动幅度延迟时间，单位为毫秒
     */
    public abstract void setScrollingDuration(int scrollingDuration);

    @Deprecated
    @Override
    public final void setOnConfirmListener(OnConfirmListener onConfirmListener) {
        throw new RuntimeException("Please use setOnWheelListener instead");
    }

    public void setOnWheelListener(OnWheelListener<T> listener) {
        this.onWheelListener = listener;
    }

    public static abstract class OnWheelListener<R> extends OnConfirmListener {

        public abstract void onSubmit(R result);

        @Deprecated
        @Override
        public void onConfirm() {
            throw new RuntimeException("Please use onSubmit instead");
        }

    }

}
