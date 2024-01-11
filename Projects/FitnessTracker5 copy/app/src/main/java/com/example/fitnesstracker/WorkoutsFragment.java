package com.example.fitnesstracker;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnesstracker.databinding.FragmentWorkoutsBinding;
import com.example.fitnesstracker.databinding.WorkoutListItemBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WorkoutsFragment extends Fragment {
    public WorkoutsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    FragmentWorkoutsBinding binding;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static final String TAG = "demo";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWorkoutsBinding.inflate(inflater, container, false);
        Log.d(TAG, "onCreateView: workouts");

        return binding.getRoot();
    }

    ListenerRegistration workoutsListener;
    ArrayList<Workout> mWorkouts = new ArrayList<>();
    WorkoutsAdapter adapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "OnViewCreated: workouts");

        binding.logout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.logout();
            }
        });
        binding.newWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.gotoCreateWorkout();
            }
        });


        adapter = new WorkoutsAdapter();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(adapter);
        workoutsListener = db.collection("workouts").whereEqualTo("uid", mAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if(error != null){
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                if(value != null){
                    mWorkouts.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        Workout workout = doc.toObject(Workout.class);
                        mWorkouts.add(workout);
                    }
                    adapter.notifyDataSetChanged();
                }

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(workoutsListener != null){
            workoutsListener.remove();
            workoutsListener = null;
        }
    }

    class WorkoutsAdapter extends RecyclerView.Adapter<WorkoutsAdapter.WorkoutViewHolder>{

        @NonNull
        @Override
        public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            WorkoutListItemBinding itemBinding = WorkoutListItemBinding.inflate(getLayoutInflater(), parent, false);
            return new WorkoutViewHolder(itemBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
            holder.setupUI(mWorkouts.get(position));
        }

        @Override
        public int getItemCount() {
            return mWorkouts.size();
        }

        class WorkoutViewHolder extends RecyclerView.ViewHolder{
            WorkoutListItemBinding itemBinding;
            Workout mWorkout;
            public WorkoutViewHolder(WorkoutListItemBinding itemBinding) {
                super(itemBinding.getRoot());
                this.itemBinding = itemBinding;
            }

            public void setupUI(Workout workout){
                mWorkout = workout;
                itemBinding.textViewWorkoutName.setText(workout.getName());
                if(mWorkout.getDate() != null){
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
                    itemBinding.textViewDate.setText(sdf.format(mWorkout.getDate().toDate()));

                } else {
                    itemBinding.textViewDate.setText("N/A");
                }

                int size = mWorkout.getExercises().size();

                if(size == 0){
                    itemBinding.textViewSetsWLI.setText("No Exercises!!");
                } else {
                    Exercise exercise = mWorkout.getExercises().get(0);

                    if(size == 1){
                        itemBinding.textViewSetsWLI.setText(exercise.getName());
                    } else {
                        Exercise exercise2 = mWorkout.getExercises().get(1);
                        itemBinding.textViewSetsWLI.setText(exercise.getName()+ ", " + exercise2.getName()+"....");
                    }
                }

                itemBinding.imageViewDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        db.collection("workouts").document(mWorkout.getDocId()).delete();
                    }
                });

                itemBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.gotoWorkoutsDetails(mWorkout);
                    }
                });
            }
        }
    }

    WorkoutsListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof WorkoutsListener){
            mListener = (WorkoutsListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement ContactsListener");
        }
    }

    interface WorkoutsListener {
        void gotoCreateWorkout();
        void logout();
        void gotoWorkoutsDetails(Workout workout);
    }
}