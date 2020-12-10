package com.siterwell.demo.commonview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.InsetDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.siterwell.demo.R;


/**
 *  设置栏控件
 */
public class SettingItem extends RelativeLayout {

    /**切换按钮*/
    public static final int ACCESSORY_TYPE_CHECKBOX = 2;
    /**箭头标志*/
    public static final int ACCESSORY_TYPE_ARROW = 1;
    /**更新标志*/
    public static final int ACCESSORY_TYPE_UPDATE = 4;


    /**Item内容区域*/
    private LinearLayout mContent;
    /**标题View*/
    private TextView mTitle;
    /**副标题View**/
    private TextView subTitle;
    /**概要View*/
    private TextView mSummary;
    /**箭头View*/
    private ImageView mCheckedTextView;
    /**分割线*/
    private View mDividerView;
    /**附加类型*/
    private int mAccessoryType;
    /**是否显示分割线*/
    private boolean mShowDivider;
    /**标题*/
    private String mTitleText;
    /**副标题*/
    private String mSubTitleText;
    /**概要文字*/
    private String mSummaryText;
    private int[] mInsetDrawableRect = {0,0,0,0};
    /**更新标志*/
    private TextView mNewUpdate;
    private CheckBox switchbox;
    /**
     * @param context
     * @param attrs
     */
    public SettingItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.detaillist_item, this, true);

        mContent = (LinearLayout) findViewById(R.id.content);
        mTitle = (TextView) findViewById(android.R.id.title);
        subTitle = (TextView)findViewById(R.id.sub_title);
        mSummary = (TextView) findViewById(android.R.id.summary);
        mCheckedTextView = (ImageView) findViewById(R.id.accessory_checked);
        mDividerView = findViewById(R.id.item_bottom_divider);
        mNewUpdate = (TextView) findViewById(R.id.text_tv_one);
        switchbox =  (CheckBox)findViewById(R.id.checkbox);
        TypedArray localTypedArray = context.obtainStyledAttributes(attrs, R.styleable.setting_info);
        setTitleText(localTypedArray.getString(R.styleable.setting_info_item_titleText));
        setSubTitleText(localTypedArray.getString(R.styleable.setting_info_item_subTitleText));
        setDetailText(localTypedArray.getString(R.styleable.setting_info_item_detailText));
        setAccessoryType(localTypedArray.getInt(R.styleable.setting_info_item_accessoryType , 1));
        setShowDivider(localTypedArray.getBoolean(R.styleable.setting_info_item_showDivider , true));
        localTypedArray.recycle();
    }

    /**
     * @param showDivider
     */
    public void setShowDivider(boolean showDivider) {
        mShowDivider = showDivider;
        View dividerView = mDividerView;
        dividerView.setVisibility(mShowDivider ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置标题信息
     * @param text
     */
    public void setTitleText(String text) {
        mTitleText = text;
        if(text == null) {
            mTitle.setText("");
            return ;
        }
        mTitle.setText(mTitleText);
    }

    /**
     * 设置副标题信息
     * @param text
     */
    public void setSubTitleText(String text) {
        mSubTitleText = text;
        if(text == null) {
            subTitle.setText("");
            subTitle.setVisibility(View.GONE);
            return ;
        }
        subTitle.setText(mSubTitleText);
        subTitle.setVisibility(View.VISIBLE);
    }

//    public void setCheckText(String text) {
//        if(text == null) {
//            mCheckedTextView.setText("");
//            return ;
//        }
//        mCheckedTextView.setText(text);
//    }

    /**
     * @param text
     *
     */
    public void setDetailText(String text) {
        mSummaryText = text;
        if(text == null) {
            mSummary.setText("");
            mSummary.setVisibility(View.GONE);
            return ;
        }
        mSummary.setText(mSummaryText);
        mSummary.setVisibility(View.VISIBLE);
    }

    public String getDetailText(){
        return mSummaryText;
    }

    @SuppressWarnings("deprecation")
    private void setSettingBackground(int resid) {
        int[] rect = new int[4];
        rect[0] = getPaddingLeft();
        rect[1] = getPaddingTop();
        rect[2] = getPaddingRight();
        rect[3] = getPaddingBottom();
        if (isInsetDrawable()) {
            setBackgroundDrawable(new InsetDrawable(getContext().getResources()
                    .getDrawable(resid), mInsetDrawableRect[0],
                    mInsetDrawableRect[1], mInsetDrawableRect[2],
                    mInsetDrawableRect[3]));
        } else {
            setBackgroundResource(resid);
        }
        setPadding(rect[0], rect[1], rect[2], rect[3]);
    }

    /**
     *
     * @return
     */
    private boolean isInsetDrawable() {
        for(int i = 0; i < mInsetDrawableRect.length ; i++) {
            if(mInsetDrawableRect[i] <= 0) {
                continue;
            }
            return true;
        }
        return false;
    }

    /**
     *
     * @param accessoryType
     */
    private void setAccessoryType(int accessoryType) {
        if(accessoryType == ACCESSORY_TYPE_ARROW) {
            mAccessoryType = ACCESSORY_TYPE_ARROW;
            mCheckedTextView.setImageResource(R.drawable.arrow);
            setSettingBackground(0);
            return ;
        }else if(accessoryType == ACCESSORY_TYPE_CHECKBOX){
            mAccessoryType = ACCESSORY_TYPE_CHECKBOX;
            switchbox.setVisibility(View.VISIBLE);
            mCheckedTextView.setVisibility(View.GONE);
            return;
        }
    }

    /**
     * 返回切换按钮
     * @return
     */
    public ImageView getCheckedTextView(){
        return mCheckedTextView;
    }

    /**
     * 是否显示版本更新
     * @param visibility
     */
    public void setNewUpdateVisibility(boolean visibility) {
        if(mNewUpdate != null) {
            mNewUpdate.setVisibility(visibility? View.VISIBLE: View.GONE);
        }
    }

    public int getNewUpdateVisibility(){

        return mNewUpdate.getVisibility();
    }


    /**
     * 是否处于选中状态
     * @return
     */
    public boolean isChecked() {
        if(mAccessoryType == ACCESSORY_TYPE_CHECKBOX) {
            return switchbox.isChecked();
        }
        return true;
    }

    /**
     * 设置状态
     * @param checked
     */
    public void setChecked(boolean checked) {
        if(mAccessoryType != ACCESSORY_TYPE_CHECKBOX) {
            return ;
        }
        switchbox.setChecked(checked);
    }
//
//
//    public void toggle() {
//        if(mAccessoryType != ACCESSORY_TYPE_CHECKBOX) {
//            return ;
//        }
//        mCheckedTextView.toggle();
//    }
}
