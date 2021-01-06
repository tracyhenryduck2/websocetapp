package me.siter.sdk.http.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/10/16.
 */

public class FolderBean implements Serializable {
    private static final long serialVersionUID = 3002689413752436474L;
    /**
     * folderId : xxxx
     * folderName : {folderName}
     * devTidList : []
     */

    private String folderId;
    private String folderName;
    private List<?> devTidList;

    public FolderBean() {
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

    public List<?> getDevTidList() {
        return devTidList;
    }

    public void setDevTidList(List<?> devTidList) {
        this.devTidList = devTidList;
    }
}
