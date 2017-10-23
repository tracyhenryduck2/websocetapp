package com.siterwell.sdk.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.siterwell.siterapp.R;

/*
@class DebugViewWindow
@autor Administrator
@time 2017/10/16 13:39
@email xuejunju_4595@qq.com
*/
public class DebugViewWindow {
    private static WindowManager.LayoutParams mLayoutParams;
    private static WindowManager mWindowManager;
    private static View mView;
    public static boolean debug = false;

    public static void initView(final Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        debug = sharedPref.getBoolean("debug", false);

        mWindowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        mLayoutParams = new WindowManager.LayoutParams();
        mView = LayoutInflater.from(context).inflate(R.layout.debug, null);
        Button btn_debug = (Button) mView.findViewById(R.id.btn_debug);
        btn_debug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "debug", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 添加窗口
     */
    public static synchronized void showView(Context context) {
        if (mWindowManager == null) {
            initView(context);
        }
        if (debug) {
            try {
                mWindowManager.addView(mView, mLayoutParams);
            } catch (Exception e) {
            }
        }
    }


    /**
     * 移出窗口
     */
    public static void removeView() {
        try {
            mWindowManager.removeView(mView);
        } catch (Exception e) {
        }
    }
}
