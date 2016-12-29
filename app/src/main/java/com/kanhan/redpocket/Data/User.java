package com.kanhan.redpocket.Data;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by USER on 2016/12/10.
 */

@IgnoreExtraProperties
public class User {
//    public String userId;
    private String nickName;
    private String email;
    private long coins;
    private long dailyPlayTimes;
    private long dailyResetDate;
    private long dailyWinTimes;
    private long dice;
    private long endDateInterval;
    private long goldenHand;
    private long ironFirst;
    private long lifeCounter;
    private long lives;
    private long mindControl;
    private long score;
    private long specialTimeRewardGetDate;
    private long startDateInterval;
    private long timer;
    private long victory;
    private long winWithPaper;
    private long winWithRock;
    private long winWithScissor;
    public Map<String, Boolean> transactionLogCoin = new HashMap<>();
    public Map<String, Boolean> transactionLogLife = new HashMap<>();
    public Map<String, Boolean> transactionLogPlay = new HashMap<>();
    public Map<String, Boolean> transactionLogTool = new HashMap<>();

    public User() {

    }



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
