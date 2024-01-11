package com.example.fitnesstracker;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginListener, RegisterFragment.RegisterListener,
            WorkoutsFragment.WorkoutsListener, AddWorkoutFragment.AddWorkoutListener, AddExerciseFragment.AddExerciseListener, EditWorkoutFragment.EditWorkoutListener {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public static final String TAG = "demo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "OnCreate: main");

        if(mAuth.getCurrentUser() == null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.rootView, new LoginFragment())
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.rootView, new WorkoutsFragment())
                    .commit();
        }
    }

    @Override
    public void gotoCreateAccount() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.rootView, new RegisterFragment())
                .commit();
    }

    @Override
    public void gotoLogin() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.rootView, new LoginFragment())
                .commit();
    }

    @Override
    public void authSuccessful() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.rootView, new WorkoutsFragment())
                .commit();
    }

    @Override
    public void gotoCreateWorkout() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.rootView, new AddWorkoutFragment(), "add-workout")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void logout() {
        mAuth.signOut();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.rootView, new LoginFragment())
                .commit();
    }

    @Override
    public void gotoWorkoutsDetails(Workout workout) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.rootView, EditWorkoutFragment.newInstance(workout),"edit-workout")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void gotoAddExercise() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.rootView, new AddExerciseFragment())
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void cancelAddWorkout() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void completedAddWorkout() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void sendCreatedExercise(Exercise exercise) {
        AddWorkoutFragment addWorkoutFragment = (AddWorkoutFragment) getSupportFragmentManager().findFragmentByTag("add-workout");
        if (addWorkoutFragment != null){
            addWorkoutFragment.addToExercises(exercise);
            getSupportFragmentManager().popBackStack();
            return;
        }

        EditWorkoutFragment EditWorkoutFragment = (EditWorkoutFragment) getSupportFragmentManager().findFragmentByTag("edit-workout");
        if(EditWorkoutFragment != null){
            EditWorkoutFragment.addToExercises(exercise);
            getSupportFragmentManager().popBackStack();
            return;
        }
    }

    @Override
    public void cancelAddExercise() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void editContact(Workout workout) {

    }

    @Override
    public void cancelContactDetails() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void doneEditingWorkout() {
        getSupportFragmentManager().popBackStack();
    }
}