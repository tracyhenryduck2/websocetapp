package com.siterwell.sdk.service;

import android.app.Service;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.litesuits.common.assist.Network;
import com.litesuits.common.assist.Toastor;
import com.siterwell.sdk.action.HekrUserAction;
import com.siterwell.sdk.bean.CtrlBean;
import com.siterwell.sdk.bean.FilterBean;
import com.siterwell.sdk.bean.FindDeviceBean;
import com.siterwell.sdk.bean.Global;
import com.siterwell.sdk.bean.LanUtilBean;
import com.siterwell.sdk.bean.SubDeviceConfigBean;
import com.siterwell.sdk.event.ClearFilterEvent;
import com.siterwell.sdk.event.CommandEvent;
import com.siterwell.sdk.event.ConfigStatusEvent;
import com.siterwell.sdk.event.CreateSocketEvent;
import com.siterwell.sdk.event.DownLoadEvent;
import com.siterwell.sdk.event.MsgCallbackEvent;
import com.siterwell.sdk.event.NetworkEvent;
import com.siterwell.sdk.event.SubDeviceConfigEvent;
import com.siterwell.sdk.event.WsSwitchEvent;
import com.siterwell.sdk.listener.DataReceiverListener;
import com.siterwell.sdk.util.ConstantsUtil;
import com.siterwell.sdk.util.DownLoadH5;
import com.siterwell.sdk.util.HekrCodeUtil;
import com.siterwell.sdk.util.HekrCommonUtil;
import com.siterwell.sdk.util.HekrSDK;
import com.siterwell.sdk.util.LANUtil;
import com.siterwell.sdk.util.ProtocolFilterUtil;
import com.siterwell.sdk.util.ServiceDscDevUtil;
import com.siterwell.sdk.util.ViewWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import de.tavendo.autobahn.WebSocket;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
/**
 * Created by Administrator on 2017/10/16.
 */

public class HekrCoreService extends Service implements WebSocket.ConnectionHandler {


    private static final String TAG = "HekrCoreService";

    private final WebSocket mConnection = new WebSocketConnection();

    //每秒扫描一次过滤器队列
    private static final int TIMEOUT = 1000;

    private static final int HEART_TIME = 20 * 1000;

    private BroadcastReceiver connectionReceiver;

    private AtomicInteger MSG_COUNT = new AtomicInteger(0);
    private HekrUserAction hekrUserAction;

    private Set<FilterBean> filterQueue;

    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    //局域网
    private List<LanUtilBean> lanUtilList = new CopyOnWriteArrayList<>();
    private ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
    private ServiceDscDevUtil serviceDscDevUtil;

    private DownLoadH5 downLoadH5;

    private Timer timer;

    private Context context;

    private Toastor toastor;

    private String IMEI;

