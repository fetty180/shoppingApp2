package com.example.shoppingapptest.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppingapptest.ExpiringItems;
import com.example.shoppingapptest.Model.expiringItemsModel;
import com.example.shoppingapptest.R;
import com.example.shoppingapptest.addExpiringTask;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ExperingAdapter extends RecyclerView.Adapter<ExperingAdapter.MyViewHolder> {
    private List<expiringItemsModel> myList;
    private ExpiringItems activity;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private String thisUser;


    public  ExperingAdapter(ExpiringItems activity, List<expiringItemsModel> itemList){
      this.myList = itemList;
      this.activity = activity;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(activity.getActivity()).inflate(R.layout.expiring_items_task, parent,false);

      firestore = FirebaseFirestore.getInstance();
      auth = FirebaseAuth.getInstance();
      thisUser = auth.getCurrentUser().getUid();
      return new MyViewHolder(view);
    }


    public  void deleteItem (int position){
        expiringItemsModel expiringItemsModel = myList.get(position);
        firestore.collection("expiring items").document(thisUser).collection("DATA").document(expiringItemsModel.ItemId).delete();
        myList.remove(position);
        notifyItemRemoved(position);
    }
    public Context getContext(){
        return activity.getActivity();
    }

    public  void editItem(int position){
        expiringItemsModel expiringItemsModel = myList.get(position);

        Bundle bundle = new Bundle();
        bundle.putString("item", expiringItemsModel.getItem());
        bundle.putString("due", expiringItemsModel.getDue());
        bundle.putString("id", expiringItemsModel.ItemId);

        addExpiringTask addExpiringTask = new addExpiringTask();
        addExpiringTask.setArguments(bundle);
        addExpiringTask.show(activity.getActivity().getSupportFragmentManager(), addExpiringTask.getTag());
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        expiringItemsModel expiringItemsModel = myList.get(position);
        holder.myCheckbox.setText(expiringItemsModel.getItem());
        holder.dueDate.setText("Due date " + expiringItemsModel.getDue());

        holder.myCheckbox.setChecked(toBoolean(expiringItemsModel.getStatus()));

        holder.myCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    firestore.collection("expiring items").document(thisUser).collection("DATA").document(expiringItemsModel.ItemId).update("status",1);
                }
                else { firestore.collection("expiring items").document(thisUser).collection("DATA").document(expiringItemsModel.ItemId).update("status", 0); }

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

    public class  MyViewHolder extends  RecyclerView.ViewHolder{
        TextView dueDate;
        CheckBox myCheckbox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            dueDate = itemView.findViewById(R.id.expired_Date);
            myCheckbox = itemView.findViewById(R.id.checkbox_expiredItem);
        }
    }
}
