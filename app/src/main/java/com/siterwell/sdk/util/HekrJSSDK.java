package com.siterwell.sdk.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.siterwell.sdk.action.HekrUser;
import com.siterwell.sdk.action.HekrUserAction;
import com.siterwell.sdk.bean.DeviceBean;
import com.siterwell.sdk.bean.GateWaySubDeviceBean;
import com.siterwell.sdk.bean.Global;
import com.siterwell.sdk.event.ClearFilterEvent;
import com.siterwell.sdk.event.DownLoadEvent;
import com.siterwell.sdk.listener.DataReceiverListener;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Administrator on 2017/10/16.
 */

public class HekrJSSDK {
    private static final String TAG = "HekrJSSDK";
    private static final int SCAN_AUTH_REQUEST_CODE = 10001;

    private AppCompatActivity activity;

    private static volatile HekrJSSDK hekrJSSDK = null;

    private WebViewJavascriptBridge bridge;

    private boolean initHEKRUserFlag = true;

    //primary_layout
    private RelativeLayout layout;

    //primary_WebView
    private WebView initView;

    //current_WebView
    private WebView currentView;

    //all_WebView
    private List<WebView> windows = new CopyOnWriteArrayList<>();

    //hekr openApi 工具类
    private HekrUserAction hekrUserAction;
    //设备信息对象
    private DeviceBean deviceBean;
    //初始url
    private String url;
    //控制页面zip下载标识
    private int downloadTag = 0;
    private String h5Zip = null;
    //推送消息
    private String pushJsonMessage;
    //是否打开控制页面zip下载功能
    private boolean isOpenWebCache;

    //手机震动
    private Vibrator mVibrator;
    private WebViewJavascriptBridge.WVJBResponseCallback scanCallback;

    private WebViewJavascriptBridge.WVJBResponseCallback fingerPrintCallback;

    private String nowPageDevTid;

    private int backStepCount = 0;

    private static String nowUrl;

    private static String funcId;

    //页面状态回调 外部接收页面加载状态
    private WebViewPageStatusListener webViewPageStatusListener;

    //页面状态回调接口
    public static abstract class WebViewPageStatusListener {

        /**
         * 可以在此添加转圈圈
         *
         * @param url 加载页面初始URL
         */
        public abstract void onPageStarted(String url);

        /**
         * 可在此取消转圈圈
         *
         * @param url 页面加载完毕URL
         */
        public abstract void onPageFinished(String url);

        /**
         * 控制页面关闭
         */
        public abstract void onAllPageClose();


        /**
         * 二维码扫描
         *
         * @param requestCode 请求码
         */
        public void openScan(int requestCode) {}

        /**
         * 指纹识别
         *
         * @param requestCode 请求码
         */
        public void openFingerPrint(int requestCode) {}

    }

    private HekrJSSDK(AppCompatActivity activity, RelativeLayout layout, WebView initView, DeviceBean deviceBean, String pushJsonMessage, boolean isOpenWebCache) {
        this.activity = activity;
        this.layout = layout;
        this.deviceBean = deviceBean;
        this.h5Zip = deviceBean.getAndroidPageZipURL();
        this.url = deviceBean.getAndroidH5Page();
        this.pushJsonMessage = pushJsonMessage;
        this.initView = initView;
        this.isOpenWebCache = isOpenWebCache;

        SpCache.init(activity);
        hekrUserAction = HekrUserAction.getInstance(activity);
    }

