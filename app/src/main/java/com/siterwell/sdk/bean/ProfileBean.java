package com.siterwell.sdk.bean;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/16.
 */

public class ProfileBean implements Serializable {
    private static final long serialVersionUID = -3713919322707991034L;
    /**
     * birthday : 269568000000
     * firstName :
     * lastName : An
     * updateDate : 1463291911715
     * phoneNumber :
     * gender : MAN
     * avatarUrl : {"small":"http://hekr-images.ufile.ucloud.com.cn/ufile-3615199020600000000000-1530d614e5e8a377c56de591a2f30b24.jpg"}
     * description : 就是我
     * email : 2784451368@qq.com
     * age : 97
     */

    private long birthday;
    private String firstName;
    private String lastName;
    private long updateDate;
    private String phoneNumber;
    private String gender;
    /**
     * small : http://hekr-images.ufile.ucloud.com.cn/ufile-3615199020600000000000-1530d614e5e8a377c56de591a2f30b24.jpg
     */

    private AvatarUrl avatarUrl;
    private String description;
    private String email;
    private String age;
    private List<String> thirdAccount;

    public ProfileBean() {

    }

    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * 如果没有上传过图片，该字段可能为空，请直接使用{@link #avatarUrl()}
     */
    public AvatarUrl getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(AvatarUrl avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public List<String> getThirdAccount() {
        if (thirdAccount == null) {
            return new ArrayList<>();
        }
        return thirdAccount;
    }

    public void setThirdAccount(List<String> thirdAccount) {
        this.thirdAccount = thirdAccount;
    }

    public static class AvatarUrl {
        public AvatarUrl() {
        }

        private String small;
        private String big;

        public AvatarUrl(String small) {
            this.small = small;
        }

        public String getBig() {
            return big;
        }

        public void setBig(String big) {
            this.big = big;
        }

        /**
         * 如果没有上传过图片，该字段可能为空，请直接使用{@link #avatarUrl()}
         */
        public String getSmall() {
            return small;
        }

        public void setSmall(String small) {
            this.small = small;
        }
    }

    public String avatarUrl() {
        if (avatarUrl != null && !TextUtils.isEmpty(avatarUrl.getSmall())) {
            return avatarUrl.getSmall();
        } else {
            return "";
        }
    }

    @Override
    public String toString() {
        return "ProfileBean{" +
                "birthday=" + getBirthday() +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", updateDate=" + getUpdateDate() +
                ", phoneNumber='" + getPhoneNumber() + '\'' +
                ", gender='" + getGender() + '\'' +
                ", avatarUrl=" + avatarUrl() +
                ", description='" + getDescription() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", age='" + getAge() + '\'' +
                ", thirdAccount=" + getThirdAccount() +
                '}';
    }
}
