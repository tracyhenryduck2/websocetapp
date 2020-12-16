package me.siter.sdk.http;

import java.util.Map;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: http doRequest
 */

public abstract class HttpRequest {

    protected String url;
    protected Object Object;
    protected Map<String, String> headers;
    protected HttpResponse response;
    protected boolean canceled;

    abstract HttpMethod getMethod();

    abstract Object getObject();

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public HttpResponse getResponse() {
        return response;
    }

    public String getUrl() {
        return url;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }
}
