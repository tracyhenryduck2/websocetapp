package me.siter.sdk.httpCore;

import org.json.JSONObject;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: get请求
 */

public class GetRequest extends HttpRequest {

    public GetRequest(String url, HttpResponse response) {
        this.url = url;
        this.response = response;
    }

    @Override
    HttpMethod getMethod() {
        return HttpMethod.GET;
    }

    @Override
    JSONObject getObject() {
        return null;
    }
}
