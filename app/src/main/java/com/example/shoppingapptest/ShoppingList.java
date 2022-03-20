package com.example.shoppingapptest;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shoppingapptest.Adapter.ShoppinglistAdapter;
import com.example.shoppingapptest.Model.ShoppingListModel;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShoppingList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShoppingList extends Fragment implements OnDialogCloseListner{
    private FloatingActionButton itemFab, titleFab;
    private TextView shoppingListTitle;
    private TextView ShoppingListDate;
    private RecyclerView recyclerView;
    private LinearLayoutManager  linearLayoutManager;
    public static  final  String SHARED_PREF = "SHARED_PREF";
    public static final  String shoppingListName = "text";
    public static final  String DATE = "DATE";

    public static final String listingFab = "myFab";
    private  boolean fabValue ;
    private  boolean hardCodeFabValue = false;
    private  String title;
    private FirebaseFirestore firestore;
    private ShoppinglistAdapter adapter;
    private List<ShoppingListModel> mList;
    private FirebaseAuth mAuth;
    private String thisUser;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date;
    private String todayDate;
    SharedPreferences sharedPreferences;

   private Query   query;
   private ListenerRegistration listenerRegistration;
   private FirebaseDatabase database;
   private DatabaseReference reference;
   private String collections ;





    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ShoppingList() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShoppingList.
     */
    // TODO: Rename and change types and number of parameters
    public static ShoppingList newInstance(String param1, String param2) {
        ShoppingList fragment = new ShoppingList();
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

        return inflater.inflate(R.layout.fragment_shopping_list, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        shoppingListTitle = view.findViewById(R.id.shoppingListtv);
         ShoppingListDate = view.findViewById(R.id.shoppingListDate);
        recyclerView = view.findViewById(R.id.shoppingListRecyclerview);
        firestore= FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        checkUid();

        calendar = Calendar.getInstance();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Title").child(FirebaseAuth.getInstance().getCurrentUser().getUid());



        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
         itemFab = view.findViewById(R.id.itemFab);
         titleFab = view.findViewById(R.id.listFab);
        dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        date = dateFormat.format(calendar.getTime());



         
         itemFab.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 AddShoppingList.newInstance().show(getActivity().getSupportFragmentManager(), AddShoppingList.TAG);
             }
         });
        titleFab.setClickable(true);

        titleFab.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
               AlertDialog.Builder myDialog = new AlertDialog.Builder(getActivity());
               myDialog.setTitle("Enter Shopping list name");
               final EditText myShoppingList = new EditText(getActivity());
               myShoppingList.setInputType(InputType.TYPE_CLASS_TEXT);
               myDialog.setView(myShoppingList);
               myDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       collections = myShoppingList.getText().toString().trim();
                       ShoppingListDate.setText(date);
                      shoppingListTitle.setText(collections);
                     // titleFab.setClickable(fabValue);

                      reference.addValueEventListener(new ValueEventListener() {
                          @Override
                          public void onDataChange(@NonNull DataSnapshot snapshot) {
                              reference.child("titles").setValue(collections);
                              reference.child("date").setValue(date);
                              Toast.makeText(getActivity(), "Shopping title added", Toast.LENGTH_SHORT).show();
                          }

                          @Override
                          public void onCancelled(@NonNull DatabaseError error) {
                              Toast.makeText(getActivity(), "Failed to add data", Toast.LENGTH_SHORT).show();

                          }
                      });

                   }
               });
               myDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.cancel();

                   }
               });
               myDialog.show();
               titleFab.setClickable(true);



             }
         });
        // loadData();
        // updateView();
        mList = new ArrayList<>();
        adapter= new ShoppinglistAdapter(ShoppingList.this,mList);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ShoppingListTouchHelper(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);
        saveData();

       // checkData();
       showData();


    }

    private void checkUid() {
        if (thisUser == null){
            //Toast.makeText(getActivity(), "welcome", Toast.LENGTH_SHORT).show();
        }else {
            thisUser = mAuth.getCurrentUser().getUid();}
    }

    private void checkData() {

        reference.child("titles").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
               if (value==""){
                   Toast.makeText(getActivity(), "wecome to Smart shopping", Toast.LENGTH_SHORT).show();
               }else {
                   showData();
               }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void saveData() {
        reference.child("titles").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                shoppingListTitle.setText(value);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        reference.child("date").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String leoDate = snapshot.getValue(String.class);
                ShoppingListDate.setText(leoDate);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void showData() {
        reference.child("titles").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                if (value==null){
                    Toast.makeText(getActivity(), "wecome to Smart shopping", Toast.LENGTH_SHORT).show();
                }else {


                  query =  firestore.collection("Shopping list").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection(value);
                           listenerRegistration= query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @SuppressLint("NotifyDataSetChanged")
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                    for (DocumentChange documentChange : value.getDocumentChanges()) {
                                        if (documentChange.getType() == DocumentChange.Type.ADDED) {
                                            String id = documentChange.getDocument().getId();
                                            ShoppingListModel model = documentChange.getDocument().toObject(ShoppingListModel.class).withid(id);
                                            mList.add(model);
                                            adapter.notifyDataSetChanged();
                                        }

                                    }
                                    listenerRegistration.remove();
                                }


                           });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //String collection = shoppingListTitle.getText().toString();

       // }


    }

    public void saveDatas() {


            dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            date = dateFormat.format(calendar.getTime());

            sharedPreferences = getActivity().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(shoppingListName, shoppingListTitle.getText().toString());
            editor.putBoolean(listingFab, hardCodeFabValue);
            editor.putString(DATE, date);

            editor.apply();

    }
   public void loadData(){
       if (thisUser== FirebaseAuth.getInstance().getCurrentUser().getUid()) {
         sharedPreferences = getActivity().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        title = sharedPreferences.getString(shoppingListName, "");
        fabValue = sharedPreferences.getBoolean(listingFab, false);
        todayDate = sharedPreferences.getString(DATE,"");
       }else {
           SharedPreferences settings = getActivity().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
           settings.edit().remove(shoppingListName).commit();
           if (shoppingListTitle.getText().toString() == "Shoppinglist title here"){
               shoppingListTitle.setText("Shoppinglist title here");
           }else {
               String tittle = String.valueOf(firestore.collection("Shopping list").document(thisUser).getParent());
               shoppingListTitle.setText(tittle);
           }
       }

    }
    public  void updateView(){
        shoppingListTitle.setText(title);
       ///titleFab.setClickable(fabValue);
     //   ShoppingListDate.setText(String.valueOf(firestore.collection("Shopping list").document(thisUser).);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
         mList.clear();
       showData();
         adapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onDialogClose(DialogInterface dialogInterface) {
       mList.clear();
       showData();
       adapter.notifyDataSetChanged();

    }



}