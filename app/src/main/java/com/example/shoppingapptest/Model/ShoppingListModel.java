package com.example.shoppingapptest.Model;

public class ShoppingListModel extends ShoppingId{
  private   String ItemName,Price,NumberOfItems;
   private int status;

    public String getItemName() {
        return ItemName;
    }

    public String getPrice() {
        return Price;
    }

    public String getNumberOfItems() {
        return NumberOfItems;
    }

    public int getStatus() {
        return status;
    }
}
