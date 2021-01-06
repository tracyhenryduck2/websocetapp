package me.siter.sdk.http;


import me.siter.sdk.http.bean.JWTBean;


/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description:
 */
public abstract class GetSiterDataWithTokenListener {

    public abstract void getDataSuccess(Object object);

    public abstract void getToken(JWTBean jwtBean);

    public abstract void getDataFail(int errorCode);

    public void getDataProgress(long bytesWritten, long totalSize) {

    }

}
