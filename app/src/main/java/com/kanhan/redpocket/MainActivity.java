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

    private TextView textFavorites;
    private TextView textSchedules;
    private TextView textMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        textFavorites = (TextView) findViewById(R.id.text_favorites);
//        textSchedules = (TextView) findViewById(R.id.text_schedules);
//        textMusic = (TextView) findViewById(R.id.text_music);

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Log.d("☆","start");
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    Fragment frag = null;
                    FragmentTransaction ft;
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        //item.setIcon(R.drawable.ic_access_time_white_24dp);
                        //item.set

                        switch (item.getItemId()) {
                            case R.id.paly:
                                frag = MenuFragment.newInstance(getString(R.string.text_play),
                                        getColorFromRes(R.color.color_home));
                                ft = getSupportFragmentManager().beginTransaction();
                                ft.add(R.id.container, frag, frag.getTag());
                                ft.commit();

                                Log.d("☆","1");

                                break;
                            case R.id.shops:
                                frag = MenuFragment.newInstance(getString(R.string.text_shops),
                                        getColorFromRes(R.color.color_notifications));
                                Log.d("☆","2");
                                ft = getSupportFragmentManager().beginTransaction();
                                ft.add(R.id.container, frag, frag.getTag());
                                ft.commit();

                                break;
                            case R.id.league:
                                frag = MenuFragment.newInstance(getString(R.string.text_league),
                                        getColorFromRes(R.color.color_home));
                                Log.d("☆","3");
                                ft = getSupportFragmentManager().beginTransaction();
                                ft.add(R.id.container, frag, frag.getTag());
                                ft.commit();
//                                textFavorites.setVisibility(View.GONE);
//                                textSchedules.setVisibility(View.GONE);
//                                textMusic.setVisibility(View.VISIBLE);
                                break;
                        }
                        return false;
                    }


                });

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
