package me.hekr.sdk;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: 连接状态
 */

public class CloudChannelStatus {

    private String url;

    private ChannelStatusType type;

    public CloudChannelStatus(String url, ChannelStatusType type) {
        this.url = url;
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ChannelStatusType getType() {
        return type;
    }

    public void setType(ChannelStatusType type) {
        this.type = type;
    }
}
