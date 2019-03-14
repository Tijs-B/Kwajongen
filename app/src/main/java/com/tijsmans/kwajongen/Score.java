package com.tijsmans.kwajongen;


import java.util.UUID;

public class Score {

    @Override
    public String toString() {
        return "Score " + mPassenspel + " " + mWijScore + " - " + mZijScore + ", game " + mGameId.toString() + ", index" + mIndex;
    }

    private UUID mId;
    private int mZijScore;
    private int mWijScore;
    private boolean mPassenspel;
    private UUID mGameId;
    private int mIndex;

    public Score() {
        this(UUID.randomUUID());
    }

    public Score(UUID id) {
        mId = id;
    }

    public int getZijScore() {
        return mZijScore;
    }

    public void setZijScore(int zijScore) {
        mZijScore = zijScore;
    }

    public int getWijScore() {
        return mWijScore;
    }

    public void setWijScore(int wijScore) {
        mWijScore = wijScore;
    }

    public boolean isPassenspel() {
        return mPassenspel;
    }

    public void setPassenspel(boolean passenspel) {
        mPassenspel = passenspel;
    }

    public UUID getId() {
        return mId;
    }

    public UUID getGameId() {
        return mGameId;
    }

    public void setGameId(UUID gameId) {
        mGameId = gameId;
    }

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int index) {
        mIndex = index;
    }
}
