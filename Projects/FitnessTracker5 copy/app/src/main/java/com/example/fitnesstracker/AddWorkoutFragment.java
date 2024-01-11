package com.example.fitnesstracker;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.fitnesstracker.databinding.FragmentAddWorkoutBinding;
import com.example.fitnesstracker.databinding.ExerciseListItemBinding;

public class AddWorkoutFragment extends Fragment {
    public AddWorkoutFragment() {
        // Required empty public constructor
    }

    FragmentAddWorkoutBinding binding;
    ArrayList<Exercise> mExercises = new ArrayList<>();
    ExercisesAdapter adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public static final String TAG = "demo";


    public void addToExercises(Exercise exercise){
        mExercises.add(exercise);
        Log.d(TAG, "addToExercises: " + exercise);
        Log.d(TAG, "addToExercises: " + mExercises);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddWorkoutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Create Workout");

        binding.imageViewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.gotoAddExercise();
            }
        });

        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.cancelAddWorkout();
            }
        });

        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.editTextName.getText().toString();

                if(name.isEmpty()){
                    Toast.makeText(getActivity(), "Name is required", Toast.LENGTH_SHORT).show();
                } else if(mExercises.size() == 0){
                    Toast.makeText(getActivity(), "At least one Exercise is required!", Toast.LENGTH_SHORT).show();
                } else {
                    HashMap<String, Object> data = new HashMap<>();

                    data.put("name", name);
                    data.put("exercises", mExercises);
                    DocumentReference docRef = db.collection("workouts").document();
                    data.put("docId", docRef.getId());
                    data.put("uid", mAuth.getCurrentUser().getUid());
                    data.put("date", FieldValue.serverTimestamp());

                    docRef.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mListener.completedAddWorkout();
                            } else {
                                Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ExercisesAdapter();
        binding.recyclerView.setAdapter(adapter);

    }

    private class ExercisesAdapter extends RecyclerView.Adapter<ExercisesAdapter.ExercisesViewHolder> {
        @NonNull
        @Override
        public ExercisesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ExerciseListItemBinding itemBinding = ExerciseListItemBinding.inflate(getLayoutInflater(), parent, false);
            return new ExercisesViewHolder(itemBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull ExercisesViewHolder holder, int position) {
            holder.setupUI(mExercises.get(position));
        }

        @Override
        public int getItemCount() {
            return mExercises.size();
        }

        class ExercisesViewHolder extends RecyclerView.ViewHolder {
            ExerciseListItemBinding itemBinding;
            Exercise mExercise;
            public ExercisesViewHolder(ExerciseListItemBinding itemBinding) {
                super(itemBinding.getRoot());
                this.itemBinding = itemBinding;
            }

            public void setupUI(Exercise exercise){
                this.mExercise = exercise;
                itemBinding.textViewExerciseName.setText(exercise.getName());
                String sets;
                if(exercise.getSets() != null) {
                    sets = "Sets: " + Integer.toString(exercise.getSets().size());
                }else{
                    sets = "Sets: 0";
                }
                itemBinding.textViewSets.setText(sets);
                itemBinding.imageViewDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mExercises.remove(mExercise);
                        notifyDataSetChanged();
                    }
                });
            }
        }
    }


    AddWorkoutListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AddWorkoutListener) {
            mListener = (AddWorkoutListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement AddWorkoutListener");
        }
    }

    interface AddWorkoutListener {
        void gotoAddExercise();
        void cancelAddWorkout();
        void completedAddWorkout();
    }

}