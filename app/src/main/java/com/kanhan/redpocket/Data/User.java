package com.kanhan.redpocket.Data;

/**
 * Created by USER on 2016/12/10.
 */

public class User {
//    public String userId;
    private String nickName;
    private String email;

    public User() {
    }

//    public String getUserId() {
//        return userId;
//    }
//
//    public void setUsrId(String userId) {
//        this.userId = userId;
//    }


    public String getNickName() {
        return nickName;
    }

    public String getEmail() {
        return email;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User(String nickName, String email){//(String userId, String nickName, String email){
//        this.userId = userId;
        this.nickName = nickName;
        this.email = email;
    }
}
