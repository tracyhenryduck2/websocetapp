package me.hekr.sdk.http;

import android.content.Context;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by hucn on 2017/3/17.
 * Author: hucn
 * Description: IHttpClient 的默认实现
 */

public class HekrDefautHttpClient implements IHttpClient {

    private static final int THREAD_SIZE = 4;
    private static final int QUEUE_SIZE = 500;

    // 消息队列
    private ArrayBlockingQueue<HttpRequest> mMessageQueue;
    // 消息回调
    private HttpBackPost mHttpBackPost;
    // 消息线程
    private ExecutorService mExecutor;
    // 请求类
    private URLClient mURLClient;

    public HekrDefautHttpClient(Context context) {
        mMessageQueue = new ArrayBlockingQueue<>(QUEUE_SIZE);
        mHttpBackPost = new HttpBackPost(context);
        mExecutor = Executors.newFixedThreadPool(THREAD_SIZE);
        mURLClient = new URLClient();
    }

    @Override
    public void start() {
        for (int i = 0; i < THREAD_SIZE; i++) {
            HttpWorker worker = new HttpWorker(mMessageQueue, mURLClient, mHttpBackPost);
            mExecutor.submit(worker);
        }
    }

    @Override
    public void stop() {
        mMessageQueue.clear();
        mExecutor.shutdown();
    }

    @Override
    public void add(HttpRequest request) {
        try {
            mMessageQueue.add(request);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
}
