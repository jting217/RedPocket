package com.kanhan.redpocket;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kanhan.redpocket.Data.User;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String SELECTED_ITEM = "arg_selected_item";

    private TextView nameTextView;
    private TextView emailTextView;
    private TextView uidTextView;

    private BottomNavigationView mBottomNav;
    private int mSelectedItem;
//    private PlayFragment playFg;
    private Intent svc;

    private DatabaseReference mDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity","onCreate");

        //        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //------------------------login start---------------
        nameTextView = (TextView) findViewById(R.id.nameTextView);
        emailTextView = (TextView) findViewById(R.id.emailTextView);
        uidTextView = (TextView) findViewById(R.id.uidTextView);

        mDatabase = FirebaseDatabase.getInstance().getReference();


        //接收Bundle
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        //  String tid = bundle.getString("Id");

        //nameTextView.setText(tid);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            String uid = user.getUid();

            //nameTextView.setText(name);
            emailTextView.setText(email);
            uidTextView.setText(uid);

            writeNewUser(uid,name,email);
        }
        else{
            goLoginScreen();
        }


        // ---------------------------login end--------------------------------------

        svc=new Intent(this, BackgroundSoundService.class);
        startService(svc);

        mBottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.disableShiftMode(mBottomNav);
        View view = mBottomNav.findViewById(R.id.paly);
        view.performClick();
        setBottomNavListener();
//        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                selectFragment(item);
//                return true;
//            }
//        });

        Log.d("+++++savedInstanceState", String.valueOf(savedInstanceState));
        MenuItem selectedItem;
        if (savedInstanceState != null) {
            mSelectedItem = savedInstanceState.getInt(SELECTED_ITEM, 0);
            selectedItem = mBottomNav.getMenu().findItem(mSelectedItem);
            mBottomNav.getMenu().findItem(mSelectedItem).setChecked(true);
        } else {
            selectedItem = mBottomNav.getMenu().getItem(0);

        }
        selectFragment(selectedItem);

        Log.d("++++++++++selectedItem", String.valueOf(selectedItem));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d("MainActivity","onSaveInstanceState");
        outState.putInt(SELECTED_ITEM, mSelectedItem);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        Log.d("MainActivity","");
        super.onStop();
        stopService(svc);
    }

    @Override
    protected void onStart() {
        Log.d("MainActivity","onStart");
        super.onStart();
        startService(svc);
    }

    @Override
    protected void onDestroy() {
        Log.d("MainActivity","onDestroy");
        super.onDestroy();
        stopService(svc);
    }

    @Override
    protected void onPause() {
        Log.d("MainActivity","onPause");
        super.onPause();
        stopService(svc);
    }

    @Override
    public void onBackPressed() {
        Log.d("MainActivity","onBackPressed");
        MenuItem homeItem = mBottomNav.getMenu().getItem(0);
        if (mSelectedItem != homeItem.getItemId()) {
            // select home item
            selectFragment(homeItem);
        } else {
            super.onBackPressed();
        }
    }

    private void selectFragment(MenuItem item) {
        Log.d("MainActivity","selectFragment");
        Fragment frag = null;
        // init corresponding fragment
        switch (item.getItemId()) {
            case R.id.paly:
//                frag = MenuFragment.newInstance(getString(R.string.text_play),
//                        getColorFromRes(R.color.color_home));
                frag = null;
                frag = PlayFragment.newInstance("Play",getColorFromRes(R.color.colorWhite));
                break;
            case R.id.shops:
                frag = MenuFragment.newInstance(getString(R.string.text_shops)+" coming soon..",
                        getColorFromRes(R.color.color_notifications));
            break;
            case R.id.league:
                frag = MenuFragment.newInstance(getString(R.string.text_league)+" coming soon..",
                        getColorFromRes(R.color.color_search));
            case R.id.options:
                frag = MenuFragment.newInstance(getString(R.string.text_options)+" coming soon..",
                        getColorFromRes(R.color.color_search));
            case R.id.info:
                frag = MenuFragment.newInstance(getString(R.string.text_info)+" coming soon..",
                        getColorFromRes(R.color.color_search));
                break;
        }



        // update selected item
        mSelectedItem = item.getItemId();

        // uncheck the other items.
        mBottomNav.getMenu().getItem(0).setChecked(true);

//        for (int i = 0; i< mBottomNav.getMenu().size(); i++) {
//            MenuItem menuItem = mBottomNav.getMenu().getItem(i);
//        }
        updateToolbarText(item.getTitle());

        if (frag != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

//            ft.add(R.id.container, frag, frag.getTag());
//            if(frag.isAdded()) {
                ft.replace(R.id.container, frag);
//            }else {
//                ft.add(R.id.container, frag, frag.getTag());

//            }

//            ft.addToBackStack(null);
            ft.commit();
        }
    }

    public void recallPlayFragment(){
        Log.d("MainActivity","");
        Fragment frag = PlayFragment.newInstance("Play",getColorFromRes(R.color.colorWhite));

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.add(R.id.container, frag, frag.getTag());
//        ft.remove(frag);
        ft.replace(R.id.container,frag);
        //ft.addToBackStack(null);
        ft.commit();


    }
    private void updateToolbarText(CharSequence text) {
        Log.d("MainActivity","");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(text);
        }
    }
    private int getColorFromRes(@ColorRes int resId) {
        return ContextCompat.getColor(this, resId);
    }

    public static class BottomNavigationViewHelper {
        public static void disableShiftMode(BottomNavigationView view) {
            BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
            try {
                Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
                shiftingMode.setAccessible(true);
                shiftingMode.setBoolean(menuView, false);
                shiftingMode.setAccessible(false);
                for (int i = 0; i < menuView.getChildCount(); i++) {
                    BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                    item.setShiftingMode(false);
                    // set once again checked value, so view will be updated
                    item.setChecked(item.getItemData().isChecked());
                }
            } catch (NoSuchFieldException e) {
                Log.e("Error", "Unable to get shift mode field");
            } catch (IllegalAccessException e) {
                Log.e("Error", "Unable to change value of shift mode");
            }
        }
    }


    private void goLoginScreen() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        goLoginScreen();
    }
    private void writeNewUser(final String userId, String nickName, String email) {
//        User user = new User(userId, nickName, email);

        //Getting values to store
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        //Creating Person object
        final User user = new User();

        //Adding values
        user.setNickName(nickName);
        user.setEmail(email);
        user.setEmail("xxx@xxx.com");

        //Storing values to firebase
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(userId)) {
                    // run some code
                    Log.d("Firebase",userId + " is existed!");

                }else{
                    mDatabase.child(userId).setValue(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    public void setBottomNavListener(){
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectFragment(item);
                return true;
            }
        });
    }
    public void removeBottomNavListener(){
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                mBottomNav.getMenu().getItem(1).setOnMenuItemClickListener(null);
                mBottomNav.getMenu().getItem(2).setOnMenuItemClickListener(null);
                mBottomNav.getMenu().getItem(3).setOnMenuItemClickListener(null);
                mBottomNav.getMenu().getItem(4).setOnMenuItemClickListener(null);
                return true;
            }
        });
    }


}
