package com.siterwell.sdk.util;

import android.content.Context;
import android.text.TextUtils;

import com.siterwell.sdk.bean.CtrlBean;
import com.siterwell.sdk.bean.FilterBean;
import com.siterwell.sdk.event.CommandEvent;
import com.siterwell.sdk.event.MsgCallbackEvent;
import com.siterwell.sdk.listener.DataReceiverListener;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Administrator on 2017/10/16.
 */

public class LANUtil {
    private static final String TAG = "LANUtil";

    private String ip;

    private int port;

    private String device_tid;

    //过滤器扫描时间间隔
    private static final int TIMEOUT = 1000;

    //心跳包发送间隔时间
    private static final int HEART_BEAT_RATE = 60 * 1000;

    //消息id标识
    private AtomicInteger MSG_COUNT = new AtomicInteger(1);

    //消息过滤器
    private Set<FilterBean> udpFilterQueue = new CopyOnWriteArraySet<>();

    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    Timer heartbeat = new Timer();//心跳计时器

    private AtomicBoolean onHeartOut = new AtomicBoolean(false);//标示定时器状态

    //接收数据字节数组
    private byte[] buffer = new byte[1024];

    //udp套接字
    private DatagramSocket ds = null;

    private Context context;

    private AtomicBoolean isRecvData = new AtomicBoolean(false);

