package me.siter.sdk.http;


import android.os.SystemClock;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.siter.sdk.utils.LanguageUtil;
import me.siter.sdk.utils.HttpUtil;
import me.siter.sdk.utils.LogUtil;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: 通过HttpURLConnection封装的请求类
 */

class URLClient {

    private static final String TAG = URLClient.class.getSimpleName();

    private static final int CONNECTION_TIMEOUT = 15000;
    private static final int READ_TIMEOUT = 15000;

    BaseHttpResponse doRequest(HttpRequest request) throws IOException {
        // TODO: 2017/3/17 使用HttpURLConnection完成网络请求
        long requestStart = SystemClock.elapsedRealtime();
        String urlStr = request.getUrl();
        URL url = new URL(urlStr);
        LogUtil.d(TAG, "Url: " + url);
        HttpURLConnection connection = getUrlConnection(url);
        Map<String, String> headers = request.getHeaders();
        if (headers != null) {
            for (String headerName : headers.keySet()) {
                connection.addRequestProperty(headerName, headers.get(headerName));
            }
            // 添加语言环境
            if (!headers.containsKey("Accept-Language")) {
                String language = LanguageUtil.getAcceptLnaguage();
                if (!TextUtils.isEmpty(language)) {
                    connection.addRequestProperty("Accept-Language", language);
                }
            }
            // User-Agent
            if (!headers.containsKey("User-Agent")) {
                String agent = HttpUtil.getUserAgent();
                if (!TextUtils.isEmpty(agent)) {
                    connection.addRequestProperty("User-Agent", agent);
                }
            }
        }
        String requestHeader = getReqeustHeader(connection);
        LogUtil.d(TAG, "RequestHeader: " + requestHeader);
        if (request.getMethod() == HttpMethod.GET) {
            doGet(connection);
        } else if (request.getMethod() == HttpMethod.POST) {
            doPost(connection, request);
        }
        int responseCode = connection.getResponseCode();
        LogUtil.d(TAG, "ResponseCode: " + responseCode);
        InputStream is;
        if (responseCode >= 300) {
            is = connection.getErrorStream();
        } else {
            is = connection.getInputStream();
        }
        Map<String, String> responseHeader = getResponseHeader(connection);
        LogUtil.d(TAG, "ResponseHeader: " + responseHeader.toString());
        byte[] responseBody = getBytesByInputStream(is);
        LogUtil.d(TAG, "ResponseBody: " + new String(responseBody));
        long networkCost = SystemClock.elapsedRealtime() - requestStart;
        return new BaseHttpResponse(responseCode, responseBody, responseHeader, networkCost);
    }

    private HttpURLConnection getUrlConnection(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(CONNECTION_TIMEOUT);
        connection.setReadTimeout(READ_TIMEOUT);
        connection.setUseCaches(false);
        connection.setDoInput(true);
        return connection;
    }

    private void doGet(HttpURLConnection connection) throws ProtocolException {
        connection.setRequestMethod("GET");
    }

    private void doPost(HttpURLConnection connection, HttpRequest request) throws IOException {
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        Object object = request.getObject();
        if (object != null) {
            String content = object.toString();
            LogUtil.d(TAG, "RequestContent: " + content);
            out.write(content.getBytes());
            out.close();
        }
    }

    private String getReqeustHeader(HttpURLConnection conn) {
        // https://github.com/square/okhttp/blob/master/okhttp-urlconnection/src/main/java/okhttp3/internal/huc/HttpURLConnectionImpl.java#L236
        Map<String, List<String>> requestHeaderMap = conn.getRequestProperties();
        Iterator<String> requestHeaderIterator = requestHeaderMap.keySet().iterator();
        StringBuilder sbRequestHeader = new StringBuilder();
        while (requestHeaderIterator.hasNext()) {
            String requestHeaderKey = requestHeaderIterator.next();
            String requestHeaderValue = conn.getRequestProperty(requestHeaderKey);
            sbRequestHeader.append(requestHeaderKey);
            sbRequestHeader.append(":");
            sbRequestHeader.append(requestHeaderValue);
            sbRequestHeader.append("\n");
        }
        return sbRequestHeader.toString();
    }

    private Map<String, String> getResponseHeader(HttpURLConnection conn) {
        Map<String, List<String>> responseHeaderMap = conn.getHeaderFields();
        int size = responseHeaderMap.size();
        Map<String, String> headers = new HashMap<>();
        for (int i = 0; i < size; i++) {
            String responseHeaderKey = conn.getHeaderFieldKey(i);
            String responseHeaderValue = conn.getHeaderField(i);
            headers.put(responseHeaderKey, responseHeaderValue);
        }
        return headers;
    }

    private byte[] getBytesByInputStream(InputStream is) {
        byte[] bytes = null;
        BufferedInputStream bis = new BufferedInputStream(is);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(baos);
        byte[] buffer = new byte[1024 * 8];
        int length;
        try {
            while ((length = bis.read(buffer)) > 0) {
                bos.write(buffer, 0, length);
            }
            bos.flush();
            bytes = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bytes;
    }
}
