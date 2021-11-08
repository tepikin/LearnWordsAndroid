package ru.lazard.learnwords.ui.navigator;

import android.animation.ValueAnimator;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.fragment.app.FragmentManager;
import androidx.core.view.GravityCompat;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import ru.lazard.learnwords.R;
import ru.lazard.learnwords.ui.activities.main.MainActivity;
import ru.lazard.learnwords.utils.Utils;

/**
 * Created by Egor on 03.06.2016.
 */
public class BackArrowController implements FragmentManager.OnBackStackChangedListener, View.OnClickListener {
    private final MainActivity activity;
    private ValueAnimator anim;
    private ActionBarDrawerToggle toggle;

    public BackArrowController(MainActivity activity){
        this.activity=activity;
        toggle = new ActionBarDrawerToggle(
                activity, activity.getDrawerLayout(),activity.getToolbar(), R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();
        activity.getToolbar().setNavigationOnClickListener(this);
        activity.getSupportFragmentManager().addOnBackStackChangedListener(this);

        setDrawerListener(activity);
    }

    private void setDrawerListener(MainActivity activity) {
        activity.getDrawerLayout().addDrawerListener(new androidx.drawerlayout.widget.DrawerLayout.DrawerListener(){
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                Utils.hideKeyboard(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }


    @Override
    public void onBackStackChanged() {
        if (activity.getSupportFragmentManager().getBackStackEntryCount()>0){
            open();
        }else{
            close();
        }
    }
    private float currentState =0;

    @Override
    public void onClick(View v) {
        if (activity.getSupportFragmentManager().getBackStackEntryCount()>0){
            activity.onBackPressed();
        }else{
            activity.getDrawerLayout().openDrawer(GravityCompat.START);
        }
    }

    public void open(){
        animateTo(1);
    }
    public void close(){
        animateTo(0);
    }
    private void animateTo(float value){
        if (anim!=null){
            anim.cancel();
        }
        anim = ValueAnimator.ofFloat(currentState, value);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                currentState = (Float) valueAnimator.getAnimatedValue();
                toggle.onDrawerSlide(activity.getDrawerLayout(), currentState);
            }
        });
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(500);
        anim.start();

    }
}
