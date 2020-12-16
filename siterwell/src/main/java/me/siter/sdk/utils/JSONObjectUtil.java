package me.siter.sdk.utils;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description:
 */

public class JSONObjectUtil {

    public static JSONObject getJSONObject(String string) {
        JSONObject jsonObject = null;
        if (!TextUtils.isEmpty(string)) {
            try {
                jsonObject = new JSONObject(string);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }
}
