package com.siterwell.demo.device;


/**
 * Created by jishu0001 on 2016/12/20.
 */
public class Controller {
    public String deviceTid;
    public String ctrlKey;
    public String model;
    public boolean flag_service;
    private static Controller instance = null;
    private Controller(){

    }
    public static Controller getInstance(){
        if (instance == null) {
//            synchronized (ConnectionPojo.class) {
//                if (instance == null) {
            return instance = new Controller();
//                }
//            }
        }
        return instance;
    }
}