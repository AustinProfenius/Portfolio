package com.example.fitnesstracker;

public class Set {
    int reps,weight,rpe;

    public Set() {
    }

    public Set(int reps, int weight, int rpe) {
        this.reps = reps;
        this.weight = weight;
        this.rpe = rpe;
    }

    public int getRpe() {
        return rpe;
    }

    public void setRpe(int rpe) {
        this.rpe = rpe;
    }

    public Set(int reps, int weight) {
        this.reps = reps;
        this.weight = weight;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
