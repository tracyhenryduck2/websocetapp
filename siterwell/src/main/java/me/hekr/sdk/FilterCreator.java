package me.hekr.sdk;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import me.hekr.sdk.dispatcher.IMessageFilter;
import me.hekr.sdk.dispatcher.MessageFilter;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: Filter 的建造者
 */

public class FilterCreator {

    private Map<String, Object> mRules;
    private IMessageFilter mFilter;

    public FilterCreator() {
        mRules = new HashMap<>();
    }

    FilterCreator putRule(String key, String value) {
        mRules.put(key, value);
        return this;
    }

    FilterCreator setRules(Map<String, String> rules) {
        mRules.putAll(rules);
        return this;
    }

    MessageFilter create() {
        if (mRules.size() == 0) {
            throw new IllegalArgumentException("No rule found");
        }
        try {
            JSONObject object = new JSONObject();
            for (Map.Entry<String, Object> entry : mRules.entrySet()) {
                object.put(entry.getKey(), entry.getValue());
            }
            return new MessageFilter(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
