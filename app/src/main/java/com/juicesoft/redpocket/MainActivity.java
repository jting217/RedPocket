package com.juicesoft.redpocket;

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
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.juicesoft.redpocket.Data.SystemPreferences;
import com.juicesoft.redpocket.Data.User;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String SELECTED_ITEM = "arg_selected_item";

    private TextView nameTextView;
    private TextView emailTextView;
    private TextView uidTextView;
    private TextView textPlayMusic;

    private BottomNavigationView mBottomNav;
    private int mSelectedItem;
//    private PlayFragment playFg;
    private Intent svc;

    private DatabaseReference mWriteDatabase, mReadDatabase;
    private SystemPreferences mSystemPreferences;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity","onCreate");

        //        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        //取消螢幕休眠
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        //------------------------login start---------------
        nameTextView = (TextView) findViewById(R.id.nameTextView);
        emailTextView = (TextView) findViewById(R.id.emailTextView);
        uidTextView = (TextView) findViewById(R.id.uidTextView);
        textPlayMusic = (TextView) findViewById(R.id.txtPlayMusic);
        mSystemPreferences = new SystemPreferences();


        //接收Bundle
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        //  String tid = bundle.getString("Id");

        //nameTextView.setText(tid);

//        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            String uid = user.getUid();

            //nameTextView.setText(name);
            emailTextView.setText(email);
            uidTextView.setText(uid);
            readSystemPreferences();
            //writeNewUser(uid);
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
        playMusicByFlag();
    }

    @Override
    protected void onDestroy() {
        Log.d("MainActivity","onDestroy");
        super.onDestroy();
//        stopMusic();
//        PlayFragment fragment = PlayFragment.newInstance("Play",getColorFromRes(R.color.colorWhite),String.valueOf(textPlayMusic.getText()));
//        fragment.updateTimer(user.getUid());
        stopService(svc);
    }




    @Override
    protected void onPause() {
        Log.d("MainActivity","onPause");
        super.onPause();
        stopService(svc);
//        stopMusic();
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
                frag = PlayFragment.newInstance("Play",getColorFromRes(R.color.colorWhite),String.valueOf(textPlayMusic.getText()));
                break;
            case R.id.shops:
                Log.d("selectFragment","shops");
                frag = ShopsFragment.newInstance("Shops","");
                break;
            case R.id.league:
                Log.d("selectFragment","league");
                frag = LeagueFragment.newInstance("League","");
                break;
            case R.id.options:
                Log.d("selectFragment","options");
                frag = OptionsFragment.newInstance("Options",String.valueOf(textPlayMusic.getText()));
//                frag = MenuFragment.newInstance(getString(R.string.text_options)+" coming soon..",
//                        getColorFromRes(R.color.color_search));
                break;
            case R.id.info:
                Log.d("selectFragment","info");
                frag = InfoFragment.newInstance("Info","");
                break;
        }

        // update selected item
        mSelectedItem = item.getItemId();

        // uncheck the other items.
//        Log.d("selectItem",item.getTitle());
//        mBottomNav.getMenu().getItem(0).setChecked(true);

        for (int i = 0; i< mBottomNav.getMenu().size(); i++) {
            MenuItem menuItem = mBottomNav.getMenu().getItem(i);
        }
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
        Fragment frag = PlayFragment.newInstance("Play",getColorFromRes(R.color.colorWhite),String.valueOf(textPlayMusic.getText()));

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
//        FragmentManager fm = getSupportFragmentManager();
////if you added fragment via layout xml
//        PlayFragment fragment = (PlayFragment)fm.findFragmentById(R.id.);
//        fragment.yourPublicMethod();
        PlayFragment fragment = PlayFragment.newInstance("Play",getColorFromRes(R.color.colorWhite),String.valueOf(textPlayMusic.getText()));
        fragment.setIsFirstCreatTimer();

        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        goLoginScreen();
    }

    private void writeNewUser(final String userId) {
//        User user = new User(userId, nickName, email);

        //Getting values to store
        mWriteDatabase = FirebaseDatabase.getInstance().getReference("users");

        //Creating Person object


        //Adding values
//        user.setNickName(nickName);
//        user.setEmail(email);
//        user.setEmail("xxx@xxx.com");
//        Log.d("firebase",String.valueOf(sp.getSignupReward()));
        //Storing values to firebase
        mWriteDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                User user = new User();
                Long iniVal = Long.valueOf(0);

                if (snapshot.hasChild(userId)) {
                    // run some code
                    Log.d("Firebase",userId + " is existed!");

                }else{
                    user.setCoins(iniVal);
                    user.setDailyPlayTimes(iniVal);
                    user.setDailyResetDate(iniVal);
                    user.setDailyWinTimes(iniVal);
                    user.setDice(iniVal);
                    user.setGoldenHand(iniVal);
                    user.setIronFirst(iniVal);
                    user.setLifeCounter(iniVal);
                    user.setMindControl(iniVal);
                    user.setSignupRewardRedeemed(true);
//                    user.setScore(iniVal);
                    user.setSpecialTimeRewardGetDate(iniVal);
                    user.setTimer(iniVal);
                    user.setVictory(iniVal);
                    user.setWinWithPaper(iniVal);
                    user.setWinWithRock(iniVal);
                    user.setWinWithScissor(iniVal);
//                    user.setStartDateInterval(iniVal);
//                    user.setEndDateInterval(iniVal);
                    user.setLives(mSystemPreferences.getSignupReward());
                    Map<String, Object> nUser = user.toMap();
                    nUser.put("lifeCounter", ServerValue.TIMESTAMP);
                    mWriteDatabase.child(userId).setValue(nUser);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void readSystemPreferences() {
        Log.d("☆Firebase","readSystemPreferences");
        mReadDatabase = FirebaseDatabase.getInstance().getReference("systemPreferences");

        mReadDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot snapshot) {
                 // do some stuff once
                 SystemPreferences sp = snapshot.getValue(SystemPreferences.class);
                 //以下這段也可以用！
                 Map<String, Object> map = (HashMap<String, Object>) snapshot.getValue();
                    //Adding it to a string
                    for (Object key : map.keySet()) {
                        Log.w("firebase",key + " : " + map.get(key) +  map.get(key).getClass());
                    }
                 mSystemPreferences = sp;
                 Log.w("☆firebase",String.valueOf(sp.getCounterSec())+","+String.valueOf(mSystemPreferences.getSignupReward()));
                 writeNewUser(user.getUid());
             }

             @Override
             public void onCancelled(DatabaseError databaseError) {
                 Log.e("☆firebase failed: " , databaseError.getMessage());
             }

        });


        //Value event listener for realtime data update
//        mDatabase.addValueEventListener(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
////                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
//                    //Getting the data from snapshot
//                    SystemPreferences sp = snapshot.getValue(SystemPreferences.class);
//                    Log.d("firebase",sp.getCounterSec());
////                    Map<String, Object> map = (HashMap<String, Object>) snapshot.getValue();
////                    //Adding it to a string
////                    for (Object key : map.keySet()) {
////                        Log.d("firebase",key + " : " + map.get(key));
////                    }
////                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.e("The read failed: " , databaseError.getMessage());
//            }
//
//
//        });
       // return sp;
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


    public void playMusic()
    {
        startService(svc);
        textPlayMusic.setText("1");
    }
    public void stopMusic()
    {
        stopService(svc);
        textPlayMusic.setText("0");
    }


    public void playMusicByFlag(){
        String isPlay = textPlayMusic.getText().toString();
        if(isPlay.equals("1")){
            startService(svc);
        }
    }

}
