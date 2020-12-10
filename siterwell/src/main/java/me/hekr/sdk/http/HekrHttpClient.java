package me.hekr.sdk.http;


/**
 * Created by hucn on 2017/3/17.
 * Author: hucn
 * Description: http 网络请求的实现
 */

public class HekrHttpClient implements IHttpClient {

    private IHttpClient mHttpClient;

    public HekrHttpClient(IHttpClient httpClient) {
        this.mHttpClient = httpClient;
    }

    @Override
    public void start() {
        mHttpClient.start();
    }

    @Override
    public void stop() {
        mHttpClient.stop();
    }

    @Override
    public void add(HttpRequest request) {
        mHttpClient.add(request);
    }
}
