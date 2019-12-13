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

    public boolean isFinished() {
        return mWijScore == 0 || mZijScore == 0;
    }

    public Score calculateNewScore(String winner, boolean aangespeeld, boolean kapot) {
        if (this.isFinished()) {
            return null;
        }

        int newWijScore = this.getWijScore();
        int newZijScore = this.getZijScore();

        if (winner.equals("wij")) {
            newWijScore -= 1;

            if (aangespeeld)
                newZijScore += 1;
            if (kapot)
                newWijScore -= 1;
            if (this.isPassenspel())
                newWijScore -= 1;
        } else {
            newZijScore -= 1;

            if (aangespeeld)
                newWijScore += 1;
            if (kapot)
                newZijScore -= 1;
            if (this.isPassenspel())
                newZijScore -= 1;
        }

        newWijScore = Math.max(newWijScore, 0);
        newZijScore = Math.max(newZijScore, 0);

        Score newScore = new Score();
        newScore.setWijScore(newWijScore);
        newScore.setZijScore(newZijScore);
        newScore.setGameId(this.getGameId());
        return newScore;
    }
}
