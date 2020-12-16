package me.siter.sdk.dispatcher;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import me.siter.sdk.utils.LogUtil;
import me.siter.sdk.utils.ProtocolFilterUtil;

/**
 * 消息过滤接口的默认实现。
 *
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 */
public class MessageFilter implements IMessageFilter {

    private static final String TAG = MessageFilter.class.getSimpleName();

    private JSONObject mRule;

    /**
     * 构造方法，传入JSONObject格式的过滤规则。
     *
     * @param rule 过滤规则
     */
    public MessageFilter(JSONObject rule) {
        this.mRule = rule;
    }

    @Override
    public synchronized boolean doFilter(String in) {
        LogUtil.d(TAG, "Filter matching rule is:" + mRule);
        // 如果能够通过这个过滤器，那么返回True，否则返回False
        JSONObject jsonObject;
        if (!TextUtils.isEmpty(in)) {
            try {
                jsonObject = new JSONObject(in);
                return ProtocolFilterUtil.dictMatch(mRule, jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 获取当前过滤器的过滤规则。
     *
     * @return JSONObject 过滤规则
     */
    public JSONObject getRule() {
        return mRule;
    }

    /**
     * 设置当前过滤器的过滤规则。
     *
     * @param rule 过滤规则
     */
    public void setRule(JSONObject rule) {
        this.mRule = rule;
    }

    @Override
    public String toString() {
        return "MessageFilter{" +
                "mRule=" + mRule +
                '}';
    }
}
