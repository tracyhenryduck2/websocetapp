package com.siterwell.sdk.bean;

import com.siterwell.sdk.listener.DataReceiverListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/16.
 */

public class FilterBean implements Serializable {
    private static final long serialVersionUID = 8000411235312201137L;
    private Object object;
    private JSONObject data;
    private JSONObject filter;
    private long timeStamp;
    private boolean once;
    private DataReceiverListener dataReceiverListener;

    public FilterBean(Object object, long timeStamp, JSONObject filter, boolean once, DataReceiverListener dataReceiverListener, JSONObject data) {
        this.object = object;
        this.timeStamp = timeStamp;
        this.filter = filter;
        this.once = once;
        this.dataReceiverListener = dataReceiverListener;
        this.data = data;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public JSONObject getFilter() {
        return filter;
    }

    public void setFilter(JSONObject filter) {
        this.filter = filter;
    }

    public boolean isOnce() {
        return once;
    }

    public void setOnce(boolean once) {
        this.once = once;
    }

    public DataReceiverListener getDataReceiverListener() {
        return dataReceiverListener;
    }

    public void setDataReceiverListener(DataReceiverListener dataReceiverListener) {
        this.dataReceiverListener = dataReceiverListener;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    //前端页面点击左上角退出，并未调用onClose，防止receive过滤器重复设置问题重写equals方法
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FilterBean)) return false;

        FilterBean that = (FilterBean) o;
        try {
            if(getFilter().getJSONObject("params").has("subDevTid")&&that.getFilter().getJSONObject("params").has("subDevTid")){
                return getFilter().getJSONObject("params").getString("subDevTid").equals(that.getFilter().getJSONObject("params").getString("subDevTid")) &&
                        getFilter().getString("action").equals(that.getFilter().getString("action")) && !isOnce() && !that.isOnce();
            }else if(getFilter().getJSONObject("params").has("devTid")&&that.getFilter().getJSONObject("params").has("devTid")){
                return getFilter().getJSONObject("params").getString("devTid").equals(that.getFilter().getJSONObject("params").getString("devTid")) &&
                        getFilter().getString("action").equals(that.getFilter().getString("action")) && !isOnce() && !that.isOnce();
            }else{
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int hashCode() {
        return getFilter().hashCode();
    }

    @Override
    public String toString() {
        return "{" + "filter=" + filter + ", once=" + once + '}';
    }
}
