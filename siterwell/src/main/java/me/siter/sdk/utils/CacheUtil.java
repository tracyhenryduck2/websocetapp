package me.siter.sdk.utils;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

import me.siter.sdk.Constants;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: 缓存的封装
 */
public class CacheUtil {

    public static void init(Context context) {
        SpCache.init(context);
    }

    public static void setUserToken(String access_token, String refresh_token,String user_id) {
        SpCache.putString(Constants.JWT_TOKEN, access_token);
        SpCache.putString(Constants.REFRESH_TOKEN, refresh_token);
        SpCache.putString(Constants.USER_ID, user_id);
    }

    public static void setUserLoginInfo(String username,String password){
        SpCache.putString(Constants.USER_NAME, username);
        SpCache.putString(Constants.USER_PASSWORD, password);
    }

    public static String getUserToken() {
        return SpCache.getString(Constants.JWT_TOKEN, "");
    }

    public static String getRefreshToken(){
        return SpCache.getString(Constants.REFRESH_TOKEN,"");
    }

    public static String getUserName(){ return SpCache.getString(Constants.USER_NAME,""); }

    public static String getUserPassword(){ return SpCache.getString(Constants.USER_PASSWORD,""); }

    public static void setCloudUrls(Set<String> urls) {
        if (urls == null || urls.size() == 0) {
            SpCache.putString(Constants.CLOUD_CHANNELS, "");
            return;
        }
        try {
            JSONArray jsonArray = new JSONArray();
            for (String domain : urls) {
                JSONObject object = new JSONObject();
                object.put("url", domain);
                jsonArray.put(object);
            }
            SpCache.putString(Constants.CLOUD_CHANNELS, jsonArray.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Set<String> getCloudUrls() {
        String string = SpCache.getString(Constants.CLOUD_CHANNELS, "");
        Set<String> urls = new HashSet<>();
        try {
            JSONArray jsonArray = new JSONArray(string);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                if (object.has("url")) {
                    String url = object.getString("url");
                    urls.add(url);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return urls;
    }

    public static String getString(String key, String defValue) {
        return SpCache.getString(key, defValue);
    }

    public static void putString(String key, String value) {
        SpCache.putString(key, value);
    }
}
