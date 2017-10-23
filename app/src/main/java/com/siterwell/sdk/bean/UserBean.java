package com.siterwell.sdk.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/16.
 */
public class UserBean implements Serializable{
    private static final long serialVersionUID = 5582121410547071077L;
    /**
     * pid : 00000000000
     * password : 1qaz2wsx
     * username : 13021298993
     * clientType : ANDROID
     */

    private String pid;
    private String username;
    private String password;
    private String JWT_TOKEN;
    private String refresh_token;
    private String uid;

    public String getJWT_TOKEN() {
        return JWT_TOKEN;
    }

    public void setJWT_TOKEN(String JWT_TOKEN) {
        this.JWT_TOKEN = JWT_TOKEN;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserBean(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    public UserBean(String pid, String username, String password, String JWT_TOKEN, String refresh_token) {
        this.pid = pid;
        this.username = username;
        this.password = password;
        this.JWT_TOKEN = JWT_TOKEN;
        this.refresh_token = refresh_token;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "pid='" + pid + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", JWT_TOKEN='" + JWT_TOKEN + '\'' +
                ", refresh_token='" + refresh_token + '\'' +
                '}';
    }
}
