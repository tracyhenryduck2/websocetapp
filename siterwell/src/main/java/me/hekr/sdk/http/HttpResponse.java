package me.hekr.sdk.http;

import java.util.Map;

/**
 * Created by hucn on 2017/3/17.
 * Author: hucn
 * Description: http response
 */

public interface HttpResponse {

  void onSuccess(int code, Map<String, String> headers, byte[] bytes);

  void onError(int code, Map<String, String> headers, byte[] bytes);
}
