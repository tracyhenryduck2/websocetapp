package com.siterwell.demo.folder;

/**
 * Created by gc-0001 on 2017/4/28.
 */

public class FolderPojo {
    public String folderId;
    private static FolderPojo instance = null;
    private FolderPojo (){

    }
    public static FolderPojo getInstance(){
        if (instance == null) {
//            synchronized (ConnectionPojo.class) {
//                if (instance == null) {
            return instance = new FolderPojo();
//                }
//            }
        }
        return instance;
    }

}
