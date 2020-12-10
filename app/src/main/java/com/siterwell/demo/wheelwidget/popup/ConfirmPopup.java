package com.siterwell.demo.wheelwidget.popup;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.siterwell.demo.R;
import com.siterwell.demo.wheelwidget.helper.Common;


/**
 * 带确定及取消按钮的
 *
 */
public abstract class ConfirmPopup<V extends View> extends BasePopup<LinearLayout> implements View.OnClickListener {
    protected static final String TAG_SUBMIT = "submit";
    protected static final String TAG_CANCEL = "cancel";
    private boolean cancelVisible = true;
    private CharSequence cancelText = getView().getContext().getResources().getString(R.string.dialog_cancel);
    private CharSequence submitText = getView().getContext().getResources().getString(R.string.dialog_sure);
    private OnConfirmListener onConfirmListener;

    public ConfirmPopup(Activity activity) {
        super(activity);
    }

    protected abstract V initContentView();

    public void setCancelVisible(boolean cancelVisible) {
        this.cancelVisible = cancelVisible;
    }

    public void setCancelText(CharSequence cancelText) {
        this.cancelText = cancelText;
    }

    public void setSubmitText(CharSequence submitText) {
        this.submitText = submitText;
    }

    @Override
    protected LinearLayout getView() {

        LinearLayout rootLayout = new LinearLayout(activity);
        rootLayout.setBackgroundColor(Color.TRANSPARENT);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setGravity(Gravity.CENTER);
        rootLayout.setPadding(0, 0, 0, 0);

        LinearLayout rootLayout2 = new LinearLayout(activity);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        layoutParams.setMargins(20,0,20,0);
        rootLayout2.setLayoutParams(layoutParams);
        Drawable drawable = Common.getDrawable(activity, R.drawable.bg_timer);
         Common.setBackground(rootLayout2,drawable);
        rootLayout2.setOrientation(LinearLayout.VERTICAL);
        rootLayout2.setGravity(Gravity.CENTER);
        rootLayout2.setPadding(4, 10, 4, 10);
        rootLayout2.setClipToPadding(false);
        RelativeLayout topButtonLayout = new RelativeLayout(activity);
        topButtonLayout.setLayoutParams(new RelativeLayout.LayoutParams(MATCH_PARENT,WRAP_CONTENT));
        topButtonLayout.setGravity(Gravity.CENTER_VERTICAL);
        Button cancelButton = new Button(activity);
        if (cancelVisible) {
            cancelButton.setVisibility(View.VISIBLE);
        } else {
            cancelButton.setVisibility(View.GONE);
        }
        cancelButton.setTag(TAG_CANCEL);
        RelativeLayout.LayoutParams cancelButtonLayoutParams = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        cancelButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        cancelButtonLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        cancelButton.setLayoutParams(cancelButtonLayoutParams);
        cancelButton.setBackgroundColor(Color.TRANSPARENT);
        cancelButton.setGravity(Gravity.CENTER);
        cancelButton.setText(cancelText);
        cancelButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                activity.getResources().getDimensionPixelSize(R.dimen.bottom_text_size));
        cancelButton.setTextColor(Common.toColorStateList(Color.BLACK, Color.RED));
        cancelButton.setOnClickListener(this);
        topButtonLayout.addView(cancelButton);
        Button submitButton = new Button(activity);
        submitButton.setTag(TAG_SUBMIT);
        RelativeLayout.LayoutParams submitButtonLayoutParams = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        submitButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        submitButtonLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        submitButton.setLayoutParams(submitButtonLayoutParams);
        submitButton.setBackgroundColor(Color.TRANSPARENT);
        submitButton.setGravity(Gravity.CENTER);
        submitButton.setText(submitText);
        submitButton.setTextColor(Common.toColorStateList(Color.BLACK, Color.RED));
        submitButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                activity.getResources().getDimensionPixelSize(R.dimen.bottom_text_size));
        submitButton.setOnClickListener(this);
        topButtonLayout.addView(submitButton);
        rootLayout2.addView(topButtonLayout);
        View lineView = new View(activity);
        lineView.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, Common.toPx(activity, 1)));
        rootLayout2.addView(lineView);
        rootLayout2.addView(initContentView());
        rootLayout.addView(rootLayout2);
        return rootLayout;
    }

    @Override
    public void onClick(View v) {
        if (onConfirmListener != null) {
            String tag = v.getTag().toString();
            if (tag.equals(TAG_SUBMIT)) {
                onConfirmListener.onConfirm();
            } else {
                onConfirmListener.onCancel();
                dismiss();
            }
        }

    }

    public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
    }

    public static abstract class OnConfirmListener {

        public abstract void onConfirm();

        public void onCancel() {

        }

    }

}
