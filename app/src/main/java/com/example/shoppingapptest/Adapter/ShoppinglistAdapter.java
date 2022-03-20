package com.example.shoppingapptest.Adapter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppingapptest.AddShoppingList;
import com.example.shoppingapptest.ExpiringItems;
import com.example.shoppingapptest.Model.ShoppingListModel;
import com.example.shoppingapptest.Model.expiringItemsModel;
import com.example.shoppingapptest.R;
import com.example.shoppingapptest.ShoppingList;
import com.example.shoppingapptest.addExpiringTask;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import io.grpc.Context;

public class ShoppinglistAdapter extends RecyclerView.Adapter <ShoppinglistAdapter.SpViewHolder> {
    private List<ShoppingListModel> myList;
    private ShoppingList activity;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private String thisUser;
    private FirebaseDatabase database;
    private DatabaseReference reference;
   // private SharedPreferences preferences ;
   // private  String collictionName;
   // private Context context;

    public ShoppinglistAdapter(ShoppingList shoppingList, List<ShoppingListModel> mylist){
        this.activity = shoppingList;
        this.myList = mylist;

    }

    @NonNull
    @Override
    public SpViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity.getActivity()).inflate(R.layout.shopping_list_task, parent,false);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        thisUser = auth.getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Title").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
       // preferences = activity.getActivity().getSharedPreferences("shared_Pref", android.content.Context.MODE_PRIVATE);
                //get.getSharedPreferences("SharedPrefs", context.MODE_PRIVATE);
       // collictionName = preferences.getString("text", "");

        return new SpViewHolder(view);
    }
    public  void deleteItem (int position){
        ShoppingListModel shoppingListModel = myList.get(position);

        reference.child("titles").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                firestore.collection("Shopping list").document(thisUser).collection(value).document(shoppingListModel.myListId).delete();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        myList.remove(position);
        notifyItemRemoved(position);
    }
    public android.content.Context getContext(){
        return activity.getActivity();
    }

    public  void editItem(int position){
        ShoppingListModel shoppingListModel = myList.get(position);

        Bundle bundle = new Bundle();
        bundle.putString("ItemName", shoppingListModel.getItemName());
        bundle.putString("Price", shoppingListModel.getPrice());
        bundle.putString("NumberOfItems",shoppingListModel.getNumberOfItems());
        bundle.putString("id", shoppingListModel.myListId);

        AddShoppingList addShoppingList = new AddShoppingList();
        addShoppingList.setArguments(bundle);
        addShoppingList.show(activity.getActivity().getSupportFragmentManager(), addShoppingList.getTag());
    }

    @Override
    public void onBindViewHolder(@NonNull SpViewHolder holder, int position) {
        ShoppingListModel listModel = myList.get(position);
        holder.spCheckbox.setText(listModel.getItemName());
        holder.Price.setText(listModel.getPrice());
        holder.numberOfItems.setText(listModel.getNumberOfItems());

        holder.spCheckbox.setChecked(toBoolean(listModel.getStatus()));
        holder.spCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    reference.child("titles").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String value = snapshot.getValue(String.class);
                            firestore.collection("Shopping list").document(thisUser).collection(value).document(listModel.myListId).update("status",1);


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }else {
                    reference.child("titles").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String value = snapshot.getValue(String.class);
                            firestore.collection("Shopping list").document(thisUser).collection(value).document(listModel.myListId).update("status",1);


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });



                }
            }
        });

    }
    private  boolean toBoolean(int status){
        return status != 0;
    }


    @Override
    public int getItemCount() {
        return myList.size();
    }

    public class SpViewHolder extends RecyclerView.ViewHolder{
        TextView Price, numberOfItems;
        CheckBox spCheckbox;

        public SpViewHolder(@NonNull View itemView) {
            super(itemView);
            Price = itemView.findViewById(R.id.PriceTV);
            numberOfItems = itemView.findViewById(R.id.NoOfItemsTV);
            spCheckbox = itemView.findViewById(R.id.checkbox_ShoppingList);
        }
    }
}
