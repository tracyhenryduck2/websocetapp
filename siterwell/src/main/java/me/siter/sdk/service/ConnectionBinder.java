package me.siter.sdk.service;

import android.os.Binder;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: ConnectionService中的binder
 */

class ConnectionBinder extends Binder {

    private SiterConnectionService mSiterConnectionService;

    ConnectionBinder(SiterConnectionService service) {
        this.mSiterConnectionService = service;
    }

    public SiterConnectionService getService() {
        return mSiterConnectionService;
    }
}
