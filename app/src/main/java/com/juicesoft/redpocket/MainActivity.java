package com.juicesoft.redpocket;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.Toast;

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
import com.juicesoft.redpocket.util.IabBroadcastReceiver;
import com.juicesoft.redpocket.util.IabHelper;
import com.juicesoft.redpocket.util.IabResult;
import com.juicesoft.redpocket.util.Inventory;
import com.juicesoft.redpocket.util.Purchase;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements IabBroadcastReceiver.IabBroadcastListener,
        DialogInterface.OnClickListener {
    private static final String SELECTED_ITEM = "arg_selected_item";

    private TextView nameTextView;
    private TextView emailTextView;
    private TextView uidTextView;
    private TextView textPlayMusic;
    private int mCoins, buyCoins;

    private BottomNavigationView mBottomNav;
    private int mSelectedItem;
    private Intent svc;

    private DatabaseReference mWriteDatabase, mReadDatabase;
    private SystemPreferences mSystemPreferences;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private boolean isLoginOrSign;
    private User mUser;

    private static Tool[] tools ;

    private final String TAG = "GoogleBilling";
    // The helper object
    IabHelper mHelper;

    // Provides purchase notification while this app is running
    IabBroadcastReceiver mBroadcastReceiver;

    // SKUs for our products: the premium upgrade (non-consumable) and gas (consumable)
    static final String SKU_PREMIUM = "premium";
    static final String SKU_GAS = "gas";

    static final String SKU_100_COINS = "sku_100_coins";
    static final String SKU_220_COINS = "sku_220_coins";

    // SKU for our subscription (infinite gas)
    static final String SKU_INFINITE_GAS_MONTHLY = "infinite_gas_monthly";
    static final String SKU_INFINITE_GAS_YEARLY = "infinite_gas_yearly";

    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;

    // Tracks the currently owned infinite gas SKU, and the options in the Manage dialog
    String mInfiniteGasSku = "";
    String mFirstChoiceSku = "";
    String mSecondChoiceSku = "";


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


         /*--------------------Google In-App billing Start--------------------*/
        String base64EncodedPublicKey = "";

        Log.d(TAG, "Creating IAB helper.");
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // Important: Dynamically register for broadcast messages about updated purchases.
                // We register the receiver here instead of as a <receiver> in the Manifest
                // because we always call getPurchases() at startup, so therefore we can ignore
                // any broadcasts sent while the app isn't running.
                // Note: registering this listener in an Activity is a bad idea, but is done here
                // because this is a SAMPLE. Regardless, the receiver must be registered after
                // IabHelper is setup, but before first call to getPurchases().
                mBroadcastReceiver = new IabBroadcastReceiver(MainActivity.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                registerReceiver(mBroadcastReceiver, broadcastFilter);

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error querying inventory. Another async operation in progress.");
                }
            }
        });
        /*--------------------Google In-App billing End--------------------*/

    }

    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }

            Log.d(TAG, "Query inventory was successful.");

            Purchase coins100Purchase = inventory.getPurchase(SKU_100_COINS);
            Purchase coins220Purchase = inventory.getPurchase(SKU_220_COINS);
            if (coins100Purchase != null && verifyDeveloperPayload(coins100Purchase)) {
                Log.d(TAG, "We have 100 coins. Update user.");
                try {
                    mHelper.consumeAsync(inventory.getPurchase(SKU_100_COINS), mConsumeFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error update user. Another async operation in progress.");
                }
                return;
            }else if (coins220Purchase != null && verifyDeveloperPayload(coins220Purchase)) {
                Log.d(TAG, "We have 100 coins. Update user.");
                try {
                    mHelper.consumeAsync(inventory.getPurchase(SKU_220_COINS), mConsumeFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error update user. Another async operation in progress.");
                }
                return;
            }

//            updateUi();
//            setWaitScreen(false);
            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };

    /** Verifies the developer payload of a purchase. */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();



        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        return true;
    }


    private void updateUserAfterBuyCoins(){
        final DatabaseReference mWriteDatabase = FirebaseDatabase.getInstance().getReference("users/" + user.getUid());
        mWriteDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if( mCoins >0 && buyCoins >0) {

                    Map newUserData = new HashMap();
                    mCoins+=buyCoins;
                    newUserData.put("coins", mCoins);
                    mWriteDatabase.updateChildren(newUserData);
                    alert("You got " + buyCoins + ". Your tank is now " + mCoins + " coins");
                }else{
                    complain("You got no coins.");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("☆firebase failed: ", databaseError.getMessage());

            }
        });
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
        // very important:
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }

        // very important:
        Log.d(TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.disposeWhenFinished();
            mHelper = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        playMusicByFlag();
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
                frag = InfoFragment.newInstance("Info",mSystemPreferences.getBarInfoURLString());
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

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {

    }

    @Override
    public void receivedBroadcast() {
        // Received a broadcast notification that the inventory of items has changed
        Log.d(TAG, "Received broadcast notification. Querying inventory.");
        try {
            mHelper.queryInventoryAsync(mGotInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            complain("Error querying inventory. Another async operation in progress.");
        }
    }

    // User clicked the "Buy Gas" button
    public void onBuyGasButtonClicked(View arg0) {
        Log.d(TAG, "Buy gas button clicked.");

//        if (mSubscribedToInfiniteGas) {
//            complain("No need! You're subscribed to infinite gas. Isn't that awesome?");
//            return;
//        }
//
//        if (mTank >= TANK_MAX) {
//            complain("Your tank is full. Drive around a bit!");
//            return;
//        }
//
//        // launch the gas purchase UI flow.
//        // We will be notified of completion via mPurchaseFinishedListener
//        setWaitScreen(true);
//        Log.d(TAG, "Launching purchase flow for gas.");

        /* TODO: for security, generate your payload here for verification. See the comments on
         *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
         *        an empty string, but on a production app you should carefully generate this. */
        String payload = "";

        try {
            mHelper.launchPurchaseFlow(this, SKU_GAS, RC_REQUEST,
                    mPurchaseFinishedListener, payload);
        } catch (IabHelper.IabAsyncInProgressException e) {
            Log.e(TAG,e.toString());
            complain("Error launching purchase flow. Another async operation in progress.");
            //setWaitScreen(false);
        }
    }


    public void onBuy100Coins() {
        Log.d(TAG, "onBuy100Coins.");
        buyCoins = 100;



        String payload = "";
        try {
            if (mHelper != null) mHelper.flagEndAsync();
                mHelper.launchPurchaseFlow(this, SKU_100_COINS, RC_REQUEST,
                    mPurchaseFinishedListener, payload);
        } catch (IabHelper.IabAsyncInProgressException e) {
            complain("Error launching purchase flow. Another async operation in progress.");
            //setWaitScreen(false);
        }
    }

    public void onBuy220Coins() {
        Log.d(TAG, "onBuy220Coins.");
        String payload = "";
        try {
            if (mHelper != null) mHelper.flagEndAsync();
                mHelper.launchPurchaseFlow(this, SKU_220_COINS, RC_REQUEST,
                    mPurchaseFinishedListener, payload);
        } catch (IabHelper.IabAsyncInProgressException e) {
            complain("Error launching purchase flow. Another async operation in progress.");
            //setWaitScreen(false);
        }
    }

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                complain("Error purchasing: " + result);
//                setWaitScreen(false);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.");
//                setWaitScreen(false);
                return;
            }

            Log.d(TAG, "Purchase successful.");

            if (purchase.getSku().equals(SKU_100_COINS)) {
                // bought 1/4 tank of gas. So consume it.
                Log.d(TAG, "Purchase is 100 coins. Starting 100 coins consumption.");
                try {
                    mHelper.consumeAsync(purchase, mConsumeFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error consuming 100 coins. Another async operation in progress.");
                    //setWaitScreen(false);
                    return;
                }
            }else if (purchase.getSku().equals(SKU_220_COINS)) {
                // bought 1/4 tank of gas. So consume it.
                Log.d(TAG, "Purchase is 220 coins. Starting 220 coins consumption.");
                try {
                    mHelper.consumeAsync(purchase, mConsumeFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error consuming 220 coins. Another async operation in progress.");
                    //setWaitScreen(false);
                    return;
                }
            }

        }
    };

    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(final Purchase purchase, IabResult result) {
            Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            // We know this is the "gas" sku because it's the only one we consume,
            // so we don't check which sku was consumed. If you have more than one
            // sku, you probably should check...
            if (result.isSuccess()) {
                // successfully consumed, so we apply the effects of the item in our
                // game world's logic, which in our case means filling the gas tank a bit
                DatabaseReference mReadDatabase = FirebaseDatabase.getInstance().getReference("users/" + user.getUid());

                mReadDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        User u = snapshot.getValue(User.class);
                        if(u != null) {
                            mUser = u;
                            mCoins = u.getCoins().intValue();
                            if (purchase.getSku().equals(SKU_100_COINS)){
                                buyCoins = 100;
                            }else if (purchase.getSku().equals(SKU_220_COINS)){
                                buyCoins = 220;
                            }
                            updateUserAfterBuyCoins();
                            Log.d(TAG, "Consumption successful. Provisioning.");


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("☆firebase failed: ", databaseError.getMessage());
                    }

                });

            }
            else {
                complain("Error while consuming: " + result);
            }
//            updateUi();
//            setWaitScreen(false);
            Log.d(TAG, "End consumption flow.");
        }
    };


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

    void complain(String message) {
        Log.e(TAG, "**** TrivialDrive Error: " + message);
        alert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }

}
