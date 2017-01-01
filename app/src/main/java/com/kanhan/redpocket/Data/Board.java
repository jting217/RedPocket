package com.kanhan.redpocket.Data;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by USER on 2016/12/10.
 */

@IgnoreExtraProperties
public class Board {
//    public String userId;
    private Long endDateInterval;
    public Map<String, Object> scores = new HashMap<>();
    private Long startDateInterval;

    public Board() {
    }

    public Long getEndDateInterval() {
        return endDateInterval;
    }

    public void setEndDateInterval(Long endDateInterval) {
        this.endDateInterval = endDateInterval;
    }

    public Map<String, Object> getScores() {
        return scores;
    }

    public void setScores(Map<String, Object> scores) {
        this.scores = scores;
    }

    public Long getStartDateInterval() {
        return startDateInterval;
    }

    public void setStartDateInterval(Long startDateInterval) {
        this.startDateInterval = startDateInterval;
    }

    public Board(Long endDateInterval, Long startDateInterval){
//        this.userId = userId;
        this.endDateInterval = endDateInterval;
        this.startDateInterval = startDateInterval;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("endDateInterval", endDateInterval);
        result.put("scores", scores);
        result.put("startDateInterval", startDateInterval);
        return result;
    }
}
