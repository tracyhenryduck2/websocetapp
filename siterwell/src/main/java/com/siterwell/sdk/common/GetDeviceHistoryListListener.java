package com.siterwell.sdk.common;

import com.siterwell.sdk.bean.WarningHistoryBean;

import java.util.List;

/**
 * Created by TracyHenry on 2018/2/9.
 */

public interface GetDeviceHistoryListListener {

    void successHistoryList(List<WarningHistoryBean> warningHistoryBeanList);

    void fail(int errcode);

}
