package me.siter.sdk.http;


/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
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
