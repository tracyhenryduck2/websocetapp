package com.siterwell.demo.listener;

/**
 * Created by TracyHenry on 2018/4/27.
 */

public interface UpgradeListener {

    void progressComplete(String devTid);

    void progressIng(String devTid, int progress);

}
