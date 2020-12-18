package com.siterwell.sdk.http;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.litesuits.common.assist.Network;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.siterwell.sdk.http.bean.JWTBean;

import java.util.Arrays;

import cz.msebera.android.httpclient.Header;
import me.siter.sdk.Constants;

/*
@class HttpUtil
@autor Administrator
@time 2017/10/16 14:16
@email xuejunju_4595@qq.com
*/
public class HttpUtil {

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
     * @param getDataListener 回调方法
     */
    public static void getDataReFreshToken(final Context context, final String JWT_TOKEN, final String ReFresh_Token, final String url, Header[] headers, final GetSiterDataWithTokenListener getDataListener) {
        siter_http(HTTP_GET, context, url, headers, JWT_TOKEN, ReFresh_Token, null, null, getDataListener);
    }

    /**
     * 带token的网络post  runInUI
     * 支持刷新token
     *
     * @param context             context
     * @param JWT_TOKEN           指令格式中的参数token，是APP调用 认证授权API : 登录 接口返回的JWT Token，有效期为12小时。如果APP和云端建立通道前token已过期，云端会提示token无效；如果联网之后连接不断，即使token过期，APP还能继续控制设备。
     * @param url                 构造网址
     * @param entity              String JSON形式的string
     * @param getDataListener 回调
     */
    public static void postDataReFreshToken(final Context context, final String JWT_TOKEN, final String ReFresh_Token, final String url, Header[] headers, final String entity, final GetSiterDataWithTokenListener getDataListener) {
        siter_http(HTTP_POST, context, url, headers, JWT_TOKEN, ReFresh_Token, entity, null, getDataListener);
    }


