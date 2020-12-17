package me.siter.sdk.dispatcher;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import me.siter.sdk.FilterType;
import me.siter.sdk.SiterSDK;
import me.siter.sdk.IMessageRequest;
import me.siter.sdk.inter.SiterDispatcherListener;
import me.siter.sdk.inter.SiterMsgCallback;
import me.siter.sdk.service.SiterConnectionService;
import me.siter.sdk.service.IMsgObserver;
import me.siter.sdk.service.ServiceBinder;
import me.siter.sdk.service.ServiceMonitor;
import me.siter.sdk.utils.AndroidErrorMap;
import me.siter.sdk.utils.LogUtil;


/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: 消息上下传递的通道,作用时上下分发和过滤
 */

public class Dispatcher extends IDispatcher implements IMsgObserver {

    private static final String TAG = Dispatcher.class.getSimpleName();

    private static final int CHECK_INTERVAL = 2 * 1000;
    private static Dispatcher instance;

    private CallbackDelivery mDelivery;
    private CopyOnWriteArrayList<FilterNet> mCallBackQueue;
    private Handler mTimerHandler;
    private boolean isStarted = false;
    private CopyOnWriteArrayList<SiterDispatcherListener> mDispatcherListeners;

    public static Dispatcher getInstance() {
        if (instance == null) {
            synchronized (Dispatcher.class) {
                if (instance == null) {
                    instance = new Dispatcher();
                }
            }
        }
        return instance;
    }

    private Dispatcher() {
        this.mDelivery = new CallbackDelivery();
        this.mCallBackQueue = new CopyOnWriteArrayList<>();
        this.mDispatcherListeners = new CopyOnWriteArrayList<>();
        this.mTimerHandler = new Handler(SiterSDK.getContext().getMainLooper());
        ServiceMonitor.getInstance().registerMsgObserver(this);
    }

    @Override
    public void start() {
        isStarted = true;
        startTimer();
        startDelivery(SiterSDK.getContext());
    }

    @Override
    void stop() {
        isStarted = false;
        mCallBackQueue.clear();
        mTimerHandler.removeCallbacksAndMessages(null);
        ServiceMonitor.getInstance().unrighsterMsgObserver(this);
        stopTimer();
        stopDelivery();
    }

    @Override
    public void addFilter(IMessageFilter filter, SiterMsgCallback callback, long expired) {
        FilterNet filterNet = new FilterNet(filter, callback, expired);
        mCallBackQueue.add(filterNet);
    }

    @Override
    public void addFilter(IMessageFilter filter, SiterMsgCallback callback) {
        FilterNet filterNet = new FilterNet(filter, callback);
        mCallBackQueue.add(filterNet);
    }

    @Override
    public void addFilter(String tag, IMessageFilter filter, SiterMsgCallback callback) {
        FilterNet filterNet = new FilterNet(filter, callback);
        mCallBackQueue.add(filterNet);
    }

    @Override
    public void addFilter(IMessageFilter filter, SiterMsgCallback callback, FilterType type, long expried) {
        FilterNet filterNet = new FilterNet(filter, callback, type, expried);
        mCallBackQueue.add(filterNet);
    }

    @Override
    public void removeFilter(String tag) {
        remove(tag);
    }

    @Override
    public void removeFilter(IMessageFilter filter) {
        remove(filter);
    }

    @Override
    public void removeAllFilters() {
        mCallBackQueue.clear();
    }

    @Override
    public int getFilterSize() {
        return mCallBackQueue.size();
    }

