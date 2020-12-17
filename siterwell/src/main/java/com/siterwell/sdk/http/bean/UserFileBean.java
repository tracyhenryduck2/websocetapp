package com.siterwell.sdk.http.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/10/16.
 */

public class UserFileBean implements Serializable {

    private static final long serialVersionUID = -7429421394027734482L;


    /**
     * totalPages : 5
     * totalElements : 84
     * sort : null
     * numberOfElements : 20
     * first : true
     * size : 20
     * number : 0
     */

    private boolean last;
    private int totalPages;
    private int totalElements;
    private int numberOfElements;
    private boolean first;
    private int size;
    private int number;
    /**
     * fileOriginName : 1464695782565.png
     * fileName : ufile-3492069738800000000000-e1d34f8271b5158465bfe1a336588f3c.png
     * uploadTime : 1464695779910
     * md5 : e1d34f8271b5158465bfe1a336588f3c
     */

    private List<ContentBean> content;

    public UserFileBean() {
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public List<ContentBean> getContent() {
        return content;
    }

    public void setContent(List<ContentBean> content) {
        this.content = content;
    }

    public static class ContentBean {
        private String fileOriginName;
        private String fileName;
        private String fileSourceUrl;
        private String fileCDNUrl;
        private long uploadTime;
        private String md5;

        public ContentBean() {
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
            return "ContentBean{" +
                    "fileOriginName='" + fileOriginName + '\'' +
                    ", fileName='" + fileName + '\'' +
                    ", fileSourceUrl='" + fileSourceUrl + '\'' +
                    ", fileCDNUrl='" + fileCDNUrl + '\'' +
                    ", uploadTime=" + uploadTime +
                    ", md5='" + md5 + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "UserFileBean{" +
                "content=" + content +
                '}';
    }

}
