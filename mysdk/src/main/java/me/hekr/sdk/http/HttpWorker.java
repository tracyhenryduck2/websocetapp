package me.hekr.sdk.http;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import me.hekr.sdk.utils.AndroidErrorMap;
import me.hekr.sdk.utils.LogUtil;

/**
 * Created by hucn on 2017/3/17.
 * Author: hucn
 * Description: 子线程，处理http网络请求
 */

class HttpWorker extends Thread {

    private static final String TAG = HttpWorker.class.getSimpleName();

    private BlockingQueue<HttpRequest> mQueue;
    private URLClient mURLClient;
    private HttpBackPost mBackPost;

    HttpWorker(BlockingQueue<HttpRequest> queue, URLClient urlClient, HttpBackPost httpBackPost) {
        this.mQueue = queue;
        this.mURLClient = urlClient;
        this.mBackPost = httpBackPost;
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                HttpRequest request = mQueue.take();
                if (request.isCanceled()) {
                    LogUtil.d(TAG, "Request is canceled");
                    continue;
                }
                HttpResponse response = request.getResponse();
                try {
                    // TODO: 2017/3/17 完成请求
                    BaseHttpResponse baseResponse = mURLClient.doRequest(request);
                    if (baseResponse.getCode() >= 300) {
                        if (!request.isCanceled()) {
                            mBackPost.postFail(response, baseResponse);
                        } else {
                            LogUtil.d(TAG, "Request is canceled");
                        }
                    } else {
                        if (!request.isCanceled()) {
                            mBackPost.postSuccess(response, baseResponse);
                        } else {
                            LogUtil.d(TAG, "Request is canceled");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (!request.isCanceled()) {
                        mBackPost.postError(response, AndroidErrorMap.ERROR_HTTP_CONNECTION_ERROR);
                    } else {
                        LogUtil.d(TAG, "Request is canceled");
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
