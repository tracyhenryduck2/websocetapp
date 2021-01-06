package com.siterwell.demo.folder.bean;

import me.siter.sdk.http.bean.FolderListBean;

import java.io.Serializable;



/**
 * Created by ST-020111 on 2017/4/14.
 */

public class LocalFolderBean extends FolderListBean implements Serializable{
    //图片相对地址
    private String image;
    private boolean isselect;

    public boolean Isselect() {
        return isselect;
    }

    public void setSelect(boolean isselect) {
        this.isselect = isselect;
    }
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
