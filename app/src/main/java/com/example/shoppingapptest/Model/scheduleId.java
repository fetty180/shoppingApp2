package com.example.shoppingapptest.Model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class scheduleId {
    @Exclude
    public String scheduleId;

    public <K extends  scheduleId> K withid(@NonNull final String id){
        this.scheduleId = id;
        return (K) this;
    }

}
