package com.example.shoppingapptest;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PageAdapter extends FragmentPagerAdapter {
    private int tabnumber;

    public PageAdapter(@NonNull FragmentManager fm, int behavior, int tabs) {
        super(fm, behavior);
        this.tabnumber = tabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                return new ShoppingList();
            case  1:
                return  new ExpiringItems();
            case 2:
                return new Schedules();

            case 3:
                return new DoneShopping();
            default:
            return null;
        }

    }

    @Override
    public int getCount() {
        return tabnumber;
    }
}
