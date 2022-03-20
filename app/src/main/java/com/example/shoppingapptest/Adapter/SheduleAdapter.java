package com.example.shoppingapptest.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppingapptest.Model.ScheduleModel;

import com.example.shoppingapptest.R;
import com.example.shoppingapptest.Schedules;

import com.example.shoppingapptest.addSchedule;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SheduleAdapter extends RecyclerView.Adapter <SheduleAdapter.scheduleViewHolder>{

   private List<ScheduleModel> ScheduleList;
   private Schedules activity;
   private FirebaseFirestore firestore;
   private FirebaseAuth auth;
   private String thisUSer;
   private Context context;
    SharedPreferences sharedPreferences;

   public SheduleAdapter (Schedules schedulesActivity, List<ScheduleModel> ScheduleList){
       this.ScheduleList = ScheduleList;
       activity = schedulesActivity;
   }


    @NonNull
    @Override
    public scheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity.getActivity()).inflate(R.layout.retriving_schedule_event,parent , false);

        firestore  = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        thisUSer = auth.getCurrentUser().getUid();
        sharedPreferences = activity.requireContext().getSharedPreferences("SHARED_PREVS",Context.MODE_PRIVATE);
        return new scheduleViewHolder(view);
    }





    public  void deleteSchedule (int position){
        ScheduleModel scheduleModel = ScheduleList.get(position);
        firestore.collection("Schedules").document(thisUSer).collection("EVENTS").document(scheduleModel.scheduleId).delete();
        ScheduleList.remove(position);
        notifyItemRemoved(position);
    }

    public Context getScheduleContext() {
        return activity.getActivity();
    }



    public  void editSchedule(int position){
        ScheduleModel scheduleModel = ScheduleList.get(position);

        Bundle bundle = new Bundle();
        bundle.putString("title", scheduleModel.getTitle());
        bundle.putString("date", scheduleModel.getDate());
        bundle.putString("description",scheduleModel.getDescription());
        bundle.putString("id", scheduleModel.scheduleId);

        addSchedule addSchedule = new addSchedule();
        addSchedule.setArguments(bundle);
        addSchedule.show(activity.getActivity().getSupportFragmentManager(), addSchedule.getTag());
    }

    @Override
    public void onBindViewHolder(@NonNull scheduleViewHolder holder, int position) {

       ScheduleModel scheduleModel = ScheduleList.get(position);

       holder.mScheduleDateTv.setText(scheduleModel.getDate());
       holder.mNameTv.setText(scheduleModel.getTitle());
       holder.mDescription.setText(scheduleModel.getDescription());
       holder.calendar.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
              // SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
              // ScheduleModel scheduleModel = ScheduleList.get(position);

                       //PreferenceManager.getDefaultSharedPreferences(getActivity());

               //getActivity().getSharedPreferences(listData, Context.MODE_PRIVATE);
               String ThisTitle = scheduleModel.getTitle();
                      // sharedPreferences.getString("TITLES","");
               String ThisDescription = scheduleModel.getDescription();
                       //sharedPreferences.getString("DISCRIPTION", "");
               String date = scheduleModel.getDate();
                       //sharedPreferences.getString("MYDATE", "");
               Date ThisDate = null;
               //try {
                  // ThisDate = dateFormat.parse(date);
              // } catch (ParseException e) {
               //    e.printStackTrace();
              // }
               Intent intent = new Intent(Intent.ACTION_INSERT);
               intent.setData(CalendarContract.Events.CONTENT_URI);
               intent.putExtra(CalendarContract.Events.TITLE,ThisTitle);
               intent.putExtra(CalendarContract.Events.DESCRIPTION,ThisDescription);
               intent.putExtra(CalendarContract.Events.EXDATE,date);
               intent.putExtra(CalendarContract.Events.ALL_DAY, "true");
               if (intent.resolveActivity(activity.getActivity().getPackageManager())!= null){
                   activity.startActivity(intent);

               }else {
                   Toast.makeText(activity.getActivity(), "something is wrong", Toast.LENGTH_SHORT).show();
               }
             //  Toast.makeText(activity.getActivity(), "hello", Toast.LENGTH_SHORT).show();
           }
       });
    }

    @Override
    public int getItemCount() {
        return ScheduleList.size();
    }



    public class scheduleViewHolder extends RecyclerView.ViewHolder{

       TextView mScheduleDateTv;
       TextView mNameTv;
       TextView mDescription;
       Button calendar;

      public scheduleViewHolder(@NonNull View itemView) {
          super(itemView);
          mScheduleDateTv = itemView.findViewById(R.id.eventDateTv);
          mNameTv = itemView.findViewById(R.id.eventName);
          mDescription = itemView.findViewById(R.id.eventDescription);
          calendar = itemView.findViewById(R.id.addToCalendar);

      }
  }
}
