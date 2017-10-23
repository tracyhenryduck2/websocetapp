package com.siterwell.sdk.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/10/16.
 */

public class FolderListBean implements Serializable {
    private static final long serialVersionUID = 8085810133047691951L;
    /**
     * folderId : xxxx
     * folderName : xxx
     * devTidList : ["xxx","xxx"]
     */

    private String folderId;
    private String folderName;
    private List<String> devTidList;

    public FolderListBean() {
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

    public List<String> getDevTidList() {
        return devTidList;
    }

    public void setDevTidList(List<String> devTidList) {
        this.devTidList = devTidList;
    }

    public FolderListBean(String folderId, String folderName, List<String> devTidList) {
        this.folderId = folderId;
        this.folderName = folderName;
        this.devTidList = devTidList;
    }

    @Override
    public String toString() {
        return "FolderListBean{" +
                "folderId='" + folderId + '\'' +
                ", folderName='" + folderName + '\'' +
                ", devTidList=" + devTidList +
                '}';
    }
}