    /**
     * @param activity        web控制Activity
     * @param layout          web控制Activity根布局
     * @param initView        web控制Activity初始webView可以xml也可以new出来的
     * @param deviceBean      hekr云端获取的设备信息对象
     * @param pushJsonMessage 推送消息 默认为空
     * @param isOpenWebCache  是否采用webView页面缓存机制，首次打开该页面将自动下载控制页面zip包至手机SD卡
     * @return native 与 web 桥接工具类
     */
    public static synchronized HekrJSSDK getInstance(AppCompatActivity activity, RelativeLayout layout, WebView initView, DeviceBean deviceBean, String pushJsonMessage, boolean isOpenWebCache) {
        if (deviceBean==null) {
            throw new NullPointerException("h5page、ctrlKey、ppk not allow null");
        }

        //独立设备或网关设备
        if(deviceBean.getGateWaySubDeviceBean()==null){
            if(TextUtils.isEmpty(deviceBean.getAndroidH5Page()) || TextUtils.isEmpty(deviceBean.getCtrlKey()) || TextUtils.isEmpty(deviceBean.getProductPublicKey())){
                throw new NullPointerException("h5page、ctrlKey、ppk not allow null");
            }
        }else{
            //网关下的子设备
            if(TextUtils.isEmpty(deviceBean.getGateWaySubDeviceBean().getAndroidH5Page())
                    || TextUtils.isEmpty(deviceBean.getGateWaySubDeviceBean().getParentCtrlKey())
                    || TextUtils.isEmpty(deviceBean.getGateWaySubDeviceBean().getProductPublicKey())){
                throw new NullPointerException("h5page、ctrlKey、ppk not allow null");
            }
        }

        /*if (hekrJSSDK == null || hekrJSSDK.initView == null || hekrJSSDK.layout == null || hekrJSSDK.activity == null ||
                (!deviceBean.equals(hekrJSSDK.getDeviceBean()))) {
            synchronized (HekrJSSDK.class) {
                if (hekrJSSDK == null || hekrJSSDK.initView == null || hekrJSSDK.layout == null || hekrJSSDK.activity == null ||
                        (!deviceBean.equals(hekrJSSDK.getDeviceBean()))) {
                    Log.i(TAG, "创建HekrJSSDK对象");*/
        hekrJSSDK = new HekrJSSDK(activity, layout, initView, deviceBean, pushJsonMessage, isOpenWebCache);
                /*}
            }
        }*/
        return hekrJSSDK;
    }

    public DeviceBean getDeviceBean() {
        return deviceBean;
    }

    public void setWebViewPageStatusListener(WebViewPageStatusListener webViewPageStatusListener) {
        this.webViewPageStatusListener = webViewPageStatusListener;
    }

    public void initUrl() {
        String lang = getLanguage(activity);
        String IMEI = HekrCommonUtil.getHEKRIMEI(activity);
        //网关设备
        String str;
        if (deviceBean.getGateWaySubDeviceBean() != null) {
            GateWaySubDeviceBean gateWaySubDeviceBean = deviceBean.getGateWaySubDeviceBean();
            str = TextUtils.concat(gateWaySubDeviceBean.getAndroidH5Page(),
                    "?devTid=", gateWaySubDeviceBean.getParentDevTid(),
                    "&ctrlKey=", gateWaySubDeviceBean.getParentCtrlKey(),
                    "&ppk=", gateWaySubDeviceBean.getProductPublicKey(),
                    "&lang=", lang,
                    "&subDevTid=", gateWaySubDeviceBean.getDevTid(), "&appId=", IMEI,"web").toString();
        } else {
            str = TextUtils.concat(deviceBean.getAndroidH5Page(),
                    "?devTid=", deviceBean.getDevTid(),
                    "&ctrlKey=", deviceBean.getCtrlKey(),
                    "&ppk=", deviceBean.getProductPublicKey(),
                    "&lang=", lang, "&appId=", IMEI,"web").toString();
        }
        if (!TextUtils.isEmpty(pushJsonMessage)) {
            str = TextUtils.concat(str, "&notifydata=", pushJsonMessage).toString();
        }
        nowUrl = str;

        bridge = getBridge(initView);
        initView.loadUrl(str);
        currentView = initView;
        windows.add(initView);
    }

    private static String getLanguage(Context context) {
        /*String lang = TextUtils.concat(Locale.getDefault().getLanguage(), "-", Locale.getDefault().getCountry()).toString();
        if (TextUtils.isEmpty(lang)) {
            lang = "zh-CN";
        }
        return lang;*/
        return HekrCodeUtil.getLanguageTag(context);
    }

    public void loadNewUrl(String url, String devTid) {
        if (currentView != null) {
            if (TextUtils.isEmpty(nowPageDevTid)) {
                backStepCount = 0;
                currentView.loadUrl(url);
                nowUrl = url;
            } else {
                if (TextUtils.equals(devTid, nowPageDevTid)) {
                    Log.i(TAG, "webView--reload");
                    currentView.loadUrl(url);
                    backStepCount--;
                } else {
                    Log.i(TAG, "webView--loadUrl");
                    nowUrl = url;
                    currentView.loadUrl(url);
                    backStepCount = 0;
                }
            }
        }
    }

    public void openNewPushUrl(String url, String devTid) {
        if (TextUtils.isEmpty(nowPageDevTid)) {
            openNewWindow(url, false);
        } else {
            if (TextUtils.equals(devTid, nowPageDevTid)) {
                currentView.loadUrl(url);
            } else {
                openNewWindow(url, false);
            }
        }
    }

    //open_new_WebView(with_push_flag)
    private void openNewWindow(String url, boolean pushInLastFlag) {

        /*if(!TextUtils.isEmpty(url)&&currentView!=null&&!TextUtils.isEmpty(currentView.getUrl())) {
            Log.i(TAG,"currentView.getOriginalUrl():"+currentView.getOriginalUrl());
            Log.i(TAG,"currentView.getUrl():"+currentView.getUrl());
            Log.i(TAG,"url:"+url);
            if (!url.equals(currentView.getUrl())) {*/
        //Log.i("HekrJSSDK", "openNewWindow:URL:" + url.substring(0, url.length() - 13));
        //Log.i(TAG,"新建webView");
        WebView window = new WebView(activity);
        bridge = getBridge(window);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        if (pushInLastFlag) {
            if (!TextUtils.isEmpty(url) && url.length() > 14) {
                window.loadUrl(url.substring(0, url.length() - 14));
            }
        } else {
            window.loadUrl(url);
        }

        if (layout != null) {
            layout.addView(window, lp);
        } else {
            Log.i(TAG, "layout is null");
        }
        if (currentView != null) {
            currentView.setVisibility(View.INVISIBLE);
        }
        currentView = window;
        windows.add(window);
            /*}
            else {
                Log.i(TAG,"重新加载");
                currentView.reload();
            }
        }*/
    }

    //close-current-WebView
    private void closeWindow() {
        if (windows != null && !windows.isEmpty() && currentView != null) {
            //多个webView
            if (windows.size() > 1) {
                if (currentView.canGoBack()) {
                    Log.i(TAG, "多个WebView并且当前WebView中存在多个url页面！");
                    if (backStepCount != 0) {
                        currentView.goBackOrForward(backStepCount);
                        if (!nowUrl.equals(currentView.getUrl())) {
                            currentView.goBackOrForward(backStepCount);
                            currentView.clearHistory();
                        }
                        backStepCount = 0;
                    } else {
                        currentView.goBack();
                    }
                } else {
                    Log.i(TAG, "多个WebView并且当前WebView中只剩下一个页面！");
                    windows.remove(currentView);
                    currentView.setVisibility(View.GONE);
                    /*WebView view = windows.get(windows.size() - 1);
                    view.setVisibility(View.VISIBLE);
                    view.reload();
                    currentView = view;*/

                    windows.get(windows.size() - 1).setVisibility(View.VISIBLE);
                    windows.get(windows.size() - 1).reload();
                    currentView = windows.get(windows.size() - 1);
                }
            }//一个webView
            else {
                if (currentView.canGoBack()) {
                    Log.i(TAG, "一个WebView并且当前WebView中存在多个url页面！");
                    if (backStepCount != 0) {
                        currentView.goBackOrForward(backStepCount);
                        if (!nowUrl.equals(currentView.getUrl())) {
                            currentView.goBackOrForward(backStepCount);
                            currentView.clearHistory();
                        }
                        backStepCount = 0;
                    } else {
                        currentView.goBack();
                    }
                } else {
                    Log.i(TAG, "一个WebView并且当前WebView中只剩下一个页面！");
                    backStepCount = 0;
                    webViewPageStatusListener.onAllPageClose();
                }
            }
        }
    }

    private void removeAllWebView() {
        /*Iterator<HekrWebView> it = windows.iterator();
        while (it.hasNext()) {
            HekrWebView webView = it.next();
            EventBus.getDefault().post(new ClearFilterEvent(true, webView));
            it.remove();
            if (webView != null) {
                webView.setVisibility(View.GONE);
                webView.destroy();
            }
        }
        currentView = loginView;
        windows.add(currentView);
        currentView.setVisibility(View.VISIBLE);*/
        webViewPageStatusListener.onAllPageClose();
    }

    //js桥接
    private WebViewJavascriptBridge getBridge(final WebView webView) {

        bridge = new WebViewJavascriptBridge(activity, webView, new UserServerHandler());
        bridge.setCustomWebViewClient(new myWebClient());
        bridge.setCustomWebChromeClient(new MyWebChromeClient());

        bridge.registerHandler("currentUser", new WebViewJavascriptBridge.WVJBHandler() {
            @Override
            public void handle(Object data, final WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {
                if (null != jsCallback && hekrUserAction != null) {

                    JSONObject jsonObject = new JSONObject();
                    JSONObject user = new JSONObject();
                    try {
                        user.put("uid", hekrUserAction.getUserId());
                        user.put("access_token", hekrUserAction.getJWT_TOKEN());
                        jsonObject.put("obj", user);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    jsCallback.callback(jsonObject);

                }
            }
        });

        bridge.registerHandler("getDevices", new WebViewJavascriptBridge.WVJBHandler() {
            @Override
            public void handle(Object data, final WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {
                if (null != jsCallback && hekrUserAction != null) {
                    hekrUserAction.getDevices(new HekrUser.GetDevicesListener() {
                        @Override
                        public void getDevicesSuccess(List<DeviceBean> hekrLists) {
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("obj", hekrLists);
                                //Log.i(TAG, "getDevices:" + jsonObject.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            jsCallback.callback(jsonObject);
                        }

                        @Override
                        public void getDevicesFail(int errorCode) {
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("obj", null);
                                //Log.i(TAG, "getDevices:" + jsonObject.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            jsCallback.callback(jsonObject);
                        }
                    });
                }
            }
        });
        bridge.registerHandler("login", new WebViewJavascriptBridge.WVJBHandler() {
            @Override
            public void handle(Object data, final WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {

                if (null != jsCallback && data != null && !TextUtils.isEmpty(data.toString())) {
                    try {
                        JSONObject obj = new JSONObject(data.toString());
                        if (obj.has("userName") && obj.has("password") && hekrUserAction != null) {
                            hekrUserAction.login(obj.getString("userName"), obj.getString("password"), new HekrUser.LoginListener() {
                                @Override
                                public void loginSuccess(String str) {
                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put("obj", str);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    jsCallback.callback(jsonObject);
                                }

                                @Override
                                public void loginFail(int errorMsg) {
                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put("error", errorMsg);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    jsCallback.callback(jsonObject);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        bridge.registerHandler("log", new WebViewJavascriptBridge.WVJBHandler() {
            @Override
            public void handle(Object data, final WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {

                if (null != jsCallback && data != null && !TextUtils.isEmpty(data.toString())) {

                    ViewWindow.showView(TextUtils.concat("前端log:", data.toString()).toString());
                }
            }
        });
        bridge.registerHandler("logout", new WebViewJavascriptBridge.WVJBHandler() {
            @Override
            public void handle(Object data, WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {
                hekrUserAction.userLogout();
                jsCallback.callback("");
            }
        });

        bridge.registerHandler("close", new WebViewJavascriptBridge.WVJBHandler() {
            @Override
            public void handle(Object data, WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {
                if (null != jsCallback) {
                    //if (data != null && !TextUtils.isEmpty(data.toString())) {
                    //Log.i(TAG, "close:param:" + data.toString());
                    //}
                    Log.i(TAG, "调用close关闭WebView！");
                    //closeWindow();
                    if(activity!=null) {
                        activity.finish();
                    }
                    //jsCallback.callback("");
                }
            }
        });

        bridge.registerHandler("closeAll", new WebViewJavascriptBridge.WVJBHandler() {
            @Override
            public void handle(Object data, WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {
                if (null != jsCallback) {
                    if (data != null && !TextUtils.isEmpty(data.toString())) {
                        Log.i(TAG, "closeAll:param:" + data.toString());
                    }
                    removeAllWebView();
                    Log.i(TAG, "调用closeAll关闭all_WebView！");
                    jsCallback.callback("");
                }
            }
        });
        bridge.registerHandler("playSound", new WebViewJavascriptBridge.WVJBHandler() {
            @Override
            public void handle(Object data, WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {
                if (null != jsCallback) {
                    if (data != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(data.toString());
                            if (jsonObject.has("type") && activity != null) {
                                if (TextUtils.equals("Vibrate", jsonObject.getString("type"))) {
                                    if (mVibrator == null) {
                                        mVibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
                                    }
                                    mVibrator.vibrate(1000);
                                } else {
                                    String mp3 = jsonObject.getString("type");
                                    MediaPlayer mediaPlayer = new MediaPlayer();
                                    mediaPlayer.setDataSource(activity, Uri.parse(mp3));
                                    mediaPlayer.prepare();
                                    mediaPlayer.start();
                                }

                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                    jsCallback.callback("");
                }
            }
        });


        bridge.registerHandler("qrScan", new WebViewJavascriptBridge.WVJBHandler() {
            @Override
            public void handle(Object data, WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {
                if (activity != null) {
                    webViewPageStatusListener.openScan(SCAN_AUTH_REQUEST_CODE);
                    scanCallback = jsCallback;
                }
            }
        });

        bridge.registerHandler("touchIDAuth", new WebViewJavascriptBridge.WVJBHandler() {
            @Override
            public void handle(Object data, WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {
                fingerPrintCallback = jsCallback;
            }
        });

        bridge.registerHandler("send", new WebViewJavascriptBridge.WVJBHandler() {
            @Override
            public void handle(Object data, final WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {

                if (data != null && bridge != null) {
                    Log.i(TAG, "前端页面send>>>data:" + data.toString());
                    try {
                        JSONObject obj = new JSONObject(data.toString());

                        if (obj.has("tid") && obj.has("command")) {
                            MsgUtil.sendMsg(currentView, obj.getString("tid"), new JSONObject(obj.getString("command")), new DataReceiverListener() {
                                @Override
                                public void onReceiveSuccess(String msg) {
                                    Log.i(TAG, "前端页面接收到返回数据: " + msg);
                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put("obj", new JSONObject(msg));
                                        if (jsCallback != null) {
                                            jsCallback.callback(jsonObject);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onReceiveTimeout() {
                                    if (jsCallback != null) {
                                        jsCallback.callback(null);
                                    }
                                }
                            }, Global.isUDPOpen);
                        } else {
                            Log.i(TAG, "前端页面提供的数据不包含tid或者command");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.i(TAG, "send:data:空||bridge is null");
                }

            }
        });

        bridge.registerHandler("recv", new WebViewJavascriptBridge.WVJBHandler() {
                    @Override
                    public void handle(Object data, final WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {
                        if (data != null) {
                            Log.i(TAG, "recv:data:" + data.toString());
                            if (!TextUtils.isEmpty(data.toString())) {
                                final com.alibaba.fastjson.JSONObject object = com.alibaba.fastjson.JSONObject.parseObject(data.toString());
                                //final JSONObject object1=(JSONObject) data;
                                //object.getJSONObject("filter");
                                //object.getString("funcId");
                                try {
                                    //final JSONObject object=new JSONObject(data.toString());
                                    funcId = new JSONObject(data.toString()).getString("funcId");
                                    MsgUtil.receiveMsg(currentView, new JSONObject(object.toJSONString()).getJSONObject("filter"), new DataReceiverListener() {
                                        @Override
                                        public void onReceiveSuccess(String msg) {
                                            JSONObject c = new JSONObject();

                                            try {
                                                //funcId=new JSONObject(data.toString()).getString("funcId");
                                                Log.i(TAG, "设备主动上报转给前端页面:" + msg + " ID:" + funcId);
                                                c.put("funcId", funcId);
                                                c.put("obj", new JSONObject(msg));

                                                if (jsCallback != null) {
                                                    jsCallback.callback(c);
                                                }

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            bridge.callHandler("onRecv", c, new WebViewJavascriptBridge.WVJBResponseCallback() {
                                                @Override
                                                public void callback(Object data) {
                                                    /*if (jsCallback != null) {
                                                        jsCallback.callback(data);
                                                    }*/
                                                }
                                            });
                                        }

                                        @Override
                                        public void onReceiveTimeout() {

                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                      /*  if (jsCallback != null) {
                            jsCallback.callback(null);
                        }*/
                    }
                }
        );

        bridge.registerHandler("notify", new WebViewJavascriptBridge.WVJBHandler() {
            @Override
            public void handle(Object data, WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {
                if (null != jsCallback && windows != null && !windows.isEmpty()) {
                    for (int i = 0; i < windows.size(); i++) {
                        bridge = getBridge(windows.get(i));
                        bridge.callHandler("onNotify", data, new WebViewJavascriptBridge.WVJBResponseCallback() {
                            @Override
                            public void callback(Object data) {
                                Log.i(TAG, "notify:data:" + data.toString());
                            }
                        });
                    }
                }
            }
        });

        bridge.registerHandler("share", new WebViewJavascriptBridge.WVJBHandler() {
            @Override
            public void handle(Object data, WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {
                /*if (data != null && !TextUtils.isEmpty(data.toString())) {
                    try {
                        JSONObject jsonObject = new JSONObject(data.toString());
                        if (jsonObject.has("shareStatus")) {
                            JSONObject object = jsonObject.getJSONObject("shareStatus");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }*/
            }
        });

        return bridge;
    }

    private class myWebClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.i(TAG, "onPageStarted:URL:" + url);
            debugView("onPageStarted:URL:" + url);
            nowPageDevTid = endPagePushFlag(url);
            //Log.i(TAG, "nowTid：" + nowPageDevTid);
            webViewPageStatusListener.onPageStarted(url);
            //webViewPageStatusListener.openFingerPrint(SCAN_AUTH_REQUEST_CODE);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.i(TAG, "shouldOverrideUrlLoading:url:" + url);
            /*if (url.endsWith("openType=push") || openTypeWithPush(url)) {
                boolean pushInLast = false;
                if (url.endsWith("openType=push")) {
                    pushInLast = true;
                }
                openNewWindow(url, pushInLast);
                return true;
            } else {
                view.loadUrl(url);
                return true;
            }*/
            view.loadUrl(url);
            return true;
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView webView, String url) {
            //Log.i(TAG,"shouldInterceptRequest:"+s);
            if (isOpenWebCache) {
                if (getResource(url) == null) {
                    return super.shouldInterceptRequest(webView, url);
                }
                return getResource(url);
            } else {
                return super.shouldInterceptRequest(webView, url);
            }
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView webView, WebResourceRequest webResourceRequest) {
            //Log.i(TAG,"shouldInterceptRequest:"+webResourceRequest.getUrl().toString());
            if (isOpenWebCache) {
                if (getResource(webResourceRequest.getUrl().toString()) == null) {
                    return super.shouldInterceptRequest(webView, webResourceRequest);
                }
                return getResource(webResourceRequest.getUrl().toString());
            } else {
                return super.shouldInterceptRequest(webView, webResourceRequest);
            }
        }

        private WebResourceResponse getResource(String str) {
            //Log.i(TAG, "shouldInterceptRequest:" + str);
            if (!TextUtils.isEmpty(url) &&
                    !TextUtils.isEmpty(h5Zip) &&
                    !TextUtils.isEmpty(HekrCodeUtil.url2Folder(str)) &&
                    !TextUtils.isEmpty(HekrCodeUtil.url2Folder(url))) {
                WebResourceResponse response;
                try {
                    String fileName;
                    String type;
                    String filePath;
                    String str1 = null;
                    try {
                        str1 = URLDecoder.decode(str, "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    String path = new URL(str1).getPath();

                    //Log.i(TAG, "path:" + new URL(str).getPath());
                    if (path.endsWith(".js")) {
                        type = "application/x-javascript";
                    } else if (path.endsWith(".html")) {
                        type = "text/html";
                    } else if (path.endsWith(".css")) {
                        type = "text/css";
                    } else if (path.endsWith(".jpeg")) {
                        type = "image/jpeg";
                    } else if (path.endsWith(".png")) {
                        type = "image/png";
                    } else if (path.endsWith(".ttf")) {
                        type = "application/x-font-ttf";
                    } else if (path.endsWith(".svg")) {
                        type = "image/svg+xml";
                    } else if (path.endsWith(".jpg")) {
                        type = "image/jpeg";
                    } else {
                        type = "application/octet-stream";
                    }
                    try {
                        //index.html
                        if ((!path.endsWith(".ico") || path.endsWith(".html") || path.endsWith(".css") || path.endsWith(".jpeg") ||
                                path.endsWith(".png") || path.endsWith(".ttf") || path.endsWith(".svg") || path.endsWith(".jpg")) &&
                                path.contains(HekrCodeUtil.zip2Folder(h5Zip)) && activity != null) {

                            fileName = path.substring(path.lastIndexOf("/"), path.length());
                            //HekrCodeUtil.url2Folder(url)
                            filePath = path.substring(path.indexOf(HekrCodeUtil.zip2Folder(h5Zip)), path.lastIndexOf("/"));

                            File file = new File(ExternalStorage.getSDCacheDir(activity, filePath), fileName);
                            //Log.d(TAG, "路径: " + filePath);
                            FileInputStream fis = new FileInputStream(file);
                            response = new WebResourceResponse(type, "utf-8", fis);
                            return response;
                        }
                    } catch (FileNotFoundException e) {
                        //e.printStackTrace();
                        //通知下载h5zip
                        if (downloadTag == 0) {
                            Log.d(TAG, "shouldInterceptRequest: 开始下载h5Zip！！！！！！");
                            DeviceBean deviceBean = new DeviceBean();
                            deviceBean.setAndroidPageZipURL(h5Zip);
                            EventBus.getDefault().post(new DownLoadEvent(deviceBean));
                        }
                        downloadTag = 1;
                        return null;
                    }
                } catch (MalformedURLException e) {
                    //e.printStackTrace();
                    return null;
                }
            }
            return null;
        }

        private boolean openTypeWithPush(String str) {
            Map<String, String> data = new HashMap<>();
            if (!TextUtils.isEmpty(str)) {
                int index = str.indexOf("?");
                if (index != -1) {
                    String reallyData = str.substring(index + 1);
                    //Log.i(TAG,"reallyData:"+reallyData);
                    if (reallyData.length() > 1) {
                        String[] param = reallyData.split("&");

                        for (String item : param) {
                            String[] key_value = item.split("=");
                            if (key_value.length == 2) {
                                data.put(key_value[0].trim(), key_value[1].trim());
                            }
                        }
                        if (data.containsKey("openType") && TextUtils.equals("push", data.get("openType"))) {
                            return true;
                        }
                    }
                    return false;
                }
                return false;
            }
            return false;
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        @Override
        public void onPageFinished(final WebView view, String url) {
            Log.i(TAG, "onPageFinished:URL:" + url);
            debugView("onPageFinished:URL:" + url);
            if (!view.getSettings().getLoadsImagesAutomatically()) {
                view.getSettings().setLoadsImagesAutomatically(true);
            }

            if (hekrUserAction != null && initHEKRUserFlag) {
                initHEKRUserFlag = false;
                initHEKRUser();
            }

            webViewPageStatusListener.onPageFinished(url);

            if (windows != null && !windows.isEmpty()) {
                Log.i(TAG, "当前WebView个数：" + windows.size());
            }
            super.onPageFinished(view, url);
        }
    }

    private class MyWebChromeClient extends WebChromeClient {
        MyWebChromeClient() {
            super();
        }

        @Override
        public void onCloseWindow(WebView window) {
            super.onCloseWindow(window);
        }

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
        }
    }

    private String endPagePushFlag(String str) {
        Map<String, String> data = new HashMap<>();
        if (!TextUtils.isEmpty(str)) {
            int index = str.indexOf("?");
            if (index != -1) {
                String reallyData = str.substring(index + 1);
                if (reallyData.length() > 1 && reallyData.contains("&") && reallyData.contains("=")) {
                    String[] param = reallyData.split("&");

                    for (String item : param) {
                        String[] key_value = item.split("=");
                        if (key_value.length == 2) {
                            data.put(key_value[0].trim(), key_value[1].trim());
                        }
                    }
                    if (data.containsKey("devTid")) {
                        return data.get("devTid");
                    }
                }
                return "";
            }
            return "";
        }
        return "";
    }

    private void initHEKRUser() {

        JSONObject jsonObject = new JSONObject();
        JSONObject uidObject = new JSONObject();
        try {
            uidObject.put("uid", hekrUserAction.getUserId());
            uidObject.put("access_token", hekrUserAction.getJWT_TOKEN());
            jsonObject.put("obj", uidObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        userChange(jsonObject);
    }

    private class UserServerHandler implements WebViewJavascriptBridge.WVJBHandler {

        @Override
        public void handle(Object data, WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {
            if (null != jsCallback) {
                jsCallback.callback("Java said:Right back atcha");
            }
        }

    }

    private void userChange(JSONObject jsonObject) {
        if (windows != null && !windows.isEmpty()) {
            for (int i = 0; i < windows.size(); i++) {
                bridge = getBridge(windows.get(i));
                if (bridge != null) {
                    bridge.callHandler("onUserChange", jsonObject, new WebViewJavascriptBridge.WVJBResponseCallback() {
                        @Override
                        public void callback(Object data) {

                        }
                    });
                } else {
                    Log.i(TAG, "updateUser->onUserChange:bridge为空!");
                }
            }
        } else {
            Log.i(TAG, "updateUser->onUserChange:windows为空!");
        }
    }

    /**
     * debugView界面
     */
    private void debugView(String msg) {
        ViewWindow.showView(msg);
    }

    /**
     * 第三方功能预留 例：调用拍照
     *
     * @param requestCode 请求码
     * @param resultCode  返回码
     * @param data        返回数据
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SCAN_AUTH_REQUEST_CODE:
                if (data != null && resultCode == Activity.RESULT_OK) {
                    Bundle extras = data.getExtras();
                    String result = extras.getString("result");
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("code", result);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, "scan:" + jsonObject.toString());
                    scanCallback.callback(jsonObject);
                }
                break;
            default:
                break;
        }
    }

    public void fingerPrintResult(boolean flag) {
        if (fingerPrintCallback != null) {
            fingerPrintCallback.callback(flag);
        }
    }

    /**
     * 回退事件处理
     */
    public void onBackPressed() {
        Log.i(TAG, "--onBackPressed--");
        closeWindow();
    }

    /**
     * 清空所有相关资源
     */
    public void onDestroy() {
        Log.i(TAG, "--onDestroy--");
        EventBus.getDefault().post(new ClearFilterEvent(ClearFilterEvent.CLEARALLFILTER, currentView));
        if (windows != null && !windows.isEmpty()) {
            Log.i(TAG, "windows: is not null");
            for (int i = windows.size() - 1; i >= 0; i--) {
                Log.i(TAG, "i:" + i);
                windows.get(i).setWebViewClient(null);
                windows.get(i).setWebChromeClient(null);
                //windows.get(i).removeJavascriptInterface("_WebViewJavascriptBridge");
                windows.get(i).removeAllViews();
                windows.get(i).destroy();
                windows.remove(i);
            }
        }
        initView = null;
        layout = null;
        activity = null;
        bridge = null;
    }
}
