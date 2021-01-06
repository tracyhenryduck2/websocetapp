package me.siter.sdk.httpCore;

import java.util.Map;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: http response
 */

public interface HttpResponse {

  void onSuccess(int code, Map<String, String> headers, byte[] bytes);

  void onError(int code, Map<String, String> headers, byte[] bytes);
}
