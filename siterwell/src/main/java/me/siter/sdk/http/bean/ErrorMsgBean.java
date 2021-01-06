package me.siter.sdk.http.bean;

import java.io.Serializable;

/*
@class ErrorMsgBean
@autor Administrator
@time 2017/10/16 13:30
@email xuejunju_4595@qq.com
*/
public class ErrorMsgBean implements Serializable {


    private static final long serialVersionUID = -8610440979394689380L;

    private int code;
    private String desc;
    private long timestamp;


    public ErrorMsgBean() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public ErrorMsgBean(int code, String desc, long timestamp) {
        this.code = code;
        this.desc = desc;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ErrorMsgBean{" +
                "code=" + code +
                ", desc='" + desc + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
    
}
