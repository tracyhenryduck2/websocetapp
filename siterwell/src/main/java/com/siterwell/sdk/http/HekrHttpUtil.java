package com.siterwell.sdk.http;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.litesuits.common.assist.Network;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.siterwell.sdk.http.HttpErrorEvent;
import com.siterwell.sdk.http.bean.JWTBean;

import java.util.Arrays;

import cz.msebera.android.httpclient.Header;

/*
@class HekrHttpUtil
@autor Administrator
@time 2017/10/16 14:16
@email xuejunju_4595@qq.com
*/
public class HekrHttpUtil {

    //private static AtomicReference<Toastor> toastor = new AtomicReference<>();
    private final static int HTTP_GET = 0;
    private final static int HTTP_POST = 1;
    private final static int HTTP_PUT = 2;
    private final static int HTTP_PATCH = 3;
    private final static int HTTP_DELETE = 4;
    private final static int HTTP_POST_FILE = 5;

    /**
     * 带token的get
     * 并且支持刷新token
     *
     * @param context             context
     * @param JWT_TOKEN           指令格式中的参数token，是APP调用 认证授权API : 登录 接口返回的JWT Token，有效期为12小时。如果APP和云端建立通道前token已过期，云端会提示token无效；如果联网之后连接不断，即使token过期，APP还能继续控制设备。
     * @param ReFresh_Token       刷新token
     * @param url                 构造网址
     * @param getHekrDataListener 回调方法
     */
    public static void getDataReFreshToken(final Context context, final String JWT_TOKEN, final String ReFresh_Token, final String url, Header[] headers, final GetHekrDataWithTokenListener getHekrDataListener) {
        hekr_http(HTTP_GET, context, url, headers, JWT_TOKEN, ReFresh_Token, null, null, getHekrDataListener);
    }

    /**
     * 带token的网络post  runInUI
     * 支持刷新token
     *
     * @param context             context
     * @param JWT_TOKEN           指令格式中的参数token，是APP调用 认证授权API : 登录 接口返回的JWT Token，有效期为12小时。如果APP和云端建立通道前token已过期，云端会提示token无效；如果联网之后连接不断，即使token过期，APP还能继续控制设备。
     * @param url                 构造网址
     * @param entity              String JSON形式的string
     * @param getHekrDataListener 回调
     */
    public static void postDataReFreshToken(final Context context, final String JWT_TOKEN, final String ReFresh_Token, final String url, Header[] headers, final String entity, final GetHekrDataWithTokenListener getHekrDataListener) {
        hekr_http(HTTP_POST, context, url, headers, JWT_TOKEN, ReFresh_Token, entity, null, getHekrDataListener);
    }


    /**
     * 带token的DELETE
     * 支持刷新token
     *
     * @param context             context
     * @param JWT_TOKEN           指令格式中的参数token，是APP调用 认证授权API : 登录 接口返回的JWT Token，有效期为12小时。如果APP和云端建立通道前token已过期，云端会提示token无效；如果联网之后连接不断，即使token过期，APP还能继续控制设备。
     * @param url                 构造网址
     * @param getHekrDataListener 回调方法
     */
    public static void deleteDataReFreshToken(final Context context, final String JWT_TOKEN, final String ReFresh_Token, final String url, Header[] headers, final GetHekrDataWithTokenListener getHekrDataListener) {
        hekr_http(HTTP_DELETE, context, url, headers, JWT_TOKEN, ReFresh_Token, null, null, getHekrDataListener);
    }

    /**
     * 带token的PATCH
     * 支持刷新token
     *
     * @param context             context
     * @param JWT_TOKEN           指令格式中的参数token，是APP调用 认证授权API : 登录 接口返回的JWT Token，有效期为12小时。如果APP和云端建立通道前token已过期，云端会提示token无效；如果联网之后连接不断，即使token过期，APP还能继续控制设备。
     * @param ReFresh_Token       刷新token
     * @param url                 构造网址
     * @param entity              请求体
     * @param getHekrDataListener 回调方法
     */
    public static void patchDataToken(final Context context, final String JWT_TOKEN, final String ReFresh_Token, final String url, Header[] headers, final String entity, final GetHekrDataWithTokenListener getHekrDataListener) {
        hekr_http(HTTP_PATCH, context, url, headers, JWT_TOKEN, ReFresh_Token, entity, null, getHekrDataListener);
    }


    /**
     * 带token的PUT
     *
     * @param context             context
     * @param JWT_TOKEN           指令格式中的参数token，是APP调用 认证授权API : 登录 接口返回的JWT Token，有效期为12小时。如果APP和云端建立通道前token已过期，云端会提示token无效；如果联网之后连接不断，即使token过期，APP还能继续控制设备。
     * @param url                 构造网址
     * @param entity              请求体
     * @param getHekrDataListener 回调方法
     */
    public static void putDataRefreshToken(final Context context, String JWT_TOKEN, final String ReFresh_Token, final String url, Header[] headers, final String entity, final GetHekrDataWithTokenListener getHekrDataListener) {
        hekr_http(HTTP_PUT, context, url, headers, JWT_TOKEN, ReFresh_Token, entity, null, getHekrDataListener);
    }


