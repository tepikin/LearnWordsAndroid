package ru.lazard.learnwords.ui.navigator;

import android.animation.ValueAnimator;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import ru.lazard.learnwords.ui.MainActivity;
import ru.lazard.learnwords.R;

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
