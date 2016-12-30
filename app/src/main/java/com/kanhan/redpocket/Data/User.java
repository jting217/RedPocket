package com.kanhan.redpocket.Data;

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
    private Long goldenHand;
    private Long ironFirst;
    private Long lifeCounter;
    private Long lives;
    private Long mindControl;
    private Long score;
    private Long specialTimeRewardGetDate;
    private Long timer;
    private Long victory;
    private Long winWithPaper;
    private Long winWithRock;
    private Long winWithScissor;
    public Map<String, String> transactionLogCoin = new HashMap<>();
    public Map<String, String> transactionLogLife = new HashMap<>();
    public Map<String, String> transactionLogPlay = new HashMap<>();
    public Map<String, String> transactionLogTool = new HashMap<>();

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

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }

    public Long getSpecialTimeRewardGetDate() {
        return specialTimeRewardGetDate;
    }

    public void setSpecialTimeRewardGetDate(Long specialTimeRewardGetDate) {
        this.specialTimeRewardGetDate = specialTimeRewardGetDate;
    }



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

    public Map<String, String> getTransactionLogCoin() {
        return transactionLogCoin;
    }

    public void setTransactionLogCoin(Map<String, String> transactionLogCoin) {
        this.transactionLogCoin = transactionLogCoin;
    }

    public Map<String, String> getTransactionLogLife() {
        return transactionLogLife;
    }

    public void setTransactionLogLife(Map<String, String> transactionLogLife) {
        this.transactionLogLife = transactionLogLife;
    }

    public Map<String, String> getTransactionLogPlay() {
        return transactionLogPlay;
    }

    public void setTransactionLogPlay(Map<String, String> transactionLogPlay) {
        this.transactionLogPlay = transactionLogPlay;
    }

    public Map<String, String> getTransactionLogTool() {
        return transactionLogTool;
    }

    public void setTransactionLogTool(Map<String, String> transactionLogTool) {
        this.transactionLogTool = transactionLogTool;
    }

    public User(Long coins, Long dailyPlayTimes, Long dailyResetDate, Long dailyWinTimes, Long dice, Long goldenHand,
                Long ironFirst, Long lifeCounter, Long lives, Long mindControl, Long score, Long specialTimeRewardGetDate,
                Long timer, Long victory, Long winWithPaper, Long winWithRock, Long winWithScissor){//(String userId, String nickName, String email){
//        this.userId = userId;
        this.coins = coins;
        this.dailyPlayTimes = dailyPlayTimes;
        this.dailyResetDate = dailyResetDate;
        this.dailyWinTimes = dailyWinTimes;
        this.dice = dice;
        this.goldenHand = goldenHand;
        this.ironFirst = ironFirst;
        this.lifeCounter = lifeCounter;
        this.lives = lives;
        this.mindControl = mindControl;
        this.score = score;
        this.specialTimeRewardGetDate = specialTimeRewardGetDate;
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
        result.put("specialTimeRewardGetDate", specialTimeRewardGetDate);
        result.put("victory", victory);
        result.put("winWithPaper", winWithPaper);
        result.put("winWithRock", winWithRock);
        result.put("winWithScissor", winWithScissor);

        return result;
    }
}
