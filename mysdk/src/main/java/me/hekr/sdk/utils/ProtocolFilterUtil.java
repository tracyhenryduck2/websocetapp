package me.hekr.sdk.utils;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by hekr_xm on 2016/5/10.
 **/
public class ProtocolFilterUtil {

    public static boolean dictMatch(JSONObject filter, JSONObject data) {

        if (filter == null || data == null || filter.length() == 0 || data.length() == 0)
            return false;
        Iterator it = filter.keys();

        while (it.hasNext()) {
            try {
                boolean isMatch;
                String key = (String) it.next();
                Object value = filter.get(key);

                Object object = null;
                if (data.has(key)) {
                    object = data.get(key);
                }

                if (object instanceof JSONObject && value instanceof JSONObject) {
                    isMatch = dictMatch((JSONObject) value, (JSONObject) object);
                } else if (value == null) {
                    isMatch = (object != null);
                } else {
                    if (value instanceof String && object instanceof String) {
                        isMatch = TextUtils.equals(value.toString(), object.toString());
                    } else if (value instanceof Integer && object instanceof Integer) {
                        isMatch = (((Integer) value).intValue() == ((Integer) object).intValue());
                    } else if (value instanceof Float && object instanceof Float) {
                        isMatch = (((Float) value).floatValue() == ((Float) object).floatValue());
                    } else if (value instanceof Boolean && object instanceof Boolean) {
                        isMatch = (((Boolean) value).booleanValue() == ((Boolean) object).booleanValue());
                    } else if (value instanceof Long && object instanceof Long) {
                        isMatch = (((Long) value).longValue() == ((Long) object).longValue());
                    } else {
                        isMatch = false;
                    }
                }
                if (!isMatch) {
                    return false;
                }

            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
}
