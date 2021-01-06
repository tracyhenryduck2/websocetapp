package me.siter.sdk.http.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/16.
 */

public class FileBean implements Serializable {

    private static final long serialVersionUID = 4627200370571696911L;
    /**
     * fileOriginName : cdraw.png
     * fileName : ufile-1538871986600000000000-3ba434398bbde210435afba88f64e908.png
     * uploadTime : 1458031660384
     * md5 : 3ba434398bbde210435afba88f64e908
     */

    private String fileOriginName;
    private String fileName;
    private String fileSourceUrl;
    private String fileCDNUrl;
    private long uploadTime;
    private String md5;

    public FileBean() {
    }

    public String getFileOriginName() {
        return fileOriginName;
    }

    public void setFileOriginName(String fileOriginName) {
        this.fileOriginName = fileOriginName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileSourceUrl() {
        return fileSourceUrl;
    }

    public void setFileSourceUrl(String fileSourceUrl) {
        this.fileSourceUrl = fileSourceUrl;
    }

    public String getFileCDNUrl() {
        return fileCDNUrl;
    }

    public void setFileCDNUrl(String fileCDNUrl) {
        this.fileCDNUrl = fileCDNUrl;
    }

    public long getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(long uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    @Override
    public String toString() {
        return "FileBean{" +
                "fileOriginName='" + fileOriginName + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileSourceUrl='" + fileSourceUrl + '\'' +
                ", fileCDNUrl='" + fileCDNUrl + '\'' +
                ", uploadTime=" + uploadTime +
                ", md5='" + md5 + '\'' +
                '}';
    }

}
