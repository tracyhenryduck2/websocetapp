package com.siterwell.sdk.http.bean;

import java.io.Serializable;
import java.util.List;

/*
@class GroupBean
@autor Administrator
@time 2017/10/16 13:51
@email xuejunju_4595@qq.com
*/
public class GroupBean implements Serializable {

    private static final long serialVersionUID = 2267956372236730839L;
    /**
     * groupId : afee215a898e4728b405ea091f045d4f
     * groupName : string
     * deviceList : [{"devTid":"VDEV_NodeJS_117","ctrlKey":"c8859ba320d54ee18b229b09b477e1de"}]
     * desc : 111
     * createTime : 1463221546055
     * updateTime : null
     * groupMid : 01e74de652f6
     */

    private String groupId;
    private String groupName;
    private String desc;
    private long createTime;
    private Object updateTime;
    private String groupMid;
    /**
     * devTid : VDEV_NodeJS_117
     * ctrlKey : c8859ba320d54ee18b229b09b477e1de
     */

    private List<DeviceLis> deviceList;

    public GroupBean() {
    }

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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public Object getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Object updateTime) {
        this.updateTime = updateTime;
    }

    public String getGroupMid() {
        return groupMid;
    }

    public void setGroupMid(String groupMid) {
        this.groupMid = groupMid;
    }

    public List<DeviceLis> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<DeviceLis> deviceList) {
        this.deviceList = deviceList;
    }

    public static class DeviceLis {
        private String devTid;
        private String ctrlKey;

        public DeviceLis() {
        }

        public String getDevTid() {
            return devTid;
        }

        public void setDevTid(String devTid) {
            this.devTid = devTid;
        }

        public String getCtrlKey() {
            return ctrlKey;
        }

        public void setCtrlKey(String ctrlKey) {
            this.ctrlKey = ctrlKey;
        }
    }
    /**
     *
     * [{"groupId":"afee215a898e4728b405ea091f045d4f","groupName":"string","deviceList":[{"devTid":"VDEV_NodeJS_117","ctrlKey":"c8859ba320d54ee18b229b09b477e1de"}],"desc":"111","createTime":1463221546055,"updateTime":null,"groupMid":"01e74de652f6"}]
     * */


}
