package com.tijsmans.kwajongen;


import java.util.Date;
import java.util.UUID;

public class Game {
    private UUID mId;
    private Date mDate;
    private boolean mFinished;

    public Game() {
        this(UUID.randomUUID());
    }

    public Game(UUID id) {
        mId = id;
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isFinished() {
        return mFinished;
    }

    public void setFinished(boolean finished) {
        mFinished = finished;
    }
}
