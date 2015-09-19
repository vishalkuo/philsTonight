package com.philstonight.Models;

/**
 * Created by yisen_000 on 2015-09-19.
 */
public class SquadMember {
    private String name;
    private String number;

    public SquadMember(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }
}
