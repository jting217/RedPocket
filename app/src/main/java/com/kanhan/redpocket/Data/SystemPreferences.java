package com.kanhan.redpocket.Data;

import com.google.firebase.database.PropertyName;

/**
 * Created by jting on 2016/12/24.
 */

public class SystemPreferences {
    private Long counterSec;
    private Long dailyReward;
    private Long firstLoginLives;
    private Long specialTimeStartDateLongerval;
    private Long specialTimeEndDateLongerval;

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

    public void setDailyReward(Long dailyRaward) {
        this.dailyReward = dailyRaward;
    }


    public Long getFirstLoginLives() {
        return firstLoginLives;
    }

    public void setFirstLoginLives(Long firstLoginLives) {
        this.firstLoginLives = firstLoginLives;
    }


    public Long getSpecialTimeStartDateLongerval() {
        return specialTimeStartDateLongerval;
    }

    public void setSpecialTimeStartDateLongerval(Long specialTimeStartDateLongerval) {
        this.specialTimeStartDateLongerval = specialTimeStartDateLongerval;
    }


    public Long getSpecialTimeEndDateLongerval() {
        return specialTimeEndDateLongerval;
    }

    public void setSpecialTimeEndDateLongerval(Long specialTimeEndDateLongerval) {
        this.specialTimeEndDateLongerval = specialTimeEndDateLongerval;
    }

    public SystemPreferences(Long counterSec, Long dailyReward, Long firstLoginLives, Long specialTimeStartDateLongerval, Long specialTimeEndDateLongerval){
        this.counterSec = counterSec;
        this.dailyReward = dailyReward;
        this.firstLoginLives = firstLoginLives;
        this.specialTimeStartDateLongerval = specialTimeStartDateLongerval;
        this.specialTimeEndDateLongerval = specialTimeEndDateLongerval;
    }

}
