package com.kpi.diploma.models;

import java.util.Date;

/**
 * @author dtroshchuk
 */
public class Schedule {
    private long id;
    private int channel;
    private String name;
    private String startTime;
    private String endTime;

    public Schedule(long id, int channel, String name, String startTime, String endTime) {
        this.id = id;
        this.channel = channel;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
