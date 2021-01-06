package me.siter.sdk.http.bean;

import java.io.Serializable;

/*
@class TranslateBean
@autor Administrator
@time 2017/10/16 13:52
@email xuejunju_4595@qq.com
*/
public class TranslateBean implements Serializable {

    private static final long serialVersionUID = -5740445062596082701L;

    private String zh_CN;
    private String en_US;

    public String getZh_CN() {
        return zh_CN;
    }

    public void setZh_CN(String zh_CN) {
        this.zh_CN = zh_CN;
    }

    public String getEn_US() {
        return en_US;
    }

    public void setEn_US(String en_US) {
        this.en_US = en_US;
    }

    @Override
    public String toString() {
        return "TranslateBean{" +
                "zh_CN='" + zh_CN + '\'' +
                ", en_US='" + en_US + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TranslateBean)) return false;

        TranslateBean that = (TranslateBean) o;

        if (!getZh_CN().equals(that.getZh_CN())) return false;
        return getEn_US().equals(that.getEn_US());

    }

    @Override
    public int hashCode() {
        int result = getZh_CN().hashCode();
        result = 31 * result + getEn_US().hashCode();
        return result;
    }
}