    /**
     * 首次创建服务时，系统将调用此方法来执行一次性设置程序（在调用 onStartCommand() 或 onBind() 之前）。如果服务已在运行，则不会调用此方法。
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "--onCreate--");
        EventBus.getDefault().register(this);
        context = this;
    }

    /**
     * 当另一个组件（如 Activity）通过调用 startService() 请求启动服务时，系统将调用此方法。一旦执行此方法，服务即会启动并可在后台无限期运行。
     * 如果您实现此方法，则在服务工作完成后，需要由您通过调用 stopSelf() 或 stopService() 来停止服务。（如果您只想提供绑定，则无需实现此方法。）
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "--onStartCommand--");
        initData();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * ws通道协议过滤器{@link FilterBean}容器filterQueue初始化
     * jwt获取 {@link HekrUserAction}初始化
     * 控制页面下载工具类{@link DownLoadH5}初始化
     * 局域网设备发现工具类{@link ServiceDscDevUtil}初始化
     * 开启网络变化监听广播{@link #createReceiver()}
     * 开启过滤器超时扫描{@link #filterScan()}
     * toast初始化
     * 获取手机唯一标识IMEI（International Mobile Equipment Identity）是国际移动设备标识的缩写，IMEI由15位数字(英文字母)组成
     * <p/>
     * 单卡手机肯定只有一个IMEI号，双卡双待手机不一定是双IMEI号。移动和联通的双卡双待手机就有两个IMEI号，因为它们的两个卡槽除了有一个可以装3G卡外，还可以装GSM卡，
     * 电信版的双卡双模双待手机只有一个IMEI 号，因为它的两个卡槽中，有一个只能装一张GSM卡，另一个卡槽只能装3G卡. 3G和GSM制式不同，它们处在两个不同的平台，可以区分，
     * 所以电信版的只有一个IMEI号，而联通和移动的机子有可能用的是同一家运营商的两张GSM 卡，处在同一个平台，两个手机号的通讯信息可能存在干扰，
     * 因此同一家运营商要区分两个卡，所以手机设置了两个IMEI号。
     */
    @SuppressLint("HardwareIds")
    private void initData() {
        try {
            filterQueue = new CopyOnWriteArraySet<>();
            if (hekrUserAction == null) {
                hekrUserAction = HekrUserAction.getInstance(this);
            }
            downLoadH5 = new DownLoadH5(this);
            serviceDscDevUtil = new ServiceDscDevUtil(this);
            context = this;

            createReceiver();

            filterScan();
            toastor = new Toastor(this.getApplicationContext());
            /*IMEI = TextUtils.concat(TelephoneUtil.getIMEI(this), HekrSDK.pid).toString();
            if (TextUtils.isEmpty(TelephoneUtil.getIMEI(this))) {
                IMEI = TextUtils.concat(Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID), HekrSDK.pid).toString();
            }*/
            IMEI = HekrCommonUtil.getHEKRIMEI(this);
        } catch (Exception e) {
            Log.i(TAG, "HekrCoreService:IMEI为空!");
            e.printStackTrace();
        }
    }

