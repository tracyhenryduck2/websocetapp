package com.siterwell.demo.common;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.siterwell.demo.R;
import com.siterwell.demo.commonview.ProgressDialog;
import com.siterwell.demo.commonview.TopBarView;

/**
 * ClassName:TopbarSuperActivity
 * 作者：Henry on 2017/4/22 16:29
 * 邮箱：xuejunju_4595@qq.com
 * 描述:
 */
public abstract class TopbarSuperActivity extends AppCompatActivity {

    /**
     * 标题
     */
    private TopBarView mTopBarView;
    private LayoutInflater mLayoutInflater;
    private View mContentView;
    private ProgressDialog mProgressDialog;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected SwipeRefreshLayout swipeRefreshLayout_em;//空布局；

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        init();
        swiperefreshLayoutInit();
        onCreateInit();

    }


    public TopBarView getTopBarView() {
        if(mTopBarView instanceof TopBarView) {
            return (TopBarView) mTopBarView;
        }
        return null;
    }

    private void init()  {

        int layoutId = getLayoutId();
        ViewGroup mRootView = (ViewGroup)findViewById(R.id.root);
        mLayoutInflater = LayoutInflater.from(this);
        mTopBarView = (TopBarView)findViewById(R.id.top_bar);

        if (layoutId != -1) {
            mContentView = mLayoutInflater.inflate(getLayoutId(), null);
            mRootView.addView(mContentView, LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
        }

        initSystemBar();
    }

    protected void swiperefreshLayoutInit(){
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swip);
        if(swipeRefreshLayout!=null){
            //调整SwipeRefreshLayout的位置
            swipeRefreshLayout.setColorSchemeResources(R.color.bar_bg);
            swipeRefreshLayout.setProgressViewOffset(false, 0,  (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
            swipeRefreshLayout_em = (SwipeRefreshLayout) LayoutInflater.from(this).inflate(R.layout.empty_view, null);
            ((ViewGroup)swipeRefreshLayout.getParent()).addView(swipeRefreshLayout_em);
            //调整SwipeRefreshLayout的位置
            swipeRefreshLayout_em.setColorSchemeResources(R.color.bar_bg);
            swipeRefreshLayout_em.setProgressViewOffset(false, 0,  (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        }

    }

    protected abstract void onCreateInit();

    protected abstract int getLayoutId();

    /**
     * hide inputMethod
     */
    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputMethodManager != null ) {
            View localView = this.getCurrentFocus();
            if(localView != null && localView.getWindowToken() != null ) {
                IBinder windowToken = localView.getWindowToken();
                inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
            }
        }
    }


    protected void initSystemBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getTopBarView().setFitsSystemWindows(true);//需要把根布局设置为这个属性 子布局则不会占用状态栏位置
            getTopBarView().setClipToPadding(true);//需要把根布局设置为这个属性 子布局则不会占用状态栏位置
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
        SystemTintManager tintManager = new SystemTintManager(this);// 创建状态栏的管理实例
        tintManager.setStatusBarTintEnabled(true);// 激活状态栏设置
        tintManager.setStatusBarTintColor(getResources().getColor(R.color.white));//设置状态栏颜色
        tintManager.setStatusBarDarkMode(false, this);//false 状态栏字体颜色是白色 true 颜色是黑色
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initSystemBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSoftKeyboard();
    }

    protected void showProgressDialog(String title){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog
                .setPressText(title);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

    }

    protected void hideProgressDialog(){
        if(mProgressDialog!=null&&mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
    }
}
