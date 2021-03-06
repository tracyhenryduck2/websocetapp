package me.siter.sdk.httpCore;

import java.util.Map;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Author: 基本的返回信息
 * Description:
 */

public class BaseHttpResponse {

    public int code;
    public byte[] data;
    public Map<String, String> headers;
    public long networkCost;

    public BaseHttpResponse(int code, byte[] data, Map<String, String> headers, long networkCost) {
        this.code = code;
        this.data = data;
        this.headers = headers;
        this.networkCost = networkCost;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public long getNetworkCost() {
        return networkCost;
    }

    public void setNetworkCost(long networkCost) {
        this.networkCost = networkCost;
    }
}
