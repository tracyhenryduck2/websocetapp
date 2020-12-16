package me.hekr.sdk;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import me.hekr.sdk.http.HekrRawCallback;
import me.hekr.sdk.http.HttpResponse;
import me.hekr.sdk.http.IHttpClient;
import me.hekr.sdk.http.PostRequest;
import me.hekr.sdk.inter.HekrCallback;
import me.hekr.sdk.utils.CacheUtil;
import me.hekr.sdk.utils.LogUtil;

/**
 * Created by hucn on 2017/3/17.
 * Author: hucn
 * Description: 配置用户信息接口的实现
 */
public class HekrUser implements IHekrUser {

    private static final String TAG = HekrUser.class.getSimpleName();

    private IHttpClient mHttpClient;
    private String mToken;
    private String mRefreshToken;
    private String mUserId;

    HekrUser() {
        mHttpClient = Hekr.getHttpClient();
        init();
    }

    /**
     * 初始化用户信息
     */
    private void init() {
        // 初始化的时候先读取token
        mToken = CacheUtil.getString(Constants.JWT_TOKEN, "");
        mRefreshToken = CacheUtil.getString(Constants.REFRESH_TOKEN, "");
        mUserId = CacheUtil.getString(Constants.USER_ID, "");
        LogUtil.d(TAG, "init: " + "token = " + mToken + ", refresh token = " + mRefreshToken + ", user id = " + mUserId);
    }

    /**
     * js中调用登录操作
     */
    public void login(String username, String password, final HekrRawCallback callback) {
        try {
            String url = TextUtils.concat(Constants.UrlUtil.BASE_UAA_URL, Constants.UrlUtil.UAA_LOGIN_URL).toString();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", username);
            jsonObject.put("password", password);
            jsonObject.put("pid", HekrSDK.getPid());
            jsonObject.put("clientType", "ANDROID");
            Log.e("login", "login url=" + url);
            PostRequest request = new PostRequest(url, jsonObject, new HttpResponse() {
                @Override
                public void onSuccess(int code, Map<String, String> headers, byte[] bytes) {

                    try {
                        JSONObject jsonObject = new JSONObject(new String(bytes));
                        JSONObject data = jsonObject.getJSONObject("data");
                        int errCode = jsonObject.getInt("code");
                        if(errCode==200){
                            refreshUserInfo(data.toString());
                            Hekr.getHekrClient().disconnect();
                            Hekr.getHekrClient().connect();
                            callback.onSuccess(code, bytes);
                        }else{
                            callback.onError(errCode,bytes);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onError(2, bytes);
                    }

                }

                @Override
                public void onError(int code, Map<String, String> headers, byte[] bytes) {
                    LogUtil.e(TAG, new String(bytes));
                    callback.onError(code, bytes);
                }
            });
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("Accept", "application/json");
            request.setHeaders(headers);
            mHttpClient.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 登录
     */
    public void login(String username, String password, final HekrCallback callback) {
        try {
            String url = TextUtils.concat(Constants.UrlUtil.BASE_UAA_URL, Constants.UrlUtil.UAA_LOGIN_URL).toString();
            Log.e("login", "login url=" + url);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", username);
            jsonObject.put("password", password);
            jsonObject.put("pid", HekrSDK.getPid());
            jsonObject.put("login_type", 0);
            PostRequest request = new PostRequest(url, jsonObject, new HttpResponse() {
                @Override
                public void onSuccess(int code, Map<String, String> headers, byte[] bytes) {
                    try {
                        JSONObject jsonObject = new JSONObject(new String(bytes));
                        int errCode = jsonObject.getInt("code");
                        JSONObject data = jsonObject.getJSONObject("data");
                        if(errCode==200){
                            refreshUserInfo(data.toString());
                            Hekr.getHekrClient().disconnect();
                            Hekr.getHekrClient().connect();
                            callback.onSuccess();
                        }else{
                            callback.onError(errCode,new String(bytes));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onError(2, new String(bytes));
                    }
                }

                @Override
                public void onError(int code, Map<String, String> headers, byte[] bytes) {
                    LogUtil.e(TAG, new String(bytes));
                    // TODO: 2017/4/11 确定错误码和错误信息
                    callback.onError(code, new String(bytes));
                }
            });
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("Accept", "application/json");
            request.setHeaders(headers);
            mHttpClient.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void logout(HekrCallback callback) {
        LogUtil.d(TAG, "logout");
        CacheUtil.setUserToken("", "","");
        mToken = "";
        mRefreshToken = "";
        mUserId = "";
        Hekr.getHekrClient().clearHosts();
        if (callback != null) {
            callback.onSuccess();
        }
    }

    @Override
    public String getToken() {
        if (TextUtils.isEmpty(mToken)) {
            return CacheUtil.getUserToken();
        }
        return mToken;
    }

    @Override
    public String getUserId() {
        return mUserId;
    }

    @Override
    public void refreshToken(final HekrRawCallback callback) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("refresh_token", mRefreshToken);
            String refresh_url = TextUtils.concat(Constants.UrlUtil.BASE_UAA_URL, Constants.UrlUtil.UAA_REFRESH_TOKEN).toString();
            PostRequest request = new PostRequest(refresh_url, jsonObject, new HttpResponse() {
                /**
                 * 刷新成功
                 */
                @Override
                public void onSuccess(int code, Map<String, String> headers, byte[] bytes) {
                    refreshUserInfo(new String(bytes));
                    Hekr.getHekrClient().disconnect();
                    Hekr.getHekrClient().connect();
                    callback.onSuccess(code, bytes);
                }

                /**
                 * token过期，应该退出登录
                 */
                @Override
                public void onError(int code, Map<String, String> headers, byte[] bytes) {
                    callback.onError(code, bytes);
                }
            });

            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("Accept", "application/json");
            request.setHeaders(headers);
            mHttpClient.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 重新设置用户token
     */
    public void refreshUserInfo(String info) {
        try {
            JSONObject jsonObject = new JSONObject(info);
            LogUtil.d(TAG, "Refresh user info :" + jsonObject.toString());
            mToken = jsonObject.getString("access_token");
            mRefreshToken = jsonObject.getString("refresh_token");
            mUserId = jsonObject.getString("user_id");
            CacheUtil.setUserToken(mToken, mRefreshToken,mUserId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断是否token过期
     */
    @Override
    public boolean tokenIsExpired(int httpErrorCode, byte[] bytes) {
        if (httpErrorCode == 403) {
            if (bytes != null && bytes.length > 0) {
                String str = new String(bytes);
                try {
                    JSONObject object = new JSONObject(str);
                    if (object.has("status")) {
                        return object.getInt("status") == 403;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 登录成功后的String，适合第三方登录等调用之后调用此方法
     */
    @Override
    public void setToken(String jsonString) {
        refreshUserInfo(jsonString);
        Hekr.getHekrClient().disconnect();
        Hekr.getHekrClient().connect();
    }
}
