package me.hekr.sdk.service;

import android.os.Binder;

/**
 * Created by hucn on 2017/3/20.
 * Author: hucn
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
