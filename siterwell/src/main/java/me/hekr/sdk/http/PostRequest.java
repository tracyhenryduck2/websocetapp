package me.hekr.sdk.http;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: post请求
 */

public class PostRequest extends HttpRequest {

    public PostRequest(String url, Object object, HttpResponse response) {
        this.url = url;
        this.Object = object;
        this.response = response;
    }

    @Override
    HttpMethod getMethod() {
        return HttpMethod.POST;
    }

    @Override
    Object getObject() {
        return Object;
    }
}
