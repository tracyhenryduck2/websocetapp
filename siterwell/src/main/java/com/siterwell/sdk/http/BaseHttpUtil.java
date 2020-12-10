package com.siterwell.sdk.http;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.Arrays;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import me.hekr.sdk.utils.HekrLanguageUtil;

import static com.loopj.android.http.AsyncHttpClient.HEADER_CONTENT_TYPE;

/*
@class BaseHttpUtil
@autor Administrator
@time 2017/10/16 13:53
@email xuejunju_4595@qq.com
*/
public class BaseHttpUtil {
    private static final String TAG = "HEKR_BaseHttpUtil";

    private static AsyncHttpClient client = new AsyncHttpClient();

    static {
        client.setTimeout(5 * 1000);
        client.setURLEncodingEnabled(false);
        client.addHeader("Accept-Language", HekrLanguageUtil.toBcp47Language(Locale.getDefault()));
    }

    public static AsyncHttpClient getClient() {
        return client;
    }


    /**
     * 普通get方法
     *
     * @param context                  context
     * @param url                      构造方法
     * @param asyncHttpResponseHandler 回调方法
     */
    public static void getData(Context context, String url, final AsyncHttpResponseHandler asyncHttpResponseHandler) {
        Log.d(TAG, url + "\n");
        getData(context, url, null, asyncHttpResponseHandler);
    }


    /**
     * 普通get方法
     *
     * @param context                  context
     * @param url                      url
     * @param headers                  headers
     * @param asyncHttpResponseHandler 回调方法
     */
    public static void getData(Context context, String url, Header[] headers, final AsyncHttpResponseHandler asyncHttpResponseHandler) {
        if (headers == null || headers.length == 0) {
            client.get(context, url, asyncHttpResponseHandler);
        } else {
            client.get(context, url, headers, null, asyncHttpResponseHandler);
        }
    }


    /**
     * 普通post数据
     *
     * @param url                      构造网址
     * @param entity                   String类型的json
     * @param asyncHttpResponseHandler 回调方法
     */
    public static void postData(Context context, String url, String entity, final AsyncHttpResponseHandler asyncHttpResponseHandler) {
        Log.d(TAG, url + "\n" + entity);
        StringEntity entity_post = getEntity(entity);
        client.post(context, url, entity_post, "application/json", asyncHttpResponseHandler);
    }

    /**
     * 带token的get
     *
     * @param context                  context
     * @param JWT_TOKEN                指令格式中的参数token，是APP调用 认证授权API : 登录 接口返回的JWT Token，有效期为12小时。如果APP和云端建立通道前token已过期，云端会提示token无效；如果联网之后连接不断，即使token过期，APP还能继续控制设备。
     * @param url                      构造网址
     * @param asyncHttpResponseHandler 回调方法
     */
    public static void getDataToken(Context context, String JWT_TOKEN, String url, Header[] headers, final AsyncHttpResponseHandler asyncHttpResponseHandler) {
        Log.d(TAG, url + "\n" + JWT_TOKEN);
        client.get(context, url, getFinalHeader(JWT_TOKEN, headers), null, asyncHttpResponseHandler);
    }

    /**
     * 带token的网络post  runInUI
     *
     * @param context                  context
     * @param JWT_TOKEN                指令格式中的参数token，是APP调用 认证授权API : 登录 接口返回的JWT Token，有效期为12小时。如果APP和云端建立通道前token已过期，云端会提示token无效；如果联网之后连接不断，即使token过期，APP还能继续控制设备。
     * @param url                      构造网址
     * @param entity                   String JSON形式的string
     * @param asyncHttpResponseHandler 回调方法
     */
    public static void postDataToken(Context context, String JWT_TOKEN, String url, Header[] headers, String entity, final AsyncHttpResponseHandler asyncHttpResponseHandler) {
        StringEntity entity_post = getEntity(entity);
        Log.d(TAG, url + "\n" + JWT_TOKEN + "\n" + entity);
        client.post(context, url, getFinalHeader(JWT_TOKEN, headers), entity_post, "application/json", asyncHttpResponseHandler);
    }

