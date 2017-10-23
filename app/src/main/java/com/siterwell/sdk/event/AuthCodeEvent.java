package com.siterwell.sdk.event;

import android.content.Intent;

/**
 * Created by Administrator on 2017/10/16.
 */

public class AuthCodeEvent {
    public static final int TYPE_WEIXIN = 1;
    public static final int TYPE_WEIBO = 2;

    private int type;
    private int requestCode;
    private int resultCode;
    private Intent data;
    private String code;

    public AuthCodeEvent(int type, int requestCode, int resultCode, Intent data) {
        this.type = type;
        this.requestCode = requestCode;
        this.resultCode = resultCode;
        this.data = data;
    }


    public AuthCodeEvent(int type, String code) {
        this.type = type;
        this.code = code;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getRequestCode() {
        return requestCode;
    }


    public int getResultCode() {
        return resultCode;
    }


    public Intent getData() {
        return data;
    }

    public void setData(Intent data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
