package com.siterwell.sdk.http.bean;

import java.io.Serializable;

/*
@class ConfigBean
@autor Administrator
@time 2017/10/16 13:41
@email xuejunju_4595@qq.com
*/
public class ConfigBean implements Serializable {
        private static final long serialVersionUID = 7870767545623191490L;
        private String AppId;
        private String AppKey;
        private String AppSecurit;

        public ConfigBean() {
        }

        public String getAppId() {
            return AppId;
        }

        public void setAppId(String AppId) {
            this.AppId = AppId;
        }

        public String getAppKey() {
            return AppKey;
        }

        public void setAppKey(String AppKey) {
            this.AppKey = AppKey;
        }

        public String getAppSecurit() {
            return AppSecurit;
        }

        public void setAppSecurit(String AppSecurit) {
            this.AppSecurit = AppSecurit;
        }

        @Override
        public String toString() {
            return "ConfigBean{" +
                    "AppId='" + AppId + '\'' +
                    ", AppKey='" + AppKey + '\'' +
                    ", AppSecurit='" + AppSecurit + '\'' +
                    '}';
        }

}
