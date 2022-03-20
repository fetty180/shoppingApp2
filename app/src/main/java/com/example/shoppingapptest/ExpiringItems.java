package com.example.shoppingapptest;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.shoppingapptest.Adapter.ExperingAdapter;
import com.example.shoppingapptest.Model.expiringItemsModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.remote.WatchChange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExpiringItems#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExpiringItems extends Fragment implements OnDialogCloseListner {
    FirebaseFirestore firestore;
    List<expiringItemsModel> mList;
    RecyclerView  recyclerView;
    FloatingActionButton myFab;
    ExperingAdapter adapter;
    private FirebaseAuth auth;
    private String thisUser;

     Query query;
    ListenerRegistration listenerRegistration;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ExpiringItems() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExpiringItems.
     */
    // TODO: Rename and change types and number of parameters
    public static ExpiringItems newInstance(String param1, String param2) {
        ExpiringItems fragment = new ExpiringItems();
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
        return inflater.inflate(R.layout.fragment_expiring_items, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.expiredItemsRv);
        myFab = view.findViewById(R.id.fabExpired);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        thisUser = auth.getCurrentUser().getUid();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        myFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addExpiringTask.newInstance().show(getActivity().getSupportFragmentManager(), addExpiringTask.TAG);

            }
        });

         mList = new ArrayList<>();
        adapter = new ExperingAdapter(ExpiringItems.this, mList);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TouchHelper(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        recyclerView.setAdapter(adapter);
        showDAta();





    }

   public void showDAta() {

 query=firestore.collection("expiring items").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("DATA").orderBy("time", Query.Direction.DESCENDING);

  listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
           @SuppressLint("NotifyDataSetChanged")
           @Override
           public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

               assert value != null;
               for (DocumentChange documentChange : value.getDocumentChanges()){
                   if (documentChange.getType() == DocumentChange.Type.ADDED){
                       String id = documentChange.getDocument().getId();
                   expiringItemsModel   expiringItemsModel   = documentChange.getDocument().toObject(expiringItemsModel.class).withId(id);
                   mList.add(expiringItemsModel);
                   adapter.notifyDataSetChanged();
                   }
               }
listenerRegistration.remove();
           }
       }
       );



   }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mList.clear();
        showDAta();
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onDialogClose(DialogInterface dialogInterface) {
        mList.clear();
        showDAta();
       adapter.notifyDataSetChanged();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        listenerRegistration.remove();

    }
}