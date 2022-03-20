package com.example.shoppingapptest;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddShoppingList extends BottomSheetDialogFragment {

    private EditText itemED, priceED, numED;
    private Button saveItems;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private String userId;
    private Context context;
    private SharedPreferences preferences;
    private  String collictionName;
    private String id;
    private DatabaseReference reference;
    private FirebaseDatabase database;



    public static final String TAG = "AddShoppingList";

    public  static AddShoppingList newInstance(){
        return  new AddShoppingList();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.shopping_list_drop_sheet,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        itemED = view.findViewById(R.id.itemShoppingList);
        priceED = view.findViewById(R.id.priceShoppingList);
        numED = view.findViewById(R.id.numberOfItems);
        saveItems = view.findViewById(R.id.saveItemsss);
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        preferences = getActivity().getSharedPreferences(userId, context.MODE_PRIVATE);
        //collictionName = preferences.getString("text", "");

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Title").child(userId);

        reference.child("titles").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                collictionName = value;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        boolean isUpdate = false;

        final Bundle bundle = getArguments();
        if (bundle != null) {
            isUpdate = true;
            String item = bundle.getString("ItemName");
            String price = bundle.getString("Price");
            String itemNo = bundle.getString("NumberOfItems");
            id = bundle.getString("id");//check here
            itemED.setText(item);
            priceED.setText(price);
            numED.setText(itemNo);
        }




        boolean finalIsUpdate = isUpdate;
        saveItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemName = itemED.getText().toString().trim();
                String price = priceED.getText().toString().trim();
                String numItems = numED.getText().toString().trim();

                if (finalIsUpdate){
                    firestore.collection("Shopping list").document(userId).collection(collictionName).document(id).update("ItemName" ,itemName , "Price" ,price,"NumberOfItems",numItems);
                    Toast.makeText(context, "item Updated", Toast.LENGTH_SHORT).show();

                }
                else {

                    if (itemName.isEmpty()) {

                        Toast.makeText(context, "Please input item name", Toast.LENGTH_SHORT).show();
                    } else {
                        Map<String, Object> itemsMap = new HashMap<>();
                        itemsMap.put("ItemName", itemName);
                        itemsMap.put("Price", price);
                        itemsMap.put("NumberOfItems", numItems);
                        itemsMap.put("status", 0);
                        itemsMap.put("time", FieldValue.serverTimestamp());


                        firestore.collection("Shopping list").document(userId).collection(collictionName).add(itemsMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context, "Item has been saved to firebase", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });

                    }
                }
                dismiss();
                
            }
        });

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if (activity instanceof OnDialogCloseListner){
            ((OnDialogCloseListner)activity).onDialogClose(dialog);
        }
    }
}
