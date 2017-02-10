package com.juicesoft.redpocket.Data;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jting on 2016/12/24.
 */
@IgnoreExtraProperties
public class SystemPreferences {
    private Long counterSec;
    private Long dailyReward;
    private Long playTimesPerDice;
    private Long signupReward;
    private Long livesUpperLimit;
    private Long specialTimeEndDateInterval;
    private Long specialTimeReward;
    private Long specialTimeStartDateInterval;
    private Long winTimesGetOneDiceFirst;
    private Long winTimesGetOneDiceSecond;
    private Long winWithMatchResult;

    public SystemPreferences() {

    }

    public Long getCounterSec() {
        return counterSec;
    }

    public void setCounterSec(Long counterSec) {
        this.counterSec = counterSec;
    }

    public Long getDailyReward() {
        return dailyReward;
    }

    public void setDailyReward(Long dailyReward) {
        this.dailyReward = dailyReward;
    }

    public Long getPlayTimesPerDice() {
        return playTimesPerDice;
    }

    public void setPlayTimesPerDice(Long playTimesPerDice) {
        this.playTimesPerDice = playTimesPerDice;
    }

    public Long getSignupReward() {
        return signupReward;
    }

    public void setSignupReward(Long signupReward) {
        this.signupReward = signupReward;
    }

    public Long getLivesUpperLimit() {
        return livesUpperLimit;
    }

    public void setLivesUpperLimit(Long livesUpperLimit) {
        this.livesUpperLimit = livesUpperLimit;
    }

    public Long getSpecialTimeEndDateInterval() {
        return specialTimeEndDateInterval;
    }

    public void setSpecialTimeEndDateInterval(Long specialTimeEndDateInterval) {
        this.specialTimeEndDateInterval = specialTimeEndDateInterval;
    }

    public Long getSpecialTimeReward() {
        return specialTimeReward;
    }

    public void setSpecialTimeReward(Long specialTimeReward) {
        this.specialTimeReward = specialTimeReward;
    }

    public Long getSpecialTimeStartDateInterval() {
        return specialTimeStartDateInterval;
    }

    public void setSpecialTimeStartDateInterval(Long specialTimeStartDateInterval) {
        this.specialTimeStartDateInterval = specialTimeStartDateInterval;
    }

    public Long getWinTimesGetOneDiceFirst() {
        return winTimesGetOneDiceFirst;
    }

    public void setWinTimesGetOneDiceFirst(Long winTimesGetOneDiceFirst) {
        this.winTimesGetOneDiceFirst = winTimesGetOneDiceFirst;
    }

    public Long getWinTimesGetOneDiceSecond() {
        return winTimesGetOneDiceSecond;
    }

    public void setWinTimesGetOneDiceSecond(Long winTimesGetOneDiceSecond) {
        this.winTimesGetOneDiceSecond = winTimesGetOneDiceSecond;
    }

    public Long getWinWithMatchResult() {
        return winWithMatchResult;
    }

    public void setWinWithMatchResult(Long winWithMatchResult) {
        this.winWithMatchResult = winWithMatchResult;
    }

    public SystemPreferences(Long counterSec, Long dailyReward, Long playTimesPerDice, Long signupReward, Long livesUpperLimit, Long specialTimeEndDateInterval,
                             Long specialTimeReward, Long specialTimeStartDateInterval, Long winTimesGetOneDiceFirst, Long winTimesGetOneDiceSecond,
                             Long winWithMatchResult){
        this.counterSec = counterSec;
        this.dailyReward = dailyReward;
        this.playTimesPerDice = playTimesPerDice;
        this.signupReward = signupReward;
        this.livesUpperLimit = livesUpperLimit;
        this.specialTimeEndDateInterval = specialTimeEndDateInterval;
        this.specialTimeReward = specialTimeReward;
        this.specialTimeStartDateInterval = specialTimeStartDateInterval;
        this.winTimesGetOneDiceFirst = winTimesGetOneDiceFirst;
        this.winTimesGetOneDiceSecond = winTimesGetOneDiceSecond;
        this.winWithMatchResult = winWithMatchResult;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("counterSec", counterSec);
        result.put("dailyReward", dailyReward);
        result.put("playTimesPerDice", playTimesPerDice);
        result.put("signupReward", signupReward);
        result.put("livesUpperLimit", livesUpperLimit);
        result.put("specialTimeEndDateInterval", specialTimeEndDateInterval);
        result.put("specialTimeReward", specialTimeReward);
        result.put("specialTimeStartDateInterval", specialTimeStartDateInterval);
        result.put("winTimesGetOneDiceFirst", winTimesGetOneDiceFirst);
        result.put("winTimesGetOneDiceSecond", winTimesGetOneDiceSecond);
        result.put("winWithMatchResult", winWithMatchResult);
        return result;
    }

//    @Exclude
//    public SystemPreferences toClass(){
//        SystemPreferences sp = new SystemPreferences();
//        sp.setCounterSec(counterSec);
//        sp.setDailyReward(dailyReward);
//        sp.setSignupReward(signupReward);
//        sp.setSpecialTimeEndDateInterval(specialTimeEndDateInterval);
//        sp.setSpecialTimeReward(specialTimeReward);
//        sp.setSpecialTimeStartDateInterval(specialTimeStartDateInterval);
//        sp.setWinTimesGetOneDiceFirst(winTimesGetOneDiceFirst);
//        sp.setWinTimesGetOneDiceSecond(winTimesGetOneDiceSecond);
//        return sp;
//    }

}
