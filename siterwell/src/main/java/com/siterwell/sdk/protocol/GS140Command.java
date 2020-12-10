package com.siterwell.sdk.protocol;

/**
 * Created by TracyHenry on 2018/4/25.
 */

public enum GS140Command {

        // 利用构造函数传参
        SILENCE (1),
        SET_SMOKE_SENSOR_SITERWELL (2),
        SET_SMOKE_SENSOR_OTHER(3);

        // 定义私有变量
        private int nCode ;

        // 构造函数，枚举类型只能为私有
        private GS140Command(int _nCode) {
            this . nCode = _nCode;
        }


        public int getnCode() {
            return nCode;
        }
}
