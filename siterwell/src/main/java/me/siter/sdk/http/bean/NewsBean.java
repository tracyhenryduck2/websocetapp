package me.siter.sdk.http.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/10/16.
 */

public class NewsBean implements Serializable {

    private static final long serialVersionUID = 8151360568426614016L;


    private int page;
    private int size;
    private int totalResults;
    private int totalPages;
    private boolean first;
    private boolean last;

    private List<Result> result;

    public NewsBean() {
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }

    public static class Result implements Serializable {
        private static final long serialVersionUID = 7323986903568770322L;
        private String id;
        private String authorName;
        private long updateTime;
        private String title;
        private String infoContent;
        private List<String> infoTags;

        public Result() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAuthorName() {
            return authorName;
        }

        public void setAuthorName(String authorName) {
            this.authorName = authorName;
        }

        public long getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(long updateTime) {
            this.updateTime = updateTime;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getInfoContent() {
            return infoContent;
        }

        public void setInfoContent(String infoContent) {
            this.infoContent = infoContent;
        }

        public List<String> getInfoTags() {
            return infoTags;
        }

        public void setInfoTags(List<String> infoTags) {
            this.infoTags = infoTags;
        }

        @Override
        public String toString() {
            return "Result{" +
                    "id='" + id + '\'' +
                    ", authorName='" + authorName + '\'' +
                    ", updateTime=" + updateTime +
                    ", title='" + title + '\'' +
                    ", infoContent='" + infoContent + '\'' +
                    ", infoTags=" + infoTags +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "NewsBean{" +
                "page=" + page +
                ", size=" + size +
                ", totalResults=" + totalResults +
                ", totalPages=" + totalPages +
                ", first=" + first +
                ", last=" + last +
                ", result=" + result +
                '}';
    }

}
