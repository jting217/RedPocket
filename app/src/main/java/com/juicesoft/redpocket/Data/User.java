package com.juicesoft.redpocket.Data;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by USER on 2016/12/10.
 */

@IgnoreExtraProperties
public class User {
//    public String userId;    
    private Long coins;
    private Long dailyPlayTimes;
    private Long dailyResetDate;
    private Long dailyWinTimes;
    private Long dice;
//    private Long endDateInterval;
    private Long goldenHand;
    private Long ironFirst;
    private Long lifeCounter;
    private Long lives;
    private Long mindControl;
    private boolean signupRewardRedeemed;
//    private Long score;
    private Long specialTimeRewardGetDate;
//    private Long startDateInterval;
    private Long timer;
    private Long victory;
    private Long winWithPaper;
    private Long winWithRock;
    private Long winWithScissor;
    public Map<String, Object> transactionLogCoin = new HashMap<>();
    public Map<String, Object> transactionLogLife = new HashMap<>();
    public Map<String, Object> transactionLogPlay = new HashMap<>();
    public Map<String, Object> transactionLogTool = new HashMap<>();

    public User() {
    }

    public Long getCoins() {
        return coins;
    }

    public void setCoins(Long coins) {
        this.coins = coins;
    }

    public Long getDailyPlayTimes() {
        return dailyPlayTimes;
    }

    public void setDailyPlayTimes(Long dailyPlayTimes) {
        this.dailyPlayTimes = dailyPlayTimes;
    }

    public Long getDailyResetDate() {
        return dailyResetDate;
    }

    public void setDailyResetDate(Long dailyResetDate) {
        this.dailyResetDate = dailyResetDate;
    }

    public Long getDailyWinTimes() {
        return dailyWinTimes;
    }

    public void setDailyWinTimes(Long dailyWinTimes) {
        this.dailyWinTimes = dailyWinTimes;
    }

    public Long getDice() {
        return dice;
    }

    public void setDice(Long dice) {
        this.dice = dice;
    }

//    public Long getEndDateInterval() {
//        return endDateInterval;
//    }
//
//    public void setEndDateInterval(Long endDateInterval) {
//        this.endDateInterval = endDateInterval;
//    }

    public Long getGoldenHand() {
        return goldenHand;
    }

    public void setGoldenHand(Long goldenHand) {
        this.goldenHand = goldenHand;
    }

    public Long getIronFirst() {
        return ironFirst;
    }

    public void setIronFirst(Long ironFirst) {
        this.ironFirst = ironFirst;
    }

    public Long getLifeCounter() {
        return lifeCounter;
    }

    public void setLifeCounter(Long lifeCounter) {
        this.lifeCounter = lifeCounter;
    }

    public Long getLives() {
        return lives;
    }

    public void setLives(Long lives) {
        this.lives = lives;
    }

    public Long getMindControl() {
        return mindControl;
    }

    public void setMindControl(Long mindControl) {
        this.mindControl = mindControl;
    }

    public boolean isSignupRewardRedeemed() {
        return signupRewardRedeemed;
    }

    public void setSignupRewardRedeemed(boolean signupRewardRedeemed) {
        this.signupRewardRedeemed = signupRewardRedeemed;
    }

    //    public Long getScore() {
//        return score;
//    }
//
//    public void setScore(Long score) {
//        this.score = score;
//    }

    public Long getSpecialTimeRewardGetDate() {
        return specialTimeRewardGetDate;
    }

    public void setSpecialTimeRewardGetDate(Long specialTimeRewardGetDate) {
        this.specialTimeRewardGetDate = specialTimeRewardGetDate;
    }

//    public Long getStartDateInterval() {
//        return startDateInterval;
//    }
//
//    public void setStartDateInterval(Long startDateInterval) {
//        this.startDateInterval = startDateInterval;
//    }

    public Long getTimer() {
        return timer;
    }

    public void setTimer(Long timer) {
        this.timer = timer;
    }

    public Long getVictory() {
        return victory;
    }

    public void setVictory(Long victory) {
        this.victory = victory;
    }

    public Long getWinWithPaper() {
        return winWithPaper;
    }

    public void setWinWithPaper(Long winWithPaper) {
        this.winWithPaper = winWithPaper;
    }

    public Long getWinWithRock() {
        return winWithRock;
    }

    public void setWinWithRock(Long winWithRock) {
        this.winWithRock = winWithRock;
    }

    public Long getWinWithScissor() {
        return winWithScissor;
    }

    public void setWinWithScissor(Long winWithScissor) {
        this.winWithScissor = winWithScissor;
    }

    public Map<String, Object> getTransactionLogCoin() {
        return transactionLogCoin;
    }

    public void setTransactionLogCoin(Map<String, Object> transactionLogCoin) {
        this.transactionLogCoin = transactionLogCoin;
    }

    public Map<String, Object> getTransactionLogLife() {
        return transactionLogLife;
    }

    public void setTransactionLogLife(Map<String, Object> transactionLogLife) {
        this.transactionLogLife = transactionLogLife;
    }

    public Map<String, Object> getTransactionLogPlay() {
        return transactionLogPlay;
    }

    public void setTransactionLogPlay(Map<String, Object> transactionLogPlay) {
        this.transactionLogPlay = transactionLogPlay;
    }

    public Map<String, Object> getTransactionLogTool() {
        return transactionLogTool;
    }

    public void setTransactionLogTool(Map<String, Object> transactionLogTool) {
        this.transactionLogTool = transactionLogTool;
    }

    public User(Long coins, Long dailyPlayTimes, Long dailyResetDate, Long dailyWinTimes, Long dice, Long goldenHand,
                Long ironFirst, Long lifeCounter, Long lives, Long mindControl, boolean signupRewardRedeemed, Long specialTimeRewardGetDate,
                Long timer, Long victory, Long winWithPaper, Long winWithRock, Long winWithScissor){//(String userId, String nickName, String email){
//        this.userId = userId;
        this.coins = coins;
        this.dailyPlayTimes = dailyPlayTimes;
        this.dailyResetDate = dailyResetDate;
        this.dailyWinTimes = dailyWinTimes;
        this.dice = dice;
//        this.startDateInterval = startDateInterval;
        this.goldenHand = goldenHand;
        this.ironFirst = ironFirst;
        this.lifeCounter = lifeCounter;
        this.lives = lives;
        this.mindControl = mindControl;
        this.signupRewardRedeemed = signupRewardRedeemed;
        //this.score = score;
        this.specialTimeRewardGetDate = specialTimeRewardGetDate;
        this.timer = timer;
//        this.endDateInterval = endDateInterval;
        this.victory = victory;
        this.winWithPaper = winWithPaper;
        this.winWithRock = winWithRock;
        this.winWithScissor = winWithScissor;

    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("coins", coins);
        result.put("dailyPlayTimes", dailyPlayTimes);
        result.put("dailyResetDate", dailyResetDate);
        result.put("dailyWinTimes", dailyWinTimes);
        result.put("dice", dice);
        result.put("goldenHand", goldenHand);
        result.put("ironFirst", ironFirst);
        result.put("lifeCounter", lifeCounter);
        result.put("lives", lives);
        result.put("mindControl", mindControl);
        result.put("signupRewardRedeemed", signupRewardRedeemed);
        result.put("specialTimeRewardGetDate", specialTimeRewardGetDate);
        result.put("timer", timer);
        result.put("victory", victory);
        result.put("winWithPaper", winWithPaper);
        result.put("winWithRock", winWithRock);
        result.put("winWithScissor", winWithScissor);

        return result;
    }
}
