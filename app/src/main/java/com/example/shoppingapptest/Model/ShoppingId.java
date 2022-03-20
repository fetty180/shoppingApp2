package com.example.shoppingapptest.Model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class ShoppingId {
  @Exclude
    public String myListId;
  public <M extends ShoppingId> M withid(@NonNull final String id){
    this.myListId = id;
    return (M) this;
  }

}
