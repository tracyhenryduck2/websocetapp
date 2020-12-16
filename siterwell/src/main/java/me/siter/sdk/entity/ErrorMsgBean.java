package me.siter.sdk.entity;

import java.io.Serializable;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 **/
public class ErrorMsgBean implements Serializable {

    private static final long serialVersionUID = -8610440979394689380L;
    /**
     * code : 3400005
     * desc : Verify code send too many
     * timestamp : 1460431878014
     */

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ErrorMsgBean that = (ErrorMsgBean) o;

        if (code != that.code) return false;
        if (timestamp != that.timestamp) return false;
        return desc != null ? desc.equals(that.desc) : that.desc == null;

    }

    @Override
    public int hashCode() {
        int result = code;
        result = 31 * result + (desc != null ? desc.hashCode() : 0);
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        return result;
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