    /**
     * 外网通道:接收网络状态变化，控制ws切换(每次重启app都会触发有网变化动作，进入之后有网并且jwtToken不为空那么进行ws连接)
     * 内网通道:切换网络清空{@link Global}；重新开启局域网发现serviceDscDevUtil
     *
     * @param event {@link NetworkEvent} 网络状态标识
     *              1、网络发生变化：接收到网络可用动作
     *              2、网络发生变化：接收到网络不可用动作
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(NetworkEvent event) {
        if (event != null) {
            switch (event.getNetStatus()) {
                case ConstantsUtil.ServiceCode.NETWORK_AVAILABLE:
                    Log.i(TAG, "收到网络变化动作:有网");
                    if (isAllowConnectWs()) {
                        if (!mConnection.isConnected()) {
                            Log.i(TAG, "接收到NetworkEvent(网络可用动作)，并且当前ws未连接，进行ws连接");
                            connect(ConstantsUtil.UrlUtil.APP_WEBSOCKET_CONNECT_CLOUD_URL);
                        }
                    }

                    Runnable stopRunnable = new Runnable() {
                        @Override
                        public void run() {
                            //局域网处理
                            Global.lanList.clear();
                            serviceDscDevUtil.stopSearch();
                        }
                    };

                    Runnable startRunnable = new Runnable() {
                        @Override
                        public void run() {
                            serviceDscDevUtil.startSearch();
                        }
                    };

                    singleThreadExecutor.submit(stopRunnable);
                    singleThreadExecutor.submit(startRunnable);
                    break;
                case ConstantsUtil.ServiceCode.NETWORK_NO:
                    Log.i(TAG, "收到网络变换动作:无网");
                    if (mConnection.isConnected()) {
                        Log.i(TAG, "接收到NetworkEvent(网络不可用动作):关闭心跳包");
                        stopTimer();
                        Log.i(TAG, "接收到NetworkEvent(网络不可用动作):主动断开ws");
                        mConnection.disconnect();
                    }

                    //局域网处理
                    Global.lanList.clear();
                    if (lanUtilList != null && !lanUtilList.isEmpty()) {
                        for (int i = lanUtilList.size() - 1; i >= 0; i--) {
                            lanUtilList.get(i).getLanUtil().close();
                            lanUtilList.remove(i);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 局域网中发现有可用设备，初始化{@link LANUtil}
     *
     * @param event {@link FindDeviceBean}(wifi模块通过mDNS发出的设备信息bean)
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CreateSocketEvent event) {

        if (event != null && event.getFindDeviceBean() != null) {
            FindDeviceBean findDeviceBean = event.getFindDeviceBean();
            //Log.i(TAG, "局域网内新设备:" + findDeviceBean + "\n");

            if (lanUtilList != null) {
                if (!lanUtilList.isEmpty()) {
                    int i = lanUtilList.size() - 1;
                    //遍历已有list中的设备
                    while (i >= 0) {
                        if (TextUtils.equals(findDeviceBean.getDevTid(), lanUtilList.get(i).getFindDeviceBean().getDevTid())) {
                            lanUtilList.get(i).getLanUtil().close();
                            lanUtilList.remove(i);

                            Log.i(TAG, "lanUtilList有该设备刷新设备信息" + "ip:" +
                                    findDeviceBean.getServiceIp() + "devTid:" +
                                    findDeviceBean.getDevTid());
                            addUsefulUDP(findDeviceBean);
                            break;
                        }
                        if (i == 0) {
                            Log.i(TAG, "lanUtilList添加新设备>>DevTid:" + findDeviceBean.getDevTid() +
                                    ">>ServicePort:" + findDeviceBean.getServicePort() +
                                    ">>Ip:" + findDeviceBean.getServiceIp());
                            addUsefulUDP(findDeviceBean);
                            break;
                        }
                        i--;
                    }
                } else {
                    Log.i(TAG, "lanUtilList元素为空添加新设备>>DevTid:" + findDeviceBean.getDevTid() +
                            ">>ServiceIp:" + findDeviceBean.getServiceIp() +
                            ">>ServicePort:" + findDeviceBean.getServicePort());
                    addUsefulUDP(findDeviceBean);
                }
            }
            if (lanUtilList != null) {
                Log.i(TAG, "lanUtilList个数：" + lanUtilList.size() + ">>数据：" + lanUtilList.toString());
            }
        }
    }

    /**
     * 输入账号密码登录，退出登录:控制ws的切换
     *
     * @param event {@link WsSwitchEvent}用户登录登出动作标记
     *              1、登出
     *              2、登录
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(WsSwitchEvent event) {
        if (event != null) {
            switch (event.getStatus()) {
                //用户退出当前账号
                case ConstantsUtil.EventCode.WS_SWITCH_EVENT_STATUS_DISCONNECT:
                    if (mConnection.isConnected()) {
                        Log.i(TAG, "接收到WsSwitchEvent(退出当前账号):关闭心跳包");
                        stopTimer();
                        Log.i(TAG, "接收到WsSwitchEvent(退出当前账号):主动断开ws");
                        mConnection.disconnect();
                    }
                    break;
                //用户输入账号密码登录
                case ConstantsUtil.EventCode.WS_SWITCH_EVENT_STATUS_CONNECT:
                    if (isAllowConnectWs()) {
                        if (!mConnection.isConnected()) {
                            Log.i(TAG, "接收到WsSwitchEvent(用户登录):主动连接ws");
                            connect(ConstantsUtil.UrlUtil.APP_WEBSOCKET_CONNECT_CLOUD_URL);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 数据发送，1、ws发送 2、udp发送，3、ws设定接收设备返回数据过滤器4、局域网设定接收设备返回数据过滤器
     *
     * @param event {@link CommandEvent}
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CommandEvent event) {
        if (event != null) {
            switch (event.getCommand()) {
                case ConstantsUtil.ServiceCode.LAN_DATA_SEND_WHAT:
                    if (Network.isAvailable(this)) {
                        if (isHaveLanDev(Global.lanList, event.getCtrlBean()) != null) {
                            FindDeviceBean findDeviceBean = isHaveLanDev(Global.lanList, event.getCtrlBean());
                            for (int i = 0; i < lanUtilList.size(); i++) {
                                if (TextUtils.equals(findDeviceBean.getDevTid(), lanUtilList.get(i).getFindDeviceBean().getDevTid())) {
                                    lanUtilList.get(i).getLanUtil().send(event.getCtrlBean().getObject(), event.getCtrlBean().getDevTid(), event.getCtrlBean().getData(), IMEI,
                                            event.getCtrlBean().getDataReceiverListener());
                                    //Log.i(TAG, "udp通道发送");
                                }
                            }
                        } else {
                            //Log.i(TAG, "ws通道发送");
                            EventBus.getDefault().post(new CommandEvent(ConstantsUtil.ServiceCode.WS_DATA_SEND_WHAT, event.getCtrlBean()));
                        }
                        //} else {
                        //Log.i(TAG, "udp通道无网");
                    }
                    break;
                case ConstantsUtil.ServiceCode.WS_DATA_SEND_WHAT:
                    if (event.getCtrlBean() != null && Network.isAvailable(this)) {
                        sentMsg(event.getCtrlBean().getObject(), event.getCtrlBean().getDevTid(), event.getCtrlBean().getData(), event.getCtrlBean().getDataReceiverListener());
                    } else {
                        Log.i(TAG, "ws通道无网");
                    }
                    break;
                case ConstantsUtil.ServiceCode.LAN_DATA_RECEIVE_WHAT:
                    if (isHaveLanDev(Global.lanList, event.getCtrlBean()) != null) {
                        FindDeviceBean findDeviceBean = isHaveLanDev(Global.lanList, event.getCtrlBean());
                        for (int i = 0; i < lanUtilList.size(); i++) {
                            if (TextUtils.equals(findDeviceBean.getDevTid(), lanUtilList.get(i).getFindDeviceBean().getDevTid())) {
                                lanUtilList.get(i).getLanUtil().receiveMsg(event.getCtrlBean().getObject(), event.getCtrlBean().getData(),
                                        event.getCtrlBean().getDataReceiverListener());
                                Log.i(TAG, "UDP通道设置设备消息主动上报监听过滤器");
                            }
                        }
                    }
                    break;
                case ConstantsUtil.ServiceCode.WS_DATA_RECEIVE_WHAT:
                    if (event.getCtrlBean() != null) {
                        receiveMsg(event.getCtrlBean().getObject(), event.getCtrlBean().getData(), event.getCtrlBean().getDataReceiverListener());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * scheduledExecutorService.scheduleAtFixedRate中直接进行超时回调会导致{@link #filterScan()}直接挂掉
     * ws中协议过滤器10秒之后收不到响应，则回调超时
     *
     * @param event 回调接口{@link MsgCallbackEvent}
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MsgCallbackEvent event) {
        if (event != null) {
            switch (event.getCallBackType()) {
                case MsgCallbackEvent.TIMEOUTCALLBACK:
                    Log.i(TAG, "超时回调:" + event.getFilterBean());
                    debugView("超时回调:" + event.getFilterBean().toString());
                    event.getFilterBean().getDataReceiverListener().onReceiveTimeout();
                    break;
                case MsgCallbackEvent.NORMALCALLBACK:
                    event.getFilterBean().getDataReceiverListener().onReceiveSuccess(event.getMsg());
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 收到外部webView销毁消息,清除接收过滤器
     *
     * @param event 清除标志{@link ClearFilterEvent}
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ClearFilterEvent event) {
        switch (event.getClearType()) {
            case ClearFilterEvent.CLEARALLFILTER:
                filterQueue.clear();

                if (lanUtilList != null && !lanUtilList.isEmpty()) {
                    for (int i = lanUtilList.size() - 1; i >= 0; i--) {
                        lanUtilList.get(i).getLanUtil().clear();
                    }
                }
                break;
            case ClearFilterEvent.CLEARDEVSENDFILTER:
                if (filterQueue != null) {
                    for (Object o : filterQueue.toArray()) {
                        FilterBean f = (FilterBean) o;
                        if (!f.isOnce()) {
                            filterQueue.remove(f);
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 下载前端页面
     *
     * @param event {@link DownLoadEvent}
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DownLoadEvent event) {
        if (event != null &&
                event.getDeviceBean() != null &&
                Network.isWifiConnected(this) &&
                !TextUtils.isEmpty(event.getDeviceBean().getAndroidPageZipURL()) &&
                event.getDeviceBean().getAndroidPageZipURL().endsWith(".zip")) {
            Log.i(TAG, "开始下载!");
            try {
                String zip = URLDecoder.decode(event.getDeviceBean().getAndroidPageZipURL(), "utf-8");
                downLoadH5.startDownLoadH5(zip, HekrCodeUtil.zip2Folder(zip));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 打开ws连接，并发送appLogin action登录氦氪云ws服务
     */
    @Override
    public void onOpen() {
        try {
            Log.i(TAG, "onOpen " + ConstantsUtil.UrlUtil.APP_WEBSOCKET_CONNECT_CLOUD_URL);
            JSONObject jsonObject = new JSONObject();
            JSONObject result = new JSONObject();

            jsonObject.put("appTid", IMEI);
            jsonObject.put("token", hekrUserAction.getJWT_TOKEN());

            result.put("msgId", MSG_COUNT.intValue());
            result.put("action", "appLogin");
            result.put("params", jsonObject);
            //Log.i(TAG, "param:" + result.toString());
            if (isAllowConnectWs()) {
                if (mConnection.isConnected()) {
                    Log.i(TAG, "发送登录命令:" + result);
                    wsSendTextMsg(result.toString() + "\n");
                } else {
                    Log.i(TAG, "onOpen:发送登录指令ws并未连接，执行ws连接");
                    connect(ConstantsUtil.UrlUtil.APP_WEBSOCKET_CONNECT_CLOUD_URL);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * ws关闭
     */
    @Override
    public void onClose(int i, String s) {
        Log.i(TAG, "onClose:" + s);
    }

    /**
     * 1、app重复登录处理(断开重连)
     * 2、非心跳命令处理(接受收的数据和过滤器容器中过滤器对比，如果有则进行回调并在容器中删除相应的过滤器)
     * 发出一条包含原数据的广播：用于外部sdk使用者接收
     *
     * @param payload 氦氪云ws返回所有数据
     */
    @Override
    public void onTextMessage(String payload) {
        try {
            //Log.i(TAG,"当前："+getCurrentActivityName());
            JSONObject jsonObject = null;
            if (!TextUtils.isEmpty(payload)) {
                jsonObject = new JSONObject(payload);
            }
            if (jsonObject != null && jsonObject.has("action")) {
                //Log.i(TAG, "ws接收到云端返回的desc:" + jsonObject.getString("desc").toString());
                if (!"heartbeatResp".equals(jsonObject.getString("action"))) {
                    Log.i(TAG, "云端返回数据:" + payload);
                }
                if ("appLoginResp".equals(jsonObject.getString("action"))) {
                    if (ConstantsUtil.ServiceCode.APP_REPEAT_LOGIN == jsonObject.getInt("code")) {
                        Log.i(TAG, "onTextMessage:app重复登录hekr云服务,主动断开");
                        mConnection.disconnect();
                        Log.i(TAG, "onTextMessage:app重复登录hekr云服务,重新连接ws");
                        connect(ConstantsUtil.UrlUtil.APP_WEBSOCKET_CONNECT_CLOUD_URL);
                    } else if (200 == jsonObject.getInt("code")) {
                        Log.i(TAG, "ws连接成功,并成功连接到hekr云服务");
                        startTimer();
                    }
                    /*else if (1400002 == jsonObject.getInt("code")) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                connect(ConstantsUtil.UrlUtil.APP_WEBSOCKET_CONNECT_CLOUD_URL);
                            }
                        }, 5000);
                    }*/
                }
                if (!"heartbeatResp".equals(jsonObject.getString("action"))) {
                    debugView(payload);
                    //Log.i(TAG, "ws接收到云端返回的data:" + payload + "\n");
                    if (filterQueue != null) {
                        for (Object o : filterQueue.toArray()) {
                            FilterBean f = (FilterBean) o;
                            if (ProtocolFilterUtil.dictMatch(f.getFilter(), jsonObject)) {
                                //Log.i(TAG, "sentFilterQueue.get>>>" + i + ">>>:" + filterQueue.get(i).toString());
                                Log.i(TAG, "外网回调数据>>>:" + jsonObject.toString());
                                //f.getDataReceiverListener().onReceiveSuccess(jsonObject.toString());
                                EventBus.getDefault().post(new MsgCallbackEvent(MsgCallbackEvent.NORMALCALLBACK, f, jsonObject.toString()));
                                if (f.isOnce()) {
                                    Log.i(TAG, "移除正常回调过滤器：" + f.toString());
                                    //上报本条命令控制所花时间（过滤器的时间是加了超时时间10秒的）
                                    long spendTime = System.currentTimeMillis() - f.getTimeStamp() + 10 * 1000;
                                    if (f.getFilter() != null &&
                                            f.getFilter().has("params") &&
                                            f.getFilter().getJSONObject("params").has("devTid")) {
                                        reportDevLog(f.getFilter().getString("msgId"), spendTime);
                                    }
                                    filterQueue.remove(f);
                                }
                            }
                        }
                    }
                    //Log.i(TAG, "onTextMessage:sentFilterQueue个数:" + filterQueue.size() + "数值:" + filterQueue.toString() + "\n");
                    Intent intent = new Intent();
                    intent.putExtra(ConstantsUtil.HEKR_WS_PAYLOAD, payload);
                    intent.setAction(ConstantsUtil.ActionStrUtil.ACTION_WS_DATA_RECEIVE);
                    sendBroadcast(intent);
                }
                //配网时云端返回数据
                if ("devBind".equals(jsonObject.getString("action"))) {
                    EventBus.getDefault().post(new ConfigStatusEvent(payload));
                }

                if ("addSubDev".equals(jsonObject.getString("action"))) {
                    EventBus.getDefault().post(new SubDeviceConfigEvent(com.alibaba.fastjson.JSON.parseObject(payload, SubDeviceConfigBean.class)));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //上报设备控制所花时间
    private void reportDevLog(String sendMsgId, long time) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", "reportAppLog");
            jsonObject.put("msgId", MSG_COUNT.intValue());
            JSONObject params = new JSONObject();
            params.put("appTid", IMEI);
            params.put("reportAction", "appSend");
            String logContent = TextUtils.concat("uid=", hekrUserAction.getUserId(), ",uuid=", IMEI, ",msgId=", sendMsgId, ",action=", "appSend", ",ts=", time + "").toString();
            if (!TextUtils.isEmpty(logContent)) {
                params.put("logContent", logContent);
            }
            jsonObject.put("params", params);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (mConnection.isConnected() && jsonObject.has("action") && jsonObject.has("msgId") && jsonObject.has("params")) {
            mConnection.sendTextMessage(jsonObject.toString() + "\n");
            Log.i(TAG, "appSend检测数据上报:" + jsonObject.toString());
            addMsgCount();
        }
    }

    /**
     * 1、生成协议过滤器(msgId,action,devTid)json格式
     * 2、ws发送命令至氦氪云
     *
     * @param object               外部引用 例：webView
     * @param devTid               设备tid
     * @param data                 外部传入控制命令
     * @param dataReceiverListener 外部传入回调
     */
    public synchronized void sentMsg(Object object, String devTid, JSONObject data, DataReceiverListener dataReceiverListener) {
        try {
            if (data != null) {
                data.put("msgId", MSG_COUNT.intValue());
                if (data.has("params")) {
                    data.getJSONObject("params").put("appTid", IMEI);
                }
            }
            if (mConnection.isConnected()) {
                if (!TextUtils.isEmpty(IMEI)) {
                    if (data != null && data.has("msgId") && data.has("params")) {

                        //协议过滤器
                        JSONObject filterObject = new JSONObject();
                        filterObject.put("msgId", MSG_COUNT.intValue());
                        filterObject.put("action", TextUtils.concat(data.getString("action"), "Resp"));
                        if(!TextUtils.isEmpty(devTid)) {
                            JSONObject params = new JSONObject();
                            params.put("devTid", devTid);
                            filterObject.put("params", params);
                        }

                        FilterBean filterBean = new FilterBean(object, System.currentTimeMillis() + (10 * 1000), filterObject, true, dataReceiverListener, data);

                        //Log.i(TAG,"sentMsg:filterBean:"+filterBean.toString());

                        filterQueue.add(filterBean);

                        Log.i(TAG, "sentMsg:sentFilterQueue个数:" + filterQueue.size() + "数值:" + filterQueue.toString() + "\n");

                        Log.i(TAG, "ws发送到云端的data:" + data.toString());
                        wsSendTextMsg(data.toString());

                    } else {
                        Log.i(TAG, "ws发送到云端的data为空:发送失败");
                    }
                } else {
                    toastor.showSingletonToast("IMEI为空,请检查权限!");
                }
            } else {
                if (isAllowConnectWs()) {
                    Log.i(TAG, "sentMsg:发送控制命令发现ws未连接，进行ws连接");
                    connect(ConstantsUtil.UrlUtil.APP_WEBSOCKET_CONNECT_CLOUD_URL);
                }
            }

        } catch (JSONException e) {
            Log.i(TAG, "ws发送到云端的data:异常sentMsg()");
            e.printStackTrace();
        }
    }

    /**
     * 添加接收设备主动上报命令的过滤器
     *
     * @param object               外部引用 例：webView
     * @param filter               接收设备主动上报状态过滤器
     * @param dataReceiverListener 命令回调
     */
    public void receiveMsg(Object object, JSONObject filter, DataReceiverListener dataReceiverListener) {

        String sDt = "01/01/2999 00:00:00";
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", getResources().getConfiguration().locale);
        long maxTime = 0;
        Date dt;
        try {
            dt = sdf.parse(sDt);
            maxTime = dt.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        FilterBean filterBean = new FilterBean(object, maxTime, filter, false, dataReceiverListener, null);
        //Log.i(TAG,"receiveMsg:filterBean:"+filterBean.toString());

        filterQueue.add(filterBean);
        Log.i("Protocol", "receiveMsg:sentFilterQueue个数:" + filterQueue.size() + "数值:" + filterQueue.toString() + "\n");
    }

    /**
     * 扫描过滤器是否失效
     */
    private void filterScan() {
        Log.i(TAG, "过滤器扫描线程开启");
        try {
            scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    //Log.i(TAG,getCurrentActivityName());
                    long nowTime = System.currentTimeMillis();
                    //Log.i("filterScan","扫描过滤器是否有效");
                    for (FilterBean f : filterQueue) {
                        //当前时间超过每个过滤器存活时间(10秒),并且过滤器本身生命时间超过0秒
                        if (nowTime > f.getTimeStamp() && f.getTimeStamp() > 0) {
                            EventBus.getDefault().post(new MsgCallbackEvent(MsgCallbackEvent.TIMEOUTCALLBACK, f));
                            if (f.isOnce()) {
                                Log.i(TAG, "移除超时回调过滤器：" + f.toString());
                            /*try {
                                reportDevLog(f.getFilter().getJSONObject("params").getString("devTid"),f.getFilter().getString("msgId"),-1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }*/
                                filterQueue.remove(f);
                            }
                        }
                    }
                }
            }, TIMEOUT, TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            Log.i(TAG, "消息超时检测线程池启动失败");
        }
    }

    /**
     * ws连接
     */
    private synchronized void connect(final String wsUrl) {
        try {
            mConnection.connect(wsUrl, this);
        } catch (WebSocketException e) {
            Log.e(TAG, e.toString());
        }
    }

    //心跳包发送
    class PingTask extends TimerTask {
        @Override
        public void run() {
            try {
                if (mConnection.isConnected()) {
                    sendPing();
                } else {
                    Log.i(TAG, "ws未连接,未发送心跳");
                    if (isAllowConnectWs()) {
                        Log.i(TAG, "PingTask-run:ws断开,进行主动重连");
                        Looper.prepare();
                        connect(ConstantsUtil.UrlUtil.APP_WEBSOCKET_CONNECT_CLOUD_URL);
                        Looper.loop();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送心跳命令
     */
    private void sendPing() {
        try {
            JSONObject jo = new JSONObject();
            jo.put("msgId", MSG_COUNT.intValue());
            jo.put("action", "heartbeat");
            //Log.i(TAG, "向云端发送心跳包:" + jo.toString());
            mConnection.sendTextMessage(jo.toString() + "\n");
            addMsgCount();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开启心跳包
     */
    private void startTimer() {
        stopTimer();

        if (timer == null) {
            timer = new Timer();
        }

        PingTask pingTask = new PingTask();

        if (timer != null) {
            timer.schedule(pingTask, 0, HEART_TIME);
        }
    }

    /**
     * 关闭心跳包
     */
    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    /**
     * msgId自增
     */
    private void addMsgCount() {
        MSG_COUNT.incrementAndGet();
        MSG_COUNT.compareAndSet(65535, 0);
    }

    /**
     * @return 是否有网以及jwtToken是否为空
     */
    private boolean isAllowConnectWs() {
        context = this;
        if (hekrUserAction == null) {
            hekrUserAction = HekrUserAction.getInstance(context);
        }
        return Network.isConnected(context) && !TextUtils.isEmpty(hekrUserAction.getJWT_TOKEN());
    }

    @Override
    public void onBinaryMessage(byte[] payload) {
        Log.i(TAG, new String(payload) + "\n");
    }

    @Override
    public void onRawTextMessage(byte[] payload) {
        Log.i(TAG, new String(payload));
    }

    /**
     * 发送webSocket数据
     */
    private void wsSendTextMsg(String msg) {
        mConnection.sendTextMessage(msg);
        addMsgCount();
        debugView(msg);
    }

    /**
     * debugView界面
     */
    private void debugView(String msg) {
        ViewWindow.showView(TextUtils.concat("WS:", msg).toString());
    }

    /**
     * @param findDeviceBean 发现的设备信息
     */
    private void addUsefulUDP(FindDeviceBean findDeviceBean) {
        LANUtil lanUtil = new LANUtil(this, findDeviceBean.getServiceIp(), findDeviceBean.getServicePort(), findDeviceBean.getDevTid());
        LanUtilBean lanUtilBean = new LanUtilBean(findDeviceBean, lanUtil);
        lanUtilList.add(lanUtilBean);
    }

    private FindDeviceBean isHaveLanDev(List<FindDeviceBean> list, CtrlBean ctrlBean) {
        if (list == null || ctrlBean == null) {
            return null;
        }
        String devTid = ctrlBean.getDevTid();
        if (list.isEmpty() || TextUtils.isEmpty(devTid)) {
            return null;
        }
        for (int i = 0; i < list.size(); i++) {
            if (TextUtils.equals(devTid, list.get(i).getDevTid())) {
                return list.get(i);
            }
        }
        return null;
    }

    /**
     * 创建监听网络变化广播
     */
    public void createReceiver() {
        // 创建网络监听广播
        if (connectionReceiver == null) {
            connectionReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                        ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
                        if (netInfo != null && netInfo.isAvailable()) {
                            Log.i(TAG, "网络变化：有网");
                            EventBus.getDefault().post(new NetworkEvent(ConstantsUtil.ServiceCode.NETWORK_AVAILABLE));
                        } else {
                            Log.i(TAG, "网络变化：无网");
                            EventBus.getDefault().post(new NetworkEvent(ConstantsUtil.ServiceCode.NETWORK_NO));
                        }
                    }
                }
            };
            // 注册网络监听广播
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(connectionReceiver, intentFilter);
        }
    }

    /**
     * 当另一个组件想通过调用 bindService() 与服务绑定（例如执行 RPC）时，系统将调用此方法。
     * 在此方法的实现中，您必须通过返回 IBinder 提供一个接口，供客户端用来与服务进行通信。
     * 请务必实现此方法，但如果您并不希望允许绑定，则应返回 null。
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "WS服务结束");
        EventBus.getDefault().unregister(this);
        scheduledExecutorService.shutdownNow();
        if (connectionReceiver != null) {
            unregisterReceiver(connectionReceiver);
        }
        if (mConnection.isConnected()) {
            mConnection.disconnect();
        }
        stopTimer();
        super.onDestroy();
    }

}
