package com.example.countingshakes.model;

import java.util.Date;

/**
 * Created by cyn on 02/19/2016.
 */
public class RankingItem implements Comparable<RankingItem> {
    private String name;
    private Date date;
    private int score;

    public String getName() {
        return name;
    }

    public Date getDate() {
        return date;
    }

    public int getScore() {
        return score;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "RankingItem{" +
                "name='" + name + '\'' +
                ", date=" + date +
                ", score=" + score +
                '}';
    }

    @Override
    public int compareTo(RankingItem another) {
        //return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
        return another.getScore() < score ? -1 : (another.getScore() == score ? 0 : 1);
    }
}