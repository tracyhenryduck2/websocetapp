package com.siterwell.demo.user;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gc-0001 on 2017/2/7.
 */
public class ClientUser {

    private String id;
    private long birthday;
    private String firstName;
    private String lastName;
    private long updateDate;
    private String phoneNumber;
    private String gender;
    private String description; //年龄
    private String email;

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


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id" , id);
            jsonObject.put("birthday" , birthday);
            jsonObject.put("firstName" , firstName);
            jsonObject.put("lastName" , lastName);
            jsonObject.put("updateDate" , updateDate);
            jsonObject.put("phoneNumber" , phoneNumber);
            jsonObject.put("gender" , gender);
            jsonObject.put("description" , description);
            jsonObject.put("email" , email);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "ClientUser{" +
                "id=" + id +
                "birthday=" + birthday +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", updateDate=" + updateDate +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", gender='" + gender + '\'' +
                ", description='" + description + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public ClientUser from(String input) {
        JSONObject object = null;
        try {
            object = new JSONObject(input);
            if(object.has("id")) {
                this.id = object.getString("id");
            }
            if(object.has("birthday")) {
                this.birthday = object.getLong("birthday");
            }
            if(object.has("firstName")) {
                this.firstName = object.getString("firstName");
            }
            if(object.has("lastName")) {
                this.lastName = object.getString("lastName");
            }
            if(object.has("updateDate")) {
                this.updateDate = object.getLong("updateDate");
            }
            if(object.has("phoneNumber")) {
                this.phoneNumber = object.getString("phoneNumber");
            }
            if(object.has("gender")) {
                this.gender = object.getString("gender");
            }
            if(object.has("description")) {
                this.description = object.getString("description");
            }
            if(object.has("email")) {
                this.email = object.getString("email");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

}
