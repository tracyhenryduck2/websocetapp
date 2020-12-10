package com.siterwell.sdk.http.bean;

import java.io.Serializable;
import java.util.List;

/*
@class DeviceAndFolderBean
@autor Administrator
@time 2017/10/16 13:59
@email xuejunju_4595@qq.com
*/
public class DeviceAndFolderBean implements Serializable {
    private static final long serialVersionUID = 1718894768818681217L;
    private String folderId;
    private String folderName;
    private List<DeviceBean> lists;


    public DeviceAndFolderBean() {
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public List<DeviceBean> getLists() {
        return lists;
    }

    public void setLists(List<DeviceBean> lists) {
        this.lists = lists;
    }

    public DeviceAndFolderBean(String folderId, String folderName, List<DeviceBean> lists) {
        this.folderId = folderId;
        this.folderName = folderName;
        this.lists = lists;
    }

    @Override
    public String toString() {
        return "DeviceAndFolderBean{" +
                "folderId=" + folderId +
                ", folderName='" + folderName + '\'' +
                ", lists=" + lists +
                '}';
    }
}