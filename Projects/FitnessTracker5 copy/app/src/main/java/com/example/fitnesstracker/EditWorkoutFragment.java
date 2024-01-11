package com.example.fitnesstracker;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fitnesstracker.databinding.ExerciseWithSetsListItemBinding;
import com.example.fitnesstracker.databinding.SetListItemBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.fitnesstracker.databinding.FragmentEditWorkoutBinding;

public class EditWorkoutFragment extends Fragment {
    private static final String ARG_PARAM_WORKOUT = "ARG_PARAM_WORKOUT";
    private Workout mWorkout;
    ExercisesAdapter adapter;
    public static final String TAG = "demo";


    public EditWorkoutFragment() {
        // Required empty public constructor
    }

    public static EditWorkoutFragment newInstance(Workout workout) {
        EditWorkoutFragment fragment = new EditWorkoutFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM_WORKOUT, workout);
        fragment.setArguments(args);
        return fragment;
    }

    public void addToExercises(Exercise exercise){
        mExercises.add(exercise);
        Log.d(TAG, "addToExercises: "+exercise);
        adapter.notifyDataSetChanged();
        Log.d(TAG, "addToExercises: " + mExercises.get(1));
        HashMap<String, Object> data = new HashMap<>();
        data.put("exercises", mExercises);
        db.collection("workouts").document(mWorkout.getDocId()).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mWorkout = (Workout) getArguments().getSerializable(ARG_PARAM_WORKOUT);
        }
    }

    FragmentEditWorkoutBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEditWorkoutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    void displayWorkout(){
        binding.textViewName.setText(mWorkout.getName());
        if(mWorkout.getDate() != null){
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
            binding.textViewDate.setText(sdf.format(mWorkout.getDate().toDate()));

        } else {
            binding.textViewDate.setText("N/A");
        }
        mExercises.clear();
        mExercises.addAll(mWorkout.getExercises());
    }

    ArrayList<Exercise> mExercises = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ListenerRegistration workoutListener;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        displayWorkout();

        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.cancelContactDetails();
            }
        });

        binding.exerciseRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ExercisesAdapter();
        binding.exerciseRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        workoutListener = db.collection("workouts").document(mWorkout.getDocId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null) {
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(value != null && value.exists()) {
                    mWorkout = value.toObject(Workout.class);
                    displayWorkout();
                    adapter.notifyDataSetChanged();
                }
            }
        });

        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> data = new HashMap<>();
                data.put("exercises", mExercises);
                db.collection("workouts").document(mWorkout.getDocId()).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mListener.doneEditingWorkout();
                    }
                });
            }
        });

        binding.imageViewAddExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.gotoAddExercise();
            }
        });
    }

    private class ExercisesAdapter extends RecyclerView.Adapter<ExercisesAdapter.ExercisesViewHolder> {
        @NonNull
        @Override
        public ExercisesAdapter.ExercisesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ExerciseWithSetsListItemBinding itemBinding = ExerciseWithSetsListItemBinding.inflate(getLayoutInflater(), parent, false);
            return new ExercisesAdapter.ExercisesViewHolder(itemBinding);
        }
        @Override
        public void onBindViewHolder(@NonNull ExercisesAdapter.ExercisesViewHolder holder, int position) {
            holder.setupUI(mExercises.get(position));
            holder.bindSets(mExercises.get(position).getSets());
        }

        @Override
        public int getItemCount() {
            return mExercises.size();
        }

        class ExercisesViewHolder extends RecyclerView.ViewHolder {
            ExerciseWithSetsListItemBinding itemBinding;
            Exercise mExercise;
            private RecyclerView setsRecyclerView;
            private SetAdapter setAdapter;
            public ExercisesViewHolder(ExerciseWithSetsListItemBinding itemBinding) {
                super(itemBinding.getRoot());
                this.itemBinding = itemBinding;

                setsRecyclerView = itemBinding.recyclerViewSets;
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
            public void bindSets(List<Set> sets) {

                mExercise = mExercises.get(getAdapterPosition());
                setAdapter = new SetAdapter(getContext(), mExercise.getSets());
                setsRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
                setsRecyclerView.setAdapter(setAdapter);
                setAdapter.notifyDataSetChanged();

                int setsHeight = calculateRecyclerViewHeight(sets.size());

                ViewGroup.LayoutParams params = setsRecyclerView.getLayoutParams();
                params.height = setsHeight;
                setsRecyclerView.setLayoutParams(params);
            }
            private int calculateRecyclerViewHeight(int numberOfSets) {
                int singleItemHeight = 165;
                int totalHeight = singleItemHeight * numberOfSets;

                return totalHeight;
            }
        }
    }

    public class SetAdapter extends RecyclerView.Adapter<SetAdapter.SetViewHolder> {
        private List<Set> setList;
        private Context context;

        @NonNull
        @Override
        public SetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            SetListItemBinding itemBinding = SetListItemBinding.inflate(getLayoutInflater(), parent, false);
            return new SetViewHolder(itemBinding);
        }

        @Override
        public void onBindViewHolder(SetViewHolder holder, int position) {
            Set set = setList.get(position);
            holder.bind(set);
        }

        public SetAdapter(Context context, List<Set> sets) {
            this.context = context;
            this.setList = sets;
        }

        public void setSets(List<Set> sets) {
            this.setList = sets;
            notifyDataSetChanged(); // Notify the adapter that the data has changed
        }

        // SetViewHolder and other required methods

        @Override
        public int getItemCount() {
            return setList.size();
        }

        // SetViewHolder class
        public class SetViewHolder extends RecyclerView.ViewHolder {
            private EditText repsEditText;
            private EditText weightEditText;
            private EditText rpeEditText;
            SetListItemBinding itemBinding;
            Set mSet;

            public SetViewHolder(SetListItemBinding itemBinding) {
                super(itemBinding.getRoot());
                this.itemBinding = itemBinding;
                repsEditText = itemBinding.editTextReps;
                weightEditText = itemBinding.editTextWeight;
                rpeEditText = itemBinding.editTextRPE;

                // Initialize views (repsEditText, weightEditText, etc.)
                // ...
            }

            public void bind(Set set) {
                this.mSet = set;

                // Bind set data to views (repsEditText.setText, weightEditText.setText, etc.)
                repsEditText.setText(String.valueOf(set.getReps()));
                weightEditText.setText(String.valueOf(set.getWeight()));
                rpeEditText.setText(String.valueOf(set.getRpe()));

                // Update Set object when EditText values change
                repsEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (!editable.toString().isEmpty()) {
                            mSet.setReps(Integer.parseInt(editable.toString()));
                        }
                    }
                });

                weightEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (!editable.toString().isEmpty()) {
                            mSet.setWeight(Integer.parseInt(editable.toString()));
                        }
                    }
                });

                rpeEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (!editable.toString().isEmpty()) {
                            mSet.setRpe(Integer.parseInt(editable.toString()));
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(workoutListener != null){
            workoutListener.remove();
            workoutListener = null;
        }
    }

    EditWorkoutListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof EditWorkoutListener) {
            mListener = (EditWorkoutListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement ContactDetailsListener");
        }
    }

    interface EditWorkoutListener {
        void editContact(Workout workout);
        void cancelContactDetails();
        void doneEditingWorkout();
        void gotoAddExercise();
    }
}