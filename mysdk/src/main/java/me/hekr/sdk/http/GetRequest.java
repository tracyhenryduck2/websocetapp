package me.hekr.sdk.http;

import org.json.JSONObject;

/**
 * Created by hucn on 2017/3/27.
 * Author: hucn
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