    @Override
    public void onReceived(String message, String from) {
        if (!TextUtils.isEmpty(message)) {
            for (SiterDispatcherListener listener : mDispatcherListeners) {
                mDelivery.postReceive(message, from, listener);
            }
        }
        Iterator<FilterNet> iterator = mCallBackQueue.iterator();
        while (iterator.hasNext()) {
            FilterNet filterNet = iterator.next();
            int result = filterNet.match(message);
            if (result == FilterNet.MATCH_MATCH) {
                try {
                    JSONObject jsonObject = new JSONObject(message);

                    int code = jsonObject.optInt("code", Integer.MIN_VALUE);
                    if (code == 200 || code == Integer.MIN_VALUE) {
                        mDelivery.PostSuccess(message, filterNet.getCallback());
                    } else {
                        String desc = jsonObject.optString("desc");
                        mDelivery.postError(code, message, filterNet.getCallback());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mDelivery.postError(AndroidErrorMap.ERROR_MESSAGE_FORMAT_ERROR,
                            AndroidErrorMap.errMap.get(AndroidErrorMap.ERROR_MESSAGE_FORMAT_ERROR),
                            filterNet.getCallback());
                }
                if (filterNet.getType() == FilterType.FILTER_ONCE) {
                    mCallBackQueue.remove(filterNet);
                }
            } else if (result == FilterNet.MATCH_TIMEOUT) {
                mDelivery.postTimeout(filterNet.getCallback());
                mCallBackQueue.remove(filterNet);
            } else if (result == FilterNet.MATCH_CANCEL || result == FilterNet.MATCH_NONE) {
                mCallBackQueue.remove(filterNet);
            }
        }
    }

    @Override
    public void enqueue(IMessageRequest request, FilterType type) {
        SiterConnectionService service = ServiceBinder.getInstance().getService();
        if (service != null) {
            FilterNet filterNet = new FilterNet(request.getFilter(), request.getMsgCallback(), type);
            filterNet.setRequest(request);
            add(filterNet);
            if (!TextUtils.isEmpty(request.getMessage())) {
                for (SiterDispatcherListener listener : mDispatcherListeners) {
                    mDelivery.postSend(request.getMessage(), request.getHandler(), listener);
                }
            }
            if (request.getChannel() == IMessageRequest.CHANNEL_CLOUD) {
                service.sendToCloud(request.getHandler(), request.getMessage());
            } else if (request.getChannel() == IMessageRequest.CHANNEL_DEVICE) {
                service.sendToDevice(request.getHandler(), request.getMessage());
            }
        }
    }

    @Override
    public void enqueue(IMessageRequest request, String ip, int port, FilterType type) {
        SiterConnectionService service = ServiceBinder.getInstance().getService();
        if (service != null) {
            FilterNet filterNet = new FilterNet(request.getFilter(), request.getMsgCallback(), type);
            filterNet.setRequest(request);
            add(filterNet);
            if (!TextUtils.isEmpty(request.getMessage())) {
                for (SiterDispatcherListener listener : mDispatcherListeners) {
                    mDelivery.postSend(request.getMessage(), request.getHandler(), listener);
                }
            }
            if (request.getChannel() == IMessageRequest.CHANNEL_COMMON_UDP) {
                service.sendCommonUdp(request.getMessage(), ip, port);
            } else {
                LogUtil.d(TAG, "Wrong channel");
            }
        }
    }

    @Override
    public void enqueue(IMessageRequest request, FilterType type, long expired) {
        SiterConnectionService service = ServiceBinder.getInstance().getService();
        if (service != null) {
            FilterNet filterNet = new FilterNet(request.getFilter(), request.getMsgCallback(), type, expired);
            filterNet.setRequest(request);
            if (request.getChannel() == IMessageRequest.CHANNEL_CLOUD && service.cloudConnExist(request.getHandler())) {
                if (!TextUtils.isEmpty(request.getMessage())) {
                    for (SiterDispatcherListener listener : mDispatcherListeners) {
                        mDelivery.postSend(request.getMessage(), request.getHandler(), listener);
                    }
                }
                service.sendToCloud(request.getHandler(), request.getMessage());
                add(filterNet);
            } else if (request.getChannel() == IMessageRequest.CHANNEL_DEVICE && service.deviceConnExist(request.getHandler())) {
                if (!TextUtils.isEmpty(request.getMessage())) {
                    for (SiterDispatcherListener listener : mDispatcherListeners) {
                        mDelivery.postSend(request.getMessage(), request.getHandler(), listener);
                    }
                }
                service.sendToDevice(request.getHandler(), request.getMessage());
                add(filterNet);
            }
        }
    }

    @Override
    public void addDispatcherListener(SiterDispatcherListener listener) {
        mDispatcherListeners.add(listener);
    }

    @Override
    public void removeDispatcherListener(SiterDispatcherListener listener) {
        mDispatcherListeners.remove(listener);
    }

    private void startDelivery(Context context) {
        mDelivery.start(context);
    }

    private void stopDelivery() {
        mDelivery.stop();
    }

    private void add(FilterNet filterNet) {
        mCallBackQueue.add(filterNet);
    }

    public void remove(String tag) {
        Iterator<FilterNet> iterator = mCallBackQueue.iterator();
        while (iterator.hasNext()) {
            FilterNet filterNet = iterator.next();
            if (TextUtils.equals(tag, filterNet.getTag())) {
                mCallBackQueue.remove(filterNet);
            }
        }
    }

    public void remove(IMessageFilter filter) {
        Iterator<FilterNet> iterator = mCallBackQueue.iterator();
        while (iterator.hasNext()) {
            FilterNet filterNet = iterator.next();
            if (filterNet.getFilter() == filter) {
                mCallBackQueue.remove(filterNet);
            }
        }
    }

    private void startTimer() {
        if (isStarted) {
            mTimerHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkTimeout();
                    startTimer();
                }
            }, CHECK_INTERVAL);
        }
    }

    private void stopTimer() {
        isStarted = false;
    }

    private void checkTimeout() {
        // LogUtil.d(TAG, "Check filter timeout. Queue size: " + mCallBackQueue.size());
        Iterator<FilterNet> iterator = mCallBackQueue.iterator();
        while (iterator.hasNext()) {
            FilterNet filterNet = iterator.next();
            if (filterNet.checkHasCanceled()) {
                LogUtil.e(TAG, "Request canceled: " + filterNet.getFilter().toString());
                mCallBackQueue.remove(filterNet);
            } else if (filterNet.checkIsTimeout()) {
                if (filterNet.getCallback() != null) {
                    LogUtil.e(TAG, "Filter timeout: " + filterNet.getFilter().toString());
                    mDelivery.postTimeout(filterNet.getCallback());
                    // TODO: 2017/3/22 在CopyOnWriteArrayList迭代器中执行删除操作，需要验证是否可行
                }
                mCallBackQueue.remove(filterNet);
            }
        }
    }
}
