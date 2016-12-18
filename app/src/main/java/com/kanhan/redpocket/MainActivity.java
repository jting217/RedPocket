package com.kanhan.redpocket;

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
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity {
    private static final String SELECTED_ITEM = "arg_selected_item";

    private TextView textFavorites;
    private TextView textSchedules;
    private TextView textMusic;
    private BottomNavigationView mBottomNav;
    private int mSelectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.disableShiftMode(mBottomNav);
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectFragment(item);
                return true;
            }
        });
        MenuItem selectedItem;
        if (savedInstanceState != null) {
            mSelectedItem = savedInstanceState.getInt(SELECTED_ITEM, 0);
            selectedItem = mBottomNav.getMenu().findItem(mSelectedItem);
        } else {
            selectedItem = mBottomNav.getMenu().getItem(0);
        }
        selectFragment(selectedItem);

//        textFavorites = (TextView) findViewById(R.id.text_favorites);
//        textSchedules = (TextView) findViewById(R.id.text_schedules);
//        textMusic = (TextView) findViewById(R.id.text_music);

//        BottomNavigationView bottomNavigationView = (BottomNavigationView)
//                findViewById(R.id.bottom_navigation);
//        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
//        Log.d("☆","start");
//        bottomNavigationView.setOnNavigationItemSelectedListener(
//                new BottomNavigationView.OnNavigationItemSelectedListener() {
//                    Fragment frag = null;
//                    FragmentTransaction ft;
//                    @Override
//                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                        //item.setIcon(R.drawable.ic_access_time_white_24dp);
//                        //item.set
//
//                        switch (item.getItemId()) {
//                            case R.id.paly:
//                                frag = MenuFragment.newInstance(getString(R.string.text_play),
//                                        getColorFromRes(R.color.color_home));
//                                ft = getSupportFragmentManager().beginTransaction();
//                                ft.add(R.id.container, frag, frag.getTag());
//                                ft.commit();
//
//                                Log.d("☆","1");
//
//                                break;
//                            case R.id.shops:
//                                frag = MenuFragment.newInstance(getString(R.string.text_shops),
//                                        getColorFromRes(R.color.color_notifications));
//                                Log.d("☆","2");
//                                ft = getSupportFragmentManager().beginTransaction();
//                                ft.add(R.id.container, frag, frag.getTag());
//                                ft.commit();
//
//                                break;
//                            case R.id.league:
//                                frag = MenuFragment.newInstance(getString(R.string.text_league),
//                                        getColorFromRes(R.color.color_home));
//                                Log.d("☆","3");
//                                ft = getSupportFragmentManager().beginTransaction();
//                                ft.add(R.id.container, frag, frag.getTag());
//                                ft.commit();
////                                textFavorites.setVisibility(View.GONE);
////                                textSchedules.setVisibility(View.GONE);
////                                textMusic.setVisibility(View.VISIBLE);
//                                break;
//                        }
//                        return false;
//                    }
//
//
//                });

    }

    private void selectFragment(MenuItem item) {
        Fragment frag = null;
        // init corresponding fragment
        switch (item.getItemId()) {
            case R.id.paly:
                frag = MenuFragment.newInstance(getString(R.string.text_play),
                        getColorFromRes(R.color.color_home));
                break;
            case R.id.shops:
                frag = MenuFragment.newInstance(getString(R.string.text_shops),
                        getColorFromRes(R.color.color_notifications));
                break;
            case R.id.league:
                frag = MenuFragment.newInstance(getString(R.string.text_league),
                        getColorFromRes(R.color.color_search));
            case R.id.options:
                frag = MenuFragment.newInstance(getString(R.string.text_options),
                        getColorFromRes(R.color.color_search));
            case R.id.info:
                frag = MenuFragment.newInstance(getString(R.string.text_info),
                        getColorFromRes(R.color.color_search));
                break;
        }

        // update selected item
        mSelectedItem = item.getItemId();

        // uncheck the other items.
        for (int i = 0; i< mBottomNav.getMenu().size(); i++) {
            MenuItem menuItem = mBottomNav.getMenu().getItem(i);
            menuItem.setChecked(menuItem.getItemId() == item.getItemId());
        }

        updateToolbarText(item.getTitle());

        if (frag != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.container, frag, frag.getTag());
            ft.commit();
        }
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
                //Timber.e(e, "Unable to get shift mode field");
            } catch (IllegalAccessException e) {
                //Timber.e(e, "Unable to change value of shift mode");
            }
        }
    }
}
