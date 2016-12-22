package com.kanhan.redpocket.Data;

/**
 * Created by USER on 2016/12/10.
 */

public class User {
    public String usrid;
    public String nickname;
    public String useremail;

    public User() {
    }

    public String getUsrid() {
        return usrid;
    }

    public void setUsrid(String usrid) {
        this.usrid = usrid;
    }

    public String getNickname(String na) {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUseremail(String em) {
        return useremail;
    }

    public void setUseremail(String useremail) {
        this.useremail = useremail;
    }
    public User(String usrid, String nickname, String useremail){
        this.usrid = usrid;
        this.nickname = nickname;
        this.useremail = useremail;
    }
}