    /**
     * 带token的网络post  runInUI
     * 支持刷新token
     *
     * @param context             context
     * @param JWT_TOKEN           指令格式中的参数token，是APP调用 认证授权API : 登录 接口返回的JWT Token，有效期为12小时。如果APP和云端建立通道前token已过期，云端会提示token无效；如果联网之后连接不断，即使token过期，APP还能继续控制设备。
     * @param ReFresh_Token       刷新方法
     * @param url                 构造网址
     * @param params              表单
     * @param getHekrDataListener 回调
     */
    public static void postFileReFreshToken(final Context context, final String JWT_TOKEN, final String ReFresh_Token, final String url, final RequestParams params, final GetHekrDataWithTokenListener getHekrDataListener) {
        hekr_http(HTTP_POST_FILE, context, url, JWT_TOKEN, ReFresh_Token, null, params, getHekrDataListener);
    }

    private static String byte2Str(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        } else {
            return new String(bytes);
        }
    }


    /**
     * 刷新token
     *
     * @param context              context
     * @param ReFresh_Token        RefreshToken
     * @param refreshTokenListener 回调接口
     */
    private synchronized static void refreshToken(final Context context, String ReFresh_Token, final HekrHttpUtil.RefreshTokenListener refreshTokenListener) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("refresh_token", ReFresh_Token);
        String refresh_url = TextUtils.concat(SiterConstantsUtil.UrlUtil.BASE_UAA_URL, SiterConstantsUtil.UrlUtil.UAA_REFRESH_TOKEN).toString();
        BaseHttpUtil.postData(context, refresh_url, jsonObject.toJSONString(), new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                Log.d(SiterConstantsUtil.HEKR_SDK, "token刷新成功:" + new String(bytes));
                refreshTokenListener.refreshSuccess(JSONObject.parseObject(new String(bytes), JWTBean.class));
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (bytes != null && bytes.length > 0) {
                    if (HekrCodeUtil.getErrorCode("刷新token", i, bytes) != 400016) {
                        // HTTP-429{"code":400016,"desc":"Rate limit exceeded","timestamp":1471830933243}
                        refreshTokenListener.refreshFail(i, headers, bytes);
                        //此时token过期，直接强制退出APP
                        //HTTP-403{"code":3400015,"desc":"Invalid token","timestamp":1471831141631}
                    }
                }
            }
        });
    }




    /**
     * 带token过期验证的AsyncHttpResponseHandler
     */
    private static class AsyncHttpHandlerWithTokenTimeOut extends AsyncHttpResponseHandler {

        private ReloadListener reloadListener;
        private String refresh_Token = null;
        private Context context;
        private GetHekrDataWithTokenListener getHekrDataListener;
        private String entity = "";

        public AsyncHttpHandlerWithTokenTimeOut(Context context, GetHekrDataWithTokenListener getHekrDataListener, String reFresh_Token, String entity, ReloadListener reloadListener) {
            this.context = context;
            this.getHekrDataListener = getHekrDataListener;
            this.reloadListener = reloadListener;
            this.refresh_Token = reFresh_Token;
            this.entity = entity;
        }

        public AsyncHttpHandlerWithTokenTimeOut(GetHekrDataWithTokenListener getHekrDataListener) {
            this.getHekrDataListener = getHekrDataListener;
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            getHekrDataListener.getDataSuccess(byte2Str(responseBody));
        }

        @Override
        public void onProgress(long bytesWritten, long totalSize) {
            super.onProgress(bytesWritten, totalSize);
            getHekrDataListener.getDataProgress(bytesWritten, totalSize);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            // HekrCodeUtil.getErrorCode(statusCode, responseBody);
            if (TextUtils.isEmpty(refresh_Token)) {
                getHekrDataListener.getDataFail(HekrCodeUtil.getErrorCode(statusCode, responseBody));
            } else {
                if (HekrCodeUtil.getTimeOutFlag(statusCode, responseBody)) {
                    refreshToken(context, refresh_Token, new RefreshTokenListener() {
                        @Override
                        public void refreshSuccess(JWTBean jwtBean) {
                            getHekrDataListener.getToken(jwtBean);
                            reloadListener.reload(jwtBean);
                        }

                        @Override
                        public void refreshFail(int i, Header[] headers, byte[] bytes) {
                            getHekrDataListener.getDataFail(SiterConstantsUtil.ErrorCode.TOKEN_TIME_OUT);
                        }
                    });
                } else {
                    //自定义事件
                    HttpErrorEvent.event(getRequestURI().toString(),  Arrays.asList(getRequestHeaders()).toString(),entity, statusCode, responseBody);
                    getHekrDataListener.getDataFail(HekrCodeUtil.getErrorCode(getRequestURI().toString(), statusCode, responseBody));
                }
            }
        }

    }


    /**
     * token过期后重新拉取新token接口
     */
    private interface ReloadListener {
        void reload(JWTBean jwtBean);
    }

    /**
     * 3.12刷新token接口
     */
    private interface RefreshTokenListener {
        void refreshSuccess(JWTBean jwtBean);

        void refreshFail(int i, Header[] headers, byte[] bytes);
    }

    private static void hekr_http(int http_type, final Context context, final String url, final String JWT_TOKEN, final String ReFresh_Token, final String entity, final RequestParams params, final GetHekrDataWithTokenListener getHekrDataListener) {
        hekr_http(http_type, context, url, null, JWT_TOKEN, ReFresh_Token, entity, params, getHekrDataListener);
    }


    /**
     * http请求
     */
    private static void hekr_http(int http_type, final Context context, final String url, final Header[] headers, final String JWT_TOKEN, final String ReFresh_Token, final String entity, final RequestParams params, final GetHekrDataWithTokenListener getHekrDataListener) {
        if (TextUtils.isEmpty(JWT_TOKEN) || TextUtils.isEmpty(ReFresh_Token) || TextUtils.isEmpty(url)) {
            getHekrDataListener.getDataFail(SiterConstantsUtil.ErrorCode.TOKEN_TIME_OUT);
            Log.e(SiterConstantsUtil.HEKR_SDK_ERROR, "Token or url is null\n" + "token:" + JWT_TOKEN + "url\n" + url);
        } else {
            if (Network.isConnected(context)) {
                switch (http_type) {
                    case HTTP_GET:
                        BaseHttpUtil.getDataToken(context, JWT_TOKEN, url, headers, new AsyncHttpHandlerWithTokenTimeOut(context, getHekrDataListener, ReFresh_Token, "", new ReloadListener() {
                            @Override
                            public void reload(JWTBean jwtBean) {
                                BaseHttpUtil.getDataToken(context, jwtBean.getAccessToken(), url, headers, new AsyncHttpHandlerWithTokenTimeOut(getHekrDataListener));
                            }
                        }));
                        break;
                    case HTTP_POST:
                        BaseHttpUtil.postDataToken(context, JWT_TOKEN, url, headers, entity, new AsyncHttpHandlerWithTokenTimeOut(context, getHekrDataListener, ReFresh_Token, entity, new ReloadListener() {
                            @Override
                            public void reload(JWTBean jwtBean) {
                                BaseHttpUtil.postDataToken(context, jwtBean.getAccessToken(), url, headers, entity, new AsyncHttpHandlerWithTokenTimeOut(getHekrDataListener));
                            }
                        }));
                        break;
                    case HTTP_PUT:
                        BaseHttpUtil.putDataToken(context, JWT_TOKEN, url, headers, entity, new AsyncHttpHandlerWithTokenTimeOut(context, getHekrDataListener, ReFresh_Token, entity, new ReloadListener() {
                            @Override
                            public void reload(JWTBean jwtBean) {
                                BaseHttpUtil.putDataToken(context, jwtBean.getAccessToken(), url, headers, entity, new AsyncHttpHandlerWithTokenTimeOut(getHekrDataListener));
                            }
                        }));
                        break;
                    case HTTP_DELETE:
                        BaseHttpUtil.deleteDataToken(context, JWT_TOKEN, url, headers, new AsyncHttpHandlerWithTokenTimeOut(context, getHekrDataListener, ReFresh_Token, "", new ReloadListener() {
                            @Override
                            public void reload(JWTBean jwtBean) {
                                BaseHttpUtil.deleteDataToken(context, jwtBean.getAccessToken(), url, headers, new AsyncHttpHandlerWithTokenTimeOut(getHekrDataListener));
                            }
                        }));
                        break;
                    case HTTP_PATCH:
                        BaseHttpUtil.patchDataToken(context, JWT_TOKEN, url, headers, entity, new AsyncHttpHandlerWithTokenTimeOut(context, getHekrDataListener, ReFresh_Token, entity, new ReloadListener() {
                            @Override
                            public void reload(JWTBean jwtBean) {
                                BaseHttpUtil.patchDataToken(context, jwtBean.getAccessToken(), url, headers, entity, new AsyncHttpHandlerWithTokenTimeOut(getHekrDataListener));
                            }
                        }));
                        break;
                    case HTTP_POST_FILE:
                        BaseHttpUtil.postFileToken(context, JWT_TOKEN, url, params, new AsyncHttpHandlerWithTokenTimeOut(context, getHekrDataListener, ReFresh_Token, "", new ReloadListener() {
                            @Override
                            public void reload(JWTBean jwtBean) {
                                BaseHttpUtil.postFileToken(context, jwtBean.getAccessToken(), url, params, new AsyncHttpHandlerWithTokenTimeOut(getHekrDataListener));
                            }
                        }));
                        break;
                    default:
                        getHekrDataListener.getDataFail(SiterConstantsUtil.ErrorCode.UNKNOWN_ERROR);
                        break;
                }
            } else {
                Log.e(SiterConstantsUtil.HEKR_SDK_ERROR, url + "\n" + SiterConstantsUtil.NETWORK_ERROR);
                getHekrDataListener.getDataFail(SiterConstantsUtil.ErrorCode.NETWORK_TIME_OUT);
            }
        }
    }
}
