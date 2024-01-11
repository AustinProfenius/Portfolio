package com.example.fitnesstracker;

import java.util.ArrayList;
import java.util.Set;

public class Exercise {
    public String name;
    public ArrayList<com.example.fitnesstracker.Set> sets;

    public Exercise() {
    }

    public Exercise(String name, ArrayList<com.example.fitnesstracker.Set> mSets) {
        this.name = name;
        this.sets = mSets;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<com.example.fitnesstracker.Set> getSets() {
        return sets;
    }


}
