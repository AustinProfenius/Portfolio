package com.example.fitnesstracker;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fitnesstracker.databinding.FragmentAddExerciseBinding;

import java.util.ArrayList;

public class AddExerciseFragment extends Fragment {

    public AddExerciseFragment() {
        // Required empty public constructor
    }

    FragmentAddExerciseBinding binding;
    ArrayList<Set> mSets = new ArrayList<>();
    public static final String TAG = "demo";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        binding = FragmentAddExerciseBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Add Exercise");
        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.cancelAddExercise();
            }
        });

        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.editTextPhone.getText().toString();

                if (name.isEmpty()) {
                    Toast.makeText(getActivity(), "Enter Phone Number!", Toast.LENGTH_SHORT).show();
                } else {
                    int checkedId = binding.radioGroup.getCheckedRadioButtonId();

                    if(checkedId == R.id.radioButton1){
                        for (int i = 0; i < 1; i++) {
                            mSets.add(new Set());
                        }
                    } else if(checkedId == R.id.radioButton2){
                        for (int i = 0; i < 2; i++) {
                            mSets.add(new Set());
                        }
                    } else if(checkedId == R.id.radioButton3){
                        for (int i = 0; i < 3; i++) {
                            mSets.add(new Set());
                        }
                    } else if(checkedId == R.id.radioButton4){
                        for (int i = 0; i < 4; i++) {
                            mSets.add(new Set());
                        }
                    } else if(checkedId == R.id.radioButton5){
                        for (int i = 0; i < 5; i++) {
                            mSets.add(new Set());
                        }
                    } else if(checkedId == R.id.radioButton6) {
                        for (int i = 0; i < 6; i++) {
                            mSets.add(new Set());
                        }
                    }
                    Exercise exercise = new Exercise(name,mSets);
                    Log.d(TAG, "onClick: " + mSets);
                    Log.d(TAG, "onClick: " + name);
                    mListener.sendCreatedExercise(exercise);
                }
            }
        });
    }

    AddExerciseListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AddExerciseListener) {
            mListener = (AddExerciseListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement AddExerciseListener");
        }
    }

    interface AddExerciseListener {
        void sendCreatedExercise(Exercise exercise);
        void cancelAddExercise();
    }
}