package me.siter.sdk.http.bean;

import java.io.Serializable;

/**
 * Created by TracyHenry on 2020/12/16.
 **/
public class GroupInfoBean implements Serializable {

    private static final long serialVersionUID = 2546761236620348393L;
    /**
     * groupId : gid1234
     * groupName : 联动小组001
     */

    private String groupId;
    private String groupName;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GroupInfoBean)) return false;

        GroupInfoBean that = (GroupInfoBean) o;

        if (!getGroupId().equals(that.getGroupId())) return false;
        return getGroupName().equals(that.getGroupName());

    }

    @Override
    public int hashCode() {
        int result = getGroupId().hashCode();
        result = 31 * result + getGroupName().hashCode();
        return result;
    }
}