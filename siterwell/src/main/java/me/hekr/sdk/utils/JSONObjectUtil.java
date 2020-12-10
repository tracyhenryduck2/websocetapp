package me.hekr.sdk.utils;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hucn on 2017/3/28.
 * Author:
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
