package com.juicesoft.redpocket.Data;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by USER on 2016/12/10.
 */

@IgnoreExtraProperties
public class ScoreFormat {

    private String displayName;
    private String score;

    public ScoreFormat() {
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public ScoreFormat(String displayName,  String score){

        this.displayName = displayName;
        this.score = score;

    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("displayName", displayName);
        result.put("score", score);

        return result;
    }
}
