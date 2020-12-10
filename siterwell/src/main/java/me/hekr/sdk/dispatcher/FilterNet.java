package me.hekr.sdk.dispatcher;

import me.hekr.sdk.FilterType;
import me.hekr.sdk.IMessageRequest;
import me.hekr.sdk.inter.HekrMsgCallback;

/**
 * Created by hucn on 2017/3/21.
 * Author: hucn
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
    private HekrMsgCallback mMsgCallback;

    // 如果有的话，持有IMessageRequest的引用
    private IMessageRequest mRequest;

    FilterNet(IMessageFilter filter, HekrMsgCallback callback) {
        this(filter, callback, FilterType.FILTER_PERMANANT, DEFAULT_EXPIRED_TIME);
    }

    FilterNet(IMessageFilter filter, HekrMsgCallback callback, long expired) {
        this(filter, callback, FilterType.FILTER_TEMPARORY, expired);
    }

    FilterNet(IMessageFilter filter, HekrMsgCallback callback, FilterType type) {
        this(filter, callback, type, DEFAULT_EXPIRED_TIME);
    }

    FilterNet(IMessageFilter filter, HekrMsgCallback callback, FilterType type, long expired) {
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
        HekrMsgCallback callback = mMsgCallback;
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

    public HekrMsgCallback getCallback() {
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
