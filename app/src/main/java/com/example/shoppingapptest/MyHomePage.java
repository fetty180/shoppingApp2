package com.example.shoppingapptest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


public class MyHomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener{
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    Toolbar toolbar;
    ImageView profileImage;
    FirebaseFirestore fStore;

    FirebaseAuth mAuth;
    StorageReference down;
    String usrId;
    Uri uri = null;
    TextView profileName;
    Button signOut;



    ViewPager pager;
    TabLayout mTabLayout;
    TabItem shoppingList,expired,schedule, doneShopping;
    PageAdapter pageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_home_page);
         toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        profileImage = findViewById(R.id.profileImage);
        profileName = findViewById(R.id.profileName);

        //for downloading image as in user profile
        mAuth =FirebaseAuth.getInstance();
        usrId = mAuth.getCurrentUser().getUid();
        down = FirebaseStorage.getInstance().getReference();
        fStore = FirebaseFirestore.getInstance();


        signOut = findViewById(R.id.logout);
        pager = findViewById(R.id.viewPager);
        mTabLayout = findViewById(R.id.tabLayout);
        shoppingList = findViewById(R.id.shoppingList);
        expired = findViewById(R.id.expired);
        schedule = findViewById(R.id.schedules);
        doneShopping = findViewById(R.id.completedSP);



        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);
//adding drawer with hamburger icon
        toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();



        pageAdapter = new PageAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, mTabLayout.getTabCount());
       pager.setAdapter(pageAdapter);
        // setting currentry active page
       mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
           @Override
           public void onTabSelected(TabLayout.Tab tab) {
               pager.setCurrentItem(tab.getPosition());
           }

           @Override
           public void onTabUnselected(TabLayout.Tab tab) {

           }

           @Override
           public void onTabReselected(TabLayout.Tab tab) {

           }
       });
       pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
       setUpToggle();




    }

    private void setUpToggle() {
        fStore.collection("wambugumoses7@gmail.com").document(usrId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        String kaRef = task.getResult().getString("First name");
                        profileName.setText(kaRef);
                    }
                }

            }
        });

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
       switch (item.getItemId()){
           case R.id.calculator:
               startActivity(new Intent(MyHomePage.this, calculator.class));
               break;
           case  R.id.calender:
               break;
    }
    return true;
}

    @Override
    public void onClick(View v) {
       startActivity(new Intent(MyHomePage.this, userProfile.class));

    }


    public void logOut(View view) {

        mAuth.signOut();
       // startActivity(new Intent(MyHomePage.this, MainActivity.class));
    }
}