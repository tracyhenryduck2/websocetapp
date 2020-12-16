package me.hekr.sdk.service;

import android.os.Binder;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: ConnectionService中的binder
 */

class ConnectionBinder extends Binder {

    private HekrConnectionService mHekrConnectionService;

    ConnectionBinder(HekrConnectionService service) {
        this.mHekrConnectionService = service;
    }

    public HekrConnectionService getService() {
        return mHekrConnectionService;
    }
}