    /**
     * 带token的DELETE
     * 支持刷新token
     *
     * @param context             context
     * @param JWT_TOKEN           指令格式中的参数token，是APP调用 认证授权API : 登录 接口返回的JWT Token，有效期为12小时。如果APP和云端建立通道前token已过期，云端会提示token无效；如果联网之后连接不断，即使token过期，APP还能继续控制设备。
     * @param url                 构造网址
     * @param getDataListener 回调方法
     */
    public static void deleteDataReFreshToken(final Context context, final String JWT_TOKEN, final String ReFresh_Token, final String url, Header[] headers, final GetSiterDataWithTokenListener getDataListener) {
        siter_http(HTTP_DELETE, context, url, headers, JWT_TOKEN, ReFresh_Token, null, null, getDataListener);
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
     * @param getDataListener 回调方法
     */
    public static void patchDataToken(final Context context, final String JWT_TOKEN, final String ReFresh_Token, final String url, Header[] headers, final String entity, final GetSiterDataWithTokenListener getDataListener) {
        siter_http(HTTP_PATCH, context, url, headers, JWT_TOKEN, ReFresh_Token, entity, null, getDataListener);
    }


    /**
     * 带token的PUT
     *
     * @param context             context
     * @param JWT_TOKEN           指令格式中的参数token，是APP调用 认证授权API : 登录 接口返回的JWT Token，有效期为12小时。如果APP和云端建立通道前token已过期，云端会提示token无效；如果联网之后连接不断，即使token过期，APP还能继续控制设备。
     * @param url                 构造网址
     * @param entity              请求体
     * @param getDataListener 回调方法
     */
    public static void putDataRefreshToken(final Context context, String JWT_TOKEN, final String ReFresh_Token, final String url, Header[] headers, final String entity, final GetSiterDataWithTokenListener getDataListener) {
        siter_http(HTTP_PUT, context, url, headers, JWT_TOKEN, ReFresh_Token, entity, null, getDataListener);
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
     * @param getDataListener 回调
     */
    public static void postFileReFreshToken(final Context context, final String JWT_TOKEN, final String ReFresh_Token, final String url, final RequestParams params, final GetSiterDataWithTokenListener getDataListener) {
        siter_http(HTTP_POST_FILE, context, url, JWT_TOKEN, ReFresh_Token, null, params, getDataListener);
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
    private synchronized static void refreshToken(final Context context, String ReFresh_Token, final HttpUtil.RefreshTokenListener refreshTokenListener) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("refresh_token", ReFresh_Token);
        String refresh_url = TextUtils.concat(Constants.UrlUtil.BASE_UAA_URL, Constants.UrlUtil.UAA_REFRESH_TOKEN).toString();
        BaseHttpUtil.postData(context, refresh_url, jsonObject.toJSONString(), new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                Log.d(Constants.SITER_SDK, "token刷新成功:" + new String(bytes));
                refreshTokenListener.refreshSuccess(JSONObject.parseObject(new String(bytes), JWTBean.class));
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (bytes != null && bytes.length > 0) {
                        refreshTokenListener.refreshFail(i, headers, bytes);
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
        private GetSiterDataWithTokenListener GetDataListener;
        private String entity = "";

        public AsyncHttpHandlerWithTokenTimeOut(Context context, GetSiterDataWithTokenListener GetDataListener, String reFresh_Token, String entity, ReloadListener reloadListener) {
            this.context = context;
            this.GetDataListener = GetDataListener;
            this.reloadListener = reloadListener;
            this.refresh_Token = reFresh_Token;
            this.entity = entity;
        }

        public AsyncHttpHandlerWithTokenTimeOut(GetSiterDataWithTokenListener GetDataListener) {
            this.GetDataListener = GetDataListener;
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            GetDataListener.getDataSuccess(byte2Str(responseBody));
        }

        @Override
        public void onProgress(long bytesWritten, long totalSize) {
            super.onProgress(bytesWritten, totalSize);
            GetDataListener.getDataProgress(bytesWritten, totalSize);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            if (TextUtils.isEmpty(refresh_Token)) {
                GetDataListener.getDataFail(CodeUtil.getErrorCode(statusCode, responseBody));
            } else {
                if (CodeUtil.getTimeOutFlag(statusCode, responseBody)) {
                    refreshToken(context, refresh_Token, new RefreshTokenListener() {
                        @Override
                        public void refreshSuccess(JWTBean jwtBean) {
                            GetDataListener.getToken(jwtBean);
                            reloadListener.reload(jwtBean);
                        }

                        @Override
                        public void refreshFail(int i, Header[] headers, byte[] bytes) {
                            GetDataListener.getDataFail(Constants.ErrorCode.TOKEN_TIME_OUT);
                        }
                    });
                } else {
                    GetDataListener.getDataFail(CodeUtil.getErrorCode(getRequestURI().toString(), statusCode, responseBody));
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

    private static void siter_http(int http_type, final Context context, final String url, final String JWT_TOKEN, final String ReFresh_Token, final String entity, final RequestParams params, final GetSiterDataWithTokenListener getDataListener) {
        siter_http(http_type, context, url, null, JWT_TOKEN, ReFresh_Token, entity, params, getDataListener);
    }


    /**
     * http请求
     */
    private static void siter_http(int http_type, final Context context, final String url, final Header[] headers, final String JWT_TOKEN, final String ReFresh_Token, final String entity, final RequestParams params, final GetSiterDataWithTokenListener getDataListener) {
        if (TextUtils.isEmpty(JWT_TOKEN) || TextUtils.isEmpty(ReFresh_Token) || TextUtils.isEmpty(url)) {
            getDataListener.getDataFail(Constants.ErrorCode.TOKEN_TIME_OUT);
            Log.e(Constants.SDK_ERROR, "Token or url is null\n" + "token:" + JWT_TOKEN + "url\n" + url);
        } else {
            if (Network.isConnected(context)) {
                switch (http_type) {
                    case HTTP_GET:
                        BaseHttpUtil.getDataToken(context, JWT_TOKEN, url, headers, new AsyncHttpHandlerWithTokenTimeOut(context, getDataListener, ReFresh_Token, "", new ReloadListener() {
                            @Override
                            public void reload(JWTBean jwtBean) {
                                BaseHttpUtil.getDataToken(context, jwtBean.getAccessToken(), url, headers, new AsyncHttpHandlerWithTokenTimeOut(getDataListener));
                            }
                        }));
                        break;
                    case HTTP_POST:
                        BaseHttpUtil.postDataToken(context, JWT_TOKEN, url, headers, entity, new AsyncHttpHandlerWithTokenTimeOut(context, getDataListener, ReFresh_Token, entity, new ReloadListener() {
                            @Override
                            public void reload(JWTBean jwtBean) {
                                BaseHttpUtil.postDataToken(context, jwtBean.getAccessToken(), url, headers, entity, new AsyncHttpHandlerWithTokenTimeOut(getDataListener));
                            }
                        }));
                        break;
                    case HTTP_PUT:
                        BaseHttpUtil.putDataToken(context, JWT_TOKEN, url, headers, entity, new AsyncHttpHandlerWithTokenTimeOut(context, getDataListener, ReFresh_Token, entity, new ReloadListener() {
                            @Override
                            public void reload(JWTBean jwtBean) {
                                BaseHttpUtil.putDataToken(context, jwtBean.getAccessToken(), url, headers, entity, new AsyncHttpHandlerWithTokenTimeOut(getDataListener));
                            }
                        }));
                        break;
                    case HTTP_DELETE:
                        BaseHttpUtil.deleteDataToken(context, JWT_TOKEN, url, headers, new AsyncHttpHandlerWithTokenTimeOut(context, getDataListener, ReFresh_Token, "", new ReloadListener() {
                            @Override
                            public void reload(JWTBean jwtBean) {
                                BaseHttpUtil.deleteDataToken(context, jwtBean.getAccessToken(), url, headers, new AsyncHttpHandlerWithTokenTimeOut(getDataListener));
                            }
                        }));
                        break;
                    case HTTP_PATCH:
                        BaseHttpUtil.patchDataToken(context, JWT_TOKEN, url, headers, entity, new AsyncHttpHandlerWithTokenTimeOut(context, getDataListener, ReFresh_Token, entity, new ReloadListener() {
                            @Override
                            public void reload(JWTBean jwtBean) {
                                BaseHttpUtil.patchDataToken(context, jwtBean.getAccessToken(), url, headers, entity, new AsyncHttpHandlerWithTokenTimeOut(getDataListener));
                            }
                        }));
                        break;
                    case HTTP_POST_FILE:
                        BaseHttpUtil.postFileToken(context, JWT_TOKEN, url, params, new AsyncHttpHandlerWithTokenTimeOut(context, getDataListener, ReFresh_Token, "", new ReloadListener() {
                            @Override
                            public void reload(JWTBean jwtBean) {
                                BaseHttpUtil.postFileToken(context, jwtBean.getAccessToken(), url, params, new AsyncHttpHandlerWithTokenTimeOut(getDataListener));
                            }
                        }));
                        break;
                    default:
                        getDataListener.getDataFail(Constants.ErrorCode.UNKNOWN_ERROR);
                        break;
                }
            } else {
                Log.e(Constants.SDK_ERROR, url + "\n" + "Network is not available");
                getDataListener.getDataFail(Constants.ErrorCode.NETWORK_TIME_OUT);
            }
        }
    }
}
