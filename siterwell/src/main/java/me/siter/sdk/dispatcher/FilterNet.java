package me.siter.sdk.dispatcher;

import me.siter.sdk.FilterType;
import me.siter.sdk.IMessageRequest;
import me.siter.sdk.inter.SiterMsgCallback;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: Dispatcher中过滤器的封装
 */

class FilterNet {

    private static final String TAG = FilterNet.class.getSimpleName();

    private static final long DEFAULT_EXPIRED_TIME = 6 * 1000;

    static final int MATCH_MATCH = 1;
    static final int MATCH_NOT_MATCH = 2;
    static final int MATCH_TIMEOUT = 3;
    static final int MATCH_NONE = 4;
    static final int MATCH_CANCEL = 5;

    private FilterType mType;
    private long mTimestamp;
    private long mExpired = DEFAULT_EXPIRED_TIME;
    private String mTag;

    private IMessageFilter mFilter;
    private SiterMsgCallback mMsgCallback;

    // 如果有的话，持有IMessageRequest的引用
    private IMessageRequest mRequest;

    FilterNet(IMessageFilter filter, SiterMsgCallback callback) {
        this(filter, callback, FilterType.FILTER_PERMANANT, DEFAULT_EXPIRED_TIME);
    }

    FilterNet(IMessageFilter filter, SiterMsgCallback callback, long expired) {
        this(filter, callback, FilterType.FILTER_TEMPARORY, expired);
    }

    FilterNet(IMessageFilter filter, SiterMsgCallback callback, FilterType type) {
        this(filter, callback, type, DEFAULT_EXPIRED_TIME);
    }

    FilterNet(IMessageFilter filter, SiterMsgCallback callback, FilterType type, long expired) {
        this.mFilter = filter;
        this.mMsgCallback = callback;
        this.mType = type;
        this.mExpired = expired;
        this.mTimestamp = System.currentTimeMillis();
    }

    int match(String message) {
        if (mRequest != null && mRequest.hasCanceled()) {
            return MATCH_CANCEL;
        }
        SiterMsgCallback callback = mMsgCallback;
        if (callback != null) {
            if (checkIsTimeout()) {
                return MATCH_TIMEOUT;
            }
            if (mFilter.doFilter(message)) {
                return MATCH_MATCH;
            } else {
                return MATCH_NOT_MATCH;
            }
        }
        return MATCH_NONE;
    }

    boolean checkIsTimeout() {
        if (mType == FilterType.FILTER_TEMPARORY || mType == FilterType.FILTER_ONCE) {
            if ((System.currentTimeMillis() - mTimestamp) >= mExpired) {
                return true;
            }
        }
        return false;
    }

    public SiterMsgCallback getCallback() {
        return mMsgCallback;
    }

    public String getTag() {
        return mTag;
    }

    public void setTag(String tag) {
        this.mTag = tag;
    }

    public FilterType getType() {
        return mType;
    }

    public void setType(FilterType type) {
        this.mType = type;
    }

    public IMessageFilter getFilter() {
        return mFilter;
    }

    public IMessageRequest getRequest() {
        return mRequest;
    }

    public void setRequest(IMessageRequest mRequest) {
        this.mRequest = mRequest;
    }

    public boolean checkHasCanceled() {
        return mRequest != null && mRequest.hasCanceled();
    }
}
