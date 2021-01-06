package me.siter.sdk.http;


import me.siter.sdk.http.bean.JWTBean;

/*
@class GetSiterDataWithTokenListener
@autor Administrator
@time 2017/10/16 14:26
@email xuejunju_4595@qq.com
*/
public abstract class GetSiterDataWithTokenListener {

    public abstract void getDataSuccess(Object object);

    public abstract void getToken(JWTBean jwtBean);

    public abstract void getDataFail(int errorCode);

    public void getDataProgress(long bytesWritten, long totalSize) {

    }

}
