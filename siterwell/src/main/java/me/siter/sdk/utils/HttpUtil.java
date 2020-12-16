package me.siter.sdk.utils;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description:
 */

public class HttpUtil {

    /**
     * 获取User-Agent
     */
    public static String getUserAgent() {
        String userAgent = null;
        try {
            userAgent = System.getProperty("http.agent");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (userAgent == null) {
            return "Android-Agent";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0, length = userAgent.length(); i < length; i++) {
            char c = userAgent.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
