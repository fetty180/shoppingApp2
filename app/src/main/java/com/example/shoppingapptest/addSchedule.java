package com.example.shoppingapptest;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class addSchedule extends BottomSheetDialogFragment {

    private TextView setEventDate;
    private EditText event, description;
    private Button mSave;
    private FirebaseFirestore firestore;
    private Context context;
    private  String eventDate = "";
    private String myId = "";
    private String eventDateUpdate = "";
    private FirebaseAuth auth;
    private String thisUser;
    public static final String listData = "SHARED_PREVS";
    public static final  String myTITLE= "TITLES";
    public static final String discriting = "DISCRIPTION";
    public static final String myDATE = "MYDATE";
    public ShoppingList activity;



    public static final String TAG = "addSchedule";

    public static addSchedule newInstance(){
        return new addSchedule();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.creating_schedule,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setEventDate = view.findViewById(R.id.set_date_tv);
        event  = view.findViewById(R.id.scheduleTitle);
        description  = view.findViewById(R.id.scheduleDescription);
        mSave = view.findViewById(R.id.savBtn);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        thisUser = auth.getCurrentUser().getUid();


        boolean isScheduleUpdate = false;

        final Bundle bundle = getArguments();
        if (bundle != null){
            isScheduleUpdate = true;
            String myTitle = bundle.getString("title");
            String myDesc = bundle.getString("description");
            myId = bundle.getString("id");//check here
            eventDateUpdate = bundle.getString("date");

            event.setText(myTitle);
            description.setText(myDesc);
            setEventDate.setText(eventDateUpdate);
        }


        setEventDate.setOnClickListener(new View.OnClickListener() {
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
                        setEventDate.setText(dayOfMonth+ "/" + month + "/" + year);
                        eventDate = dayOfMonth+ "/" + month + "/" + year;

                    }
                }, YEAR,MONTH,DAY);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()- 1000);

                datePickerDialog.show();
            }


        });
        boolean finalScheduleUpdate = isScheduleUpdate;
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String eventTitle = event.getText().toString();
                String myDescription = description.getText().toString();
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(listData,context.MODE_PRIVATE);
                        //PreferenceManager.getDefaultSharedPreferences(getActivity());
                        //getActivity().getSharedPreferences(listData,context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(myTITLE,eventTitle);
                editor.putString(discriting,myDescription );
                editor.putString(myDATE,eventDate);
                editor.apply();

                if (finalScheduleUpdate){
                    firestore.collection("Schedules").document(thisUser).collection("EVENTS").document(myId).update("title" ,eventTitle  , "description" , myDescription, "date", eventDate);
                    Toast.makeText(context, "Task Updated", Toast.LENGTH_SHORT).show();

                }else {

                    if (eventTitle.isEmpty() & myDescription.isEmpty()) {
                        Toast.makeText(context, "add an event and description ", Toast.LENGTH_SHORT).show();

                    } else {
                        Map<String, Object> eventMap = new HashMap<>();
                        eventMap.put("title", eventTitle);
                        eventMap.put("description", myDescription);
                        eventMap.put("date", eventDate);
                        eventMap.put("time stamp", FieldValue.serverTimestamp());

                        firestore.collection("Schedules").document(thisUser).collection("EVENTS").add(eventMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context, "event saved!", Toast.LENGTH_SHORT).show();
                                    //check this
                                   // try {
                                  //      addEventToCaledar();
                                  //  } catch (ParseException e) {
                                  //      e.printStackTrace();
                                  //  }

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

    private void addEventToCaledar() throws ParseException {

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

                    //getActivity().getSharedPreferences(listData, Context.MODE_PRIVATE);
            String ThisTitle = sharedPreferences.getString(myTITLE,"");
            String ThisDescription = sharedPreferences.getString(discriting, "");
            String date = sharedPreferences.getString(myDATE, "");
            Date ThisDate = dateFormat.parse(date);
            Intent intent = new Intent(Intent.ACTION_INSERT);
            intent.setData(CalendarContract.Events.CONTENT_URI);
            intent.putExtra(CalendarContract.Events.TITLE,ThisTitle);
            intent.putExtra(CalendarContract.Events.DESCRIPTION,ThisDescription);
            intent.putExtra(CalendarContract.Events.EXDATE,ThisDate);
            intent.putExtra(CalendarContract.Events.ALL_DAY, "true");
            if (intent.resolveActivity(context.getPackageManager())!= null){
                startActivity(intent);

            }else {
                Toast.makeText(getActivity(), "something is wrong", Toast.LENGTH_SHORT).show();
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
