package com.kpi.diploma.models;

/**
 * @author dtroshchuk
 */
public class Channel {
    private Long id;
    private Integer number;
    private String name;

    public Channel(long id, int number, String name) {
        this.id = id;
        this.number = number;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
