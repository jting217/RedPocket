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
import com.juicesoft.redpocket.Data.Tool;
import com.juicesoft.redpocket.Data.User;

import java.lang.reflect.Field;
import java.util.Date;
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
    private Intent svc;

    private DatabaseReference mWriteDatabase, mReadDatabase;
    private SystemPreferences mSystemPreferences;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private boolean isLoginOrSign;

    private static Tool[] tools ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        //取消螢幕休眠
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

//更改狀態列顏色，在 activity_main.xml要加 android:fitsSystemWindows="true" 屬性
//        WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
//        localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);

        setContentView(R.layout.activity_main);
        //------------------------login start---------------
        nameTextView = (TextView) findViewById(R.id.nameTextView);
        emailTextView = (TextView) findViewById(R.id.emailTextView);
        uidTextView = (TextView) findViewById(R.id.uidTextView);
        textPlayMusic = (TextView) findViewById(R.id.txtPlayMusic);
        mSystemPreferences = new SystemPreferences();
        isLoginOrSign = false;

        //接收Bundle
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        //  String tid = bundle.getString("Id");
        if(bundle != null){
            if(bundle.getString("srcClass") != null ){
                if(!bundle.getString("srcClass").equals("LoginActivity"))
                    isLoginOrSign = true;
            }
        }

        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            String uid = user.getUid();

            emailTextView.setText(email);
            uidTextView.setText(uid);
            readSystemPreferences();
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

        MenuItem selectedItem;
        if (savedInstanceState != null) {
            mSelectedItem = savedInstanceState.getInt(SELECTED_ITEM, 0);
            selectedItem = mBottomNav.getMenu().findItem(mSelectedItem);
            mBottomNav.getMenu().findItem(mSelectedItem).setChecked(true);
        } else {
            selectedItem = mBottomNav.getMenu().getItem(0);

        }
        selectFragment(selectedItem);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SELECTED_ITEM, mSelectedItem);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopService(svc);
    }

    @Override
    protected void onStart() {
        super.onStart();
        playMusicByFlag();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(svc);
    }




    @Override
    protected void onPause() {
        super.onPause();
        stopService(svc);
    }

    @Override
    public void onBackPressed() {
        MenuItem homeItem = mBottomNav.getMenu().getItem(0);
        if (mSelectedItem != homeItem.getItemId()) {
            selectFragment(homeItem);
        } else {
            super.onBackPressed();
        }
    }



    private void selectFragment(MenuItem item) {
        Fragment frag = null;

        switch (item.getItemId()) {
            case R.id.paly:
                frag = null;
                frag = PlayFragment.newInstance("Play",getColorFromRes(R.color.colorWhite),String.valueOf(textPlayMusic.getText()));
                break;
            case R.id.shops:
                frag = ShopsFragment.newInstance("Shops","");
                break;
            case R.id.league:
                frag = LeagueFragment.newInstance("League","");
                break;
            case R.id.options:
                frag = OptionsFragment.newInstance("Options",String.valueOf(textPlayMusic.getText()));
                break;
            case R.id.info:
                frag = InfoFragment.newInstance("Info","");
                break;
        }

        mSelectedItem = item.getItemId();


        for (int i = 0; i< mBottomNav.getMenu().size(); i++) {
            MenuItem menuItem = mBottomNav.getMenu().getItem(i);
        }
        updateToolbarText(item.getTitle());

        if (frag != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, frag);
            ft.commit();
        }
    }

    public void recallPlayFragment(){
        Fragment frag = PlayFragment.newInstance("Play",getColorFromRes(R.color.colorWhite),String.valueOf(textPlayMusic.getText()));
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container,frag);
        ft.commit();
    }

    private void updateToolbarText(CharSequence text) {
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

    public void logout() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        goLoginScreen();
    }

    public void logout(View view) {
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            goLoginScreen();
    }

    private void writeNewUser(final String userId) {

        mWriteDatabase = FirebaseDatabase.getInstance().getReference("users");

        mWriteDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                User user = new User();
                Long iniVal = Long.valueOf(0);

                if (snapshot.hasChild(userId)) {
                    // run some code
                    Log.d("Firebase",userId + " is existed!");

                }else{
                    if(isLoginOrSign) {
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
                        user.setSpecialTimeRewardGetDate(iniVal);
                        user.setTimer(iniVal);
                        user.setVictory(iniVal);
                        user.setWinWithPaper(iniVal);
                        user.setWinWithRock(iniVal);
                        user.setWinWithScissor(iniVal);
                        user.setLives(mSystemPreferences.getSignupReward());
                        Map<String, Object> nUser = user.toMap();
                        nUser.put("lifeCounter", ServerValue.TIMESTAMP);
                        mWriteDatabase.child(userId).setValue(nUser);
                    }else{
                        Log.e("Logout",(new Date()).toString());
                        logout();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void readSystemPreferences() {
        mReadDatabase = FirebaseDatabase.getInstance().getReference("systemPreferences");

        mReadDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot snapshot) {
                 SystemPreferences sp = snapshot.getValue(SystemPreferences.class);
                 //以下這段也可以用！
                 Map<String, Object> map = (HashMap<String, Object>) snapshot.getValue();
                    //Adding it to a string
                    //for (Object key : map.keySet()) {
                    //    Log.w("firebase",key + " : " + map.get(key) +  map.get(key).getClass());
                    //}
                 mSystemPreferences = sp;
                 writeNewUser(user.getUid());
             }

             @Override
             public void onCancelled(DatabaseError databaseError) {
                 Log.e("☆firebase failed: " , databaseError.getMessage());
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

    public void enableBottomNavListener() {
        mBottomNav.getMenu().getItem(0).setEnabled(true);
        mBottomNav.getMenu().getItem(1).setEnabled(true);
        mBottomNav.getMenu().getItem(2).setEnabled(true);
        mBottomNav.getMenu().getItem(3).setEnabled(true);
        mBottomNav.getMenu().getItem(4).setEnabled(true);
    }

    public void disalbeBottomNavListener(){
        mBottomNav.getMenu().getItem(0).setEnabled(false);
        mBottomNav.getMenu().getItem(1).setEnabled(false);
        mBottomNav.getMenu().getItem(2).setEnabled(false);
        mBottomNav.getMenu().getItem(3).setEnabled(false);
        mBottomNav.getMenu().getItem(4).setEnabled(false);
//        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//
//
////                mBottomNav.getMenu().getItem(1).setOnMenuItemClickListener(null);
////                mBottomNav.getMenu().getItem(2).setOnMenuItemClickListener(null);
////                mBottomNav.getMenu().getItem(3).setOnMenuItemClickListener(null);
////                mBottomNav.getMenu().getItem(4).setOnMenuItemClickListener(null);
//                return true;
//            }
//        });
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

    public void saveToolsArray(Tool[] toolArr){
        tools = toolArr;
    }

    public Tool[] getToolsArray(){
       return tools;
    }

    public void playMusicByFlag(){
        String isPlay = textPlayMusic.getText().toString();
        if(isPlay.equals("1")){
            startService(svc);
        }
    }

}