    /**
     * 带token的DELETE
     *
     * @param context                  context
     * @param JWT_TOKEN                指令格式中的参数token，是APP调用 认证授权API : 登录 接口返回的JWT Token，有效期为12小时。如果APP和云端建立通道前token已过期，云端会提示token无效；如果联网之后连接不断，即使token过期，APP还能继续控制设备。
     * @param url                      构造网址
     * @param asyncHttpResponseHandler 回调方法
     */
    public static void deleteDataToken(Context context, String JWT_TOKEN, String url, Header[] headers, final AsyncHttpResponseHandler asyncHttpResponseHandler) {
        Log.d(TAG, url + "\n" + JWT_TOKEN);
        BasicHeader basicHeader1 = new BasicHeader("Authorization", "Bearer " + JWT_TOKEN);
        BasicHeader basicHeader2 = new BasicHeader(HEADER_CONTENT_TYPE, "application/json");
        BasicHeader[] firstHeaders = new BasicHeader[]{basicHeader1, basicHeader2};
        Header[] finalHeader = (headers == null || headers.length == 0) ? firstHeaders : concat(firstHeaders, headers);
        client.delete(context, url, finalHeader, asyncHttpResponseHandler);
    }

    /**
     * 带token的PATCH
     *
     * @param context                  context
     * @param JWT_TOKEN                指令格式中的参数token，是APP调用 认证授权API : 登录 接口返回的JWT Token，有效期为12小时。如果APP和云端建立通道前token已过期，云端会提示token无效；如果联网之后连接不断，即使token过期，APP还能继续控制设备。
     * @param url                      构造网址
     * @param asyncHttpResponseHandler 回调方法
     */
    public static void patchDataToken(Context context, String JWT_TOKEN, String url, Header[] headers, String entity, final AsyncHttpResponseHandler asyncHttpResponseHandler) {
        Log.d(TAG, url + "\n" + JWT_TOKEN + "\n" + entity);
        StringEntity entity_post = getEntity(entity);
        client.patch(context, url, getFinalHeader(JWT_TOKEN, headers), entity_post, "application/json", asyncHttpResponseHandler);
    }

    /**
     * 带token的PUT
     *
     * @param context                  context
     * @param JWT_TOKEN                指令格式中的参数token，是APP调用 认证授权API : 登录 接口返回的JWT Token，有效期为12小时。如果APP和云端建立通道前token已过期，云端会提示token无效；如果联网之后连接不断，即使token过期，APP还能继续控制设备。
     * @param url                      构造网址
     * @param asyncHttpResponseHandler 回调方法
     */
    public static void putDataToken(Context context, String JWT_TOKEN, String url, Header[] headers, String entity, final AsyncHttpResponseHandler asyncHttpResponseHandler) {
        Log.d(TAG, url + "\n" + JWT_TOKEN + "\n" + entity);
        StringEntity entity_post = getEntity(entity);
        client.put(context, url, getFinalHeader(JWT_TOKEN, headers), entity_post, "application/json", asyncHttpResponseHandler);
    }

    /**
     * 带token的网络post  runInUI
     *
     * @param context                  context
     * @param JWT_TOKEN                指令格式中的参数token，是APP调用 认证授权API : 登录 接口返回的JWT Token，有效期为12小时。如果APP和云端建立通道前token已过期，云端会提示token无效；如果联网之后连接不断，即使token过期，APP还能继续控制设备。
     * @param url                      构造网址
     * @param params                   参数
     * @param asyncHttpResponseHandler 回调方法
     */
    public static void postFileToken(Context context, String JWT_TOKEN, String url, RequestParams params, final AsyncHttpResponseHandler asyncHttpResponseHandler) {
        Log.d(TAG, url + "\n" + JWT_TOKEN + "\n" + params);
        client.post(context, url, getTokenHeader(JWT_TOKEN), params, null, asyncHttpResponseHandler);
    }


    private static Header[] getTokenHeader(String JWT_TOKEN) {
        return new BasicHeader[]{new BasicHeader("Authorization", "Bearer " + JWT_TOKEN)};
    }

    private static Header[] getFinalHeader(String JWT_TOKEN, Header[] headers) {
        return (headers == null || headers.length == 0) ? getTokenHeader(JWT_TOKEN) : concat(getTokenHeader(JWT_TOKEN), headers);
    }

    /**
     * 合并header
     */
    private static <T> T[] concat(T[] firstHeader, T[] secondHeader) {
        T[] result = Arrays.copyOf(firstHeader, firstHeader.length + secondHeader.length);
        System.arraycopy(secondHeader, 0, result, firstHeader.length, secondHeader.length);
        return result;
    }

    /**
     * entity处理
     *
     * @param entity entity
     * @return StringEntity
     */
    private static StringEntity getEntity(String entity) {
        StringEntity entity_post = null;
        if (!TextUtils.isEmpty(entity)) {
            entity_post = new StringEntity(entity, "utf-8");
        }
        return entity_post;
    }
}
