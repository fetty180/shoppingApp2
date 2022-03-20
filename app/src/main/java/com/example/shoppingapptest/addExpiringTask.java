package com.example.shoppingapptest;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class addExpiringTask extends BottomSheetDialogFragment {

    public static final String TAG = "addExpiringTask";
    private TextView setDueDate;
    private EditText expiredItem;
    private Button save;
    private FirebaseFirestore firestore;
    private Context context;
    private String duedate = "";
    private String id = "";
    private String dueDateUpdate = "";
    private ImageButton speachButton;
    private static final int RECOGNIZER_CODE = 1;
    private FirebaseAuth auth;
    private  String thisUser;


    public static addExpiringTask newInstance(){
        return new addExpiringTask();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.addexpireditem, container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setDueDate = view.findViewById(R.id.set_due_tv);
        expiredItem = view.findViewById(R.id.expireItem);
        save = view.findViewById(R.id.saveItem);
        speachButton = view.findViewById(R.id.speach_mic_btn);

        speachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "speak up");
                startActivityForResult(intent, RECOGNIZER_CODE);
            }
        });

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        thisUser = auth.getCurrentUser().getUid();

        boolean isUpdate = false;

        final Bundle bundle = getArguments();
        if (bundle != null){
            isUpdate = true;
            String task = bundle.getString("item");
            id = bundle.getString("id");//check here
            dueDateUpdate = bundle.getString("due");

            expiredItem.setText(task);
            setDueDate.setText(dueDateUpdate);

            if (task.length() > 0){//changed here
                save.setEnabled(false);
                save.setBackgroundColor(Color.GRAY);
            }
        }


        expiredItem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")){
                    save.setEnabled(false);
                    save.setBackgroundColor(Color.GRAY);

                }else {
                    save.setEnabled(true);
                    save.setBackgroundColor(getResources().getColor(R.color.green_blue));
                }

           }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });

        setDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();

                int MONTH = calendar.get(Calendar.MONTH);
                int YEAR = calendar.get(Calendar.YEAR);
                int DAY = calendar.get(Calendar.DATE);

                DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month +1;
                        setDueDate.setText(dayOfMonth+ "/" + month + "/" + year);
                        duedate = dayOfMonth+ "/" + month + "/" + year;

                    }
                }, YEAR,MONTH,DAY);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()- 1000);

                datePickerDialog.show();
            }
        });
        boolean finalIsUpdate = isUpdate;

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String expiredItems = expiredItem.getText().toString();

                if (finalIsUpdate){
                    firestore.collection("expiring items").document(thisUser).collection("DATA").document(id).update("item" ,expiredItems , "due" , duedate);
                    Toast.makeText(context, "item Updated", Toast.LENGTH_SHORT).show();

                }
                else {

                    if (expiredItems.isEmpty()) {

                        Toast.makeText(context, "add an item here", Toast.LENGTH_SHORT).show();
                    } else {
                        Map<String, Object> taskMap = new HashMap<>();

                        taskMap.put("item", expiredItems);
                        taskMap.put("due", duedate);
                        taskMap.put("status", 0);
                        taskMap.put("time", FieldValue.serverTimestamp());

                        firestore.collection("expiring items").document(thisUser).collection("DATA").add(taskMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context, "task saved", Toast.LENGTH_SHORT).show();

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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RECOGNIZER_CODE && resultCode == RESULT_OK){
            ArrayList<String> expiredItems = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            expiredItem.setText(expiredItems.get(0).toString());


        }
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
