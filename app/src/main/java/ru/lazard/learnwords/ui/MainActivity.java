package ru.lazard.learnwords.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import ru.lazard.learnwords.R;
import ru.lazard.learnwords.db.DbHelper;
import ru.lazard.learnwords.ui.fragments.learn.LearnFragment;
import ru.lazard.learnwords.ui.fragments.preferences.SettingsFragment;
import ru.lazard.learnwords.ui.fragments.wordsList.WordsListFragment;
import ru.lazard.learnwords.ui.navigator.BackArrowController;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private FloatingActionButton floatingActionButton;
    private NavigationView navigationView;
    private Toolbar toolbar;
public void setSelectedNavigationMenu(int resId){
    getNavigationView().getMenu().findItem(resId).setChecked(true);
}
    public void addFragment(Fragment fragment, boolean isAddToBackStack) {
        String backStateName = fragment.getClass().getName();
        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);
        if (fragmentPopped)    return;

        Fragment fragmentByTag = manager.findFragmentByTag(backStateName);

        if (fragmentByTag == null) {
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.fragment_container, fragment, backStateName);
            if (isAddToBackStack) {
                ft.addToBackStack(backStateName);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            }
            ft.commit();
            return;
        }

        //rewind to base fragment
        while(manager.popBackStackImmediate());
    }

    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    public FloatingActionButton getFloatingActionButton() {
        return floatingActionButton;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main2, menu);
//        return true;
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_slideshow) {
            addFragment(new LearnFragment(), true);
        } else if (id == R.id.nav_manage) {
            addFragment(new SettingsFragment(), true);
        } else if (id == R.id.nav_words_list) {
            addFragment(new WordsListFragment(), true);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            addFragment(new SettingsFragment(), true);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        new BackArrowController(this);

        DbHelper.init(this);
        DbHelper.getInstance().getWritableDatabase();

        addFragment(new LearnFragment(), false);



    }

    public NavigationView getNavigationView() {
        return navigationView;
    }
}