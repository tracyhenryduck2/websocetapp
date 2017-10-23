package com.siterwell.sdk.event;


import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2017/10/16.
 */

public class HttpErrorEvent {

    public String url;
    public String entity = "";
    public Integer httpCode;
    public String message;
    public String heads;


    public HttpErrorEvent(String url, String entity, String heads, Integer httpCode, byte[] bytes) {
        this.url = url;
        this.heads = heads;
        this.entity = entity;
        this.httpCode = httpCode;
        if (bytes != null && bytes.length > 0) {
            this.message = new String(bytes);
        }
    }


    public HttpErrorEvent() {
    }

    /**
     * 上报数据
     */
    public static void event(String url, String heads, String entity, Integer httpCode, byte[] bytes) {
        if (httpCode > 300) {
            EventBus.getDefault().post(new HttpErrorEvent(url, entity, heads, httpCode, bytes));
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHeads() {
        return heads;
    }

    public void setHeads(String heads) {
        this.heads = heads;
    }

    public Integer getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(Integer httpCode) {
        this.httpCode = httpCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return url +
                "\n" +
                entity +
                "\n" +
                heads +
                "\n" +
                "HTTP-" + String.valueOf(httpCode) +
                "\n" +
                message;
    }

}