    /**
     * 构造函数，创建UDP客户端
     */
    public LANUtil(Context context, final String ip, final int port, final String devTid) {
        this.context = context;
        this.ip = ip;
        this.port = port;
        this.device_tid = devTid;

        isRecvData.set(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ds = new DatagramSocket();

                    ds.connect(InetAddress.getByName(ip), port);

                    if (ds != null && ds.isConnected()) {
                        Log.i(TAG, "设备：" + devTid + ">>已连接:" + InetAddress.getByName(ip));
                        /*if (!onHeartOut.get()) {
                            startHeartBeat();
                        }*/
                        filterScan();
                    }

                    //接收数据
                    while (!isRecvData.get() && ds != null && !ds.isClosed() && ds.isConnected()) {
                        //Log.i(TAG, "等待接收数据:");
                        if (ds == null || ds.isClosed() || !ds.isConnected()) {
                            isRecvData.set(true);
                            break;
                        }
                        receive();
                    }

                } catch (SocketException | UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 向指定的服务端发送数据信息.
     *
     * @param msg 发送的数据信息
     * @throws IOException
     */
    private void send(String msg) throws IOException {
        if (ds != null) {
            byte[] buf = msg.getBytes("utf-8");
            DatagramPacket dp = new DatagramPacket(buf, buf.length, InetAddress.getByName(ip), port);
            if (!ds.isClosed() && ds.isConnected()) {
                Log.i(TAG, "UDP向设备发送心跳包:" + msg);
                ds.send(dp);
            } else {
                try {
                    if (onHeartOut.get()) {
                        ds.disconnect();
                        ds.connect(InetAddress.getByName(ip), port);
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param devTid               设备tid
     * @param data                 需要发送的部分数据
     * @param IMEI                 appTid
     * @param dataReceiverListener 数据回调接口
     */
    public synchronized void send(final Object object, final String devTid, final JSONObject data, String IMEI, final DataReceiverListener dataReceiverListener) {
        try {
            //提取过滤器
            if (!onHeartOut.get()) {
                startHeartBeat();
            }
            final JSONObject filterObject = new JSONObject();

            filterObject.put("msgId", MSG_COUNT.intValue());
            filterObject.put("action", TextUtils.concat(data.getString("action"), "Resp"));
            JSONObject params = new JSONObject();
            params.put("devTid", devTid);
            filterObject.put("params", params);

            if (ds != null) {
                data.put("msgId", MSG_COUNT.intValue());
                data.getJSONObject("params").put("appTid", IMEI);

                //Log.i(TAG, "udp发送到设备data:" + data.toString());

                byte[] buf = (data.toString() + "\n").getBytes();
                DatagramPacket dp = null;
                try {
                    dp = new DatagramPacket(buf, buf.length, InetAddress.getByName(ip), port);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                //Log.i(TAG,"ip:"+ip+">>>port:"+port);

                final DatagramPacket finalDp = dp;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            if (!ds.isClosed() && ds.isConnected()) {

                                //协议过滤器
                                //Log.i(TAG, "filterObject:" + filterObject);
                                FilterBean filterBean = new FilterBean(object, System.currentTimeMillis() + 3 * 1000, filterObject, true, dataReceiverListener, data);
                                udpFilterQueue.add(filterBean);
                                Log.i(TAG, "send:sentFilterQueue个数:" + udpFilterQueue.size() + "数值:" + udpFilterQueue.toString());

                                //Log.i(TAG, "UDP发送到设备data:" + data.toString());
                                Log.i(TAG, "局域网发送到设备data:" + data.toString() + "ip:" + ip + "port:" + port);
                                debugView("局域网:" + data.toString() + "ip:" + ip + "port:" + port);
                                ds.send(finalDp);
                                addMsgCount();
                            } else {
                                EventBus.getDefault().post(new CommandEvent(ConstantsUtil.ServiceCode.WS_DATA_SEND_WHAT, new CtrlBean(object, devTid, filterObject, dataReceiverListener)));
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } else {
                //直接走云端ws通道
                EventBus.getDefault().post(new CommandEvent(ConstantsUtil.ServiceCode.WS_DATA_SEND_WHAT, new CtrlBean(object, devTid, filterObject, dataReceiverListener)));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 接收从指定的服务端发回的数据.
     */
    private void receive() {

        // 创建用来发送数据报包的套接字
        try {
            if (ds != null && !ds.isClosed() && ds.isConnected()) {
                DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
                ds.receive(dp);
                String msg = new String(dp.getData(), 0, dp.getLength());

                if (!TextUtils.isEmpty(msg)) {
                    JSONObject jsonObject = new JSONObject(msg);

                    if (!"heartbeatResp".equals(jsonObject.getString("action")) && udpFilterQueue != null) {
                        Log.i(TAG, "局域网收到数据:" + msg);
                        debugView("局域网:" + msg);
                        for (FilterBean f : udpFilterQueue) {
                            if (ProtocolFilterUtil.dictMatch(f.getFilter(), jsonObject)) {
                                //android.util.Log.i(TAG, "sentFilterQueue.get>>>" + i + ">>>:" + udpFilterQueue.get(i).toString());
                                try {
                                    Log.i(TAG, "局域网回调数据:" + jsonObject.toString());
                                    EventBus.getDefault().post(new MsgCallbackEvent(MsgCallbackEvent.NORMALCALLBACK,f,jsonObject.toString()));
                                    //f.getDataReceiverListener().onReceiveSuccess(jsonObject.toString());
                                    if (f.isOnce()) {
                                        udpFilterQueue.remove(f);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        Log.i(TAG, "receive:sentFilterQueue个数:" + udpFilterQueue.size() + "数值:" + udpFilterQueue.toString());
                    }
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public void receiveMsg(Object object, final JSONObject filter, final DataReceiverListener dataReceiverListener) {

        String sDt = "01/01/2999 00:00:00";
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", context.getResources().getConfiguration().locale);
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

        udpFilterQueue.add(filterBean);
        //Log.i(TAG, "receiveMsg:sentFilterQueue个数:" + udpFilterQueue.size() + "数值:" + udpFilterQueue.toString() + "\n");
    }

    //外部webView关闭时,清除相关的消息过滤器
    public void clear() {
        if (udpFilterQueue != null) {
            udpFilterQueue.clear();
        }
    }

    public void removeDevSendFilter(){
        if (udpFilterQueue != null) {
            for (Object o : udpFilterQueue.toArray()) {
                FilterBean f = (FilterBean) o;
                if (!f.isOnce()) {
                    udpFilterQueue.remove(f);
                }
            }
        }
    }

    /**
     * 关闭udp连接.
     */
    public void close() {
        try {
            if (onHeartOut.get()) {
                heartbeat.cancel();
                heartbeat.purge();
                onHeartOut.set(false);
            }
            isRecvData.set(true);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if(ds!=null) {
                        ds.disconnect();
                        ds.close();
                    }
                }
            }).start();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 启动超时任务
     */
    private void filterScan() {

        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                //ds.close();
                //Log.i(TAG, "局域网接收数据超时--使用ws发送!");
                long nowTime = System.currentTimeMillis();

                for (FilterBean f : udpFilterQueue) {
                    if (nowTime > f.getTimeStamp()) {

                        Log.i(TAG, "局域网接收数据超时--使用ws发送" + f.toString());

                        EventBus.getDefault().post(new CommandEvent(ConstantsUtil.ServiceCode.WS_DATA_SEND_WHAT, new CtrlBean(f.getObject(), device_tid, f.getData(), f.getDataReceiverListener())));

                        if (f.isOnce()) {
                            udpFilterQueue.remove(f);
                        }
                    }
                }
            }
        }, TIMEOUT, TIMEOUT, TimeUnit.MILLISECONDS);
    }

    /**
     * 启动心跳任务
     */
    private void startHeartBeat() {
        onHeartOut.set(true);

        heartbeat.schedule(new TimerTask() {
            @Override
            public void run() {
                JSONObject jo = new JSONObject();
                try {
                    jo.put("msgId", MSG_COUNT.intValue());
                    jo.put("action", "heartbeat");
                    send(jo.toString() + "\n");
                    addMsgCount();
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

            }
        }, 0, HEART_BEAT_RATE);
    }

    private void addMsgCount() {
        MSG_COUNT.incrementAndGet();
        MSG_COUNT.compareAndSet(65535, 1);
    }

    /**
     * debugView界面
     */
    private void debugView(String msg) {
        ViewWindow.showView(msg);
    }
}
