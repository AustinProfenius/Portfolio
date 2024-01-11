package com.example.fitnesstracker;

import java.io.Serializable;
import com.google.firebase.Timestamp;
import java.util.ArrayList;

public class Workout implements Serializable {
    public ArrayList<Exercise> exercises;
    public String name, uid, docId, notes;
    public Timestamp date;

    public Workout(String name, Timestamp date, String uid, String docId) {
        this.name = name;
        this.date = date;
        this.uid = uid;
        this.docId = docId;
    }

    public Workout() {
    }

    public ArrayList<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(ArrayList<Exercise> exercises) {
        this.exercises = exercises;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
