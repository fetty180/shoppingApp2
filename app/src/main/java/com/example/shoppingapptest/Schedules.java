package com.example.shoppingapptest;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.ColorSpace;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shoppingapptest.Adapter.SheduleAdapter;
import com.example.shoppingapptest.Model.ScheduleModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Schedules#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Schedules extends Fragment implements OnDialogCloseListner{
  private   RecyclerView recyclerView;
  private FloatingActionButton floatingActionButton;
  private FirebaseFirestore firestore;
  private SheduleAdapter adapter;
  private List<ScheduleModel> newList;
  private Query query;
  private ListenerRegistration listenerRegistration;
  private  FirebaseAuth auth;
  private String thisUser;




    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Schedules() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Schedules.
     */
    // TODO: Rename and change types and number of parameters
    public static Schedules newInstance(String param1, String param2) {
        Schedules fragment = new Schedules();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedules, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.scheduleRv);
        floatingActionButton = view.findViewById(R.id.ScheduleFab);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        thisUser = auth.getCurrentUser().getUid();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSchedule.newInstance().show(getActivity().getSupportFragmentManager(), addSchedule.TAG);
            }
        });
        newList = new ArrayList<>();
        adapter = new SheduleAdapter(Schedules.this,newList);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ScheduleTouchHelper(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        recyclerView.setAdapter(adapter);
       // retroiveData();




    }

    private void retroiveData() {

     firestore.collection("Schedules").document(thisUser).collection("EVENTS").orderBy("time stamp", Query.Direction.DESCENDING)
           .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                //assert value != null;
                for (DocumentChange documentChange : value.getDocumentChanges()){
                  if (documentChange.getType() == DocumentChange.Type.ADDED){
                      String ids = documentChange.getDocument().getId();
                      ScheduleModel scheduleModel = documentChange.getDocument().toObject(ScheduleModel.class).withid(ids);
                      newList.add(scheduleModel);
                      adapter.notifyDataSetChanged();
                  }
              }

            }
        });
    }
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        newList.clear();
        retroiveData();
        adapter.notifyDataSetChanged();
    }



    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onDialogClose(DialogInterface dialogInterface) {
        newList.clear();
        retroiveData();
        adapter.notifyDataSetChanged();

    }




}








