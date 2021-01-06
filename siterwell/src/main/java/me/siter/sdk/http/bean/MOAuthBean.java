package me.siter.sdk.http.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/16.
 */

public class MOAuthBean implements Serializable {

    private static final long serialVersionUID = 4490816541354661124L;
    /**
     * uid : null
     * pid : 01698862200
     * oAuthType : ANDROID
     * bindToken : qazxswedcvfrtgbnhyujmkilop
     * unionId : ***
     * nick :
     * avatar : http://xxx.xxx.xxx/xxxx.png
     */

    private Object uid;
    private String pid;
    private String oAuthType;
    private String bindToken;
    private String unionId;
    private String nick;
    private String avatar;

    public MOAuthBean() {
    }

    public Object getUid() {
        return uid;
    }

    public void setUid(Object uid) {
        this.uid = uid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getOAuthType() {
        return oAuthType;
    }

    public void setOAuthType(String oAuthType) {
        this.oAuthType = oAuthType;
    }

    public String getBindToken() {
        return bindToken;
    }

    public void setBindToken(String bindToken) {
        this.bindToken = bindToken;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public MOAuthBean(Object uid, String pid, String oAuthType, String bindToken, String unionId, String nick, String avatar) {
        this.uid = uid;
        this.pid = pid;
        this.oAuthType = oAuthType;
        this.bindToken = bindToken;
        this.unionId = unionId;
        this.nick = nick;
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "MOAuthBean{" +
                "uid=" + uid +
                ", pid='" + pid + '\'' +
                ", oAuthType='" + oAuthType + '\'' +
                ", bindToken='" + bindToken + '\'' +
                ", unionId='" + unionId + '\'' +
                ", nick='" + nick + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}
