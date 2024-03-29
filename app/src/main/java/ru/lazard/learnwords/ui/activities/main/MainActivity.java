package ru.lazard.learnwords.ui.activities.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import ru.lazard.learnwords.R;
import ru.lazard.learnwords.speach.TTS;
import ru.lazard.learnwords.ui.BaseActivity;
import ru.lazard.learnwords.ui.fragments.books.readBook.ReadBookFragment;
import ru.lazard.learnwords.ui.fragments.checkWords.checkTranslate.CheckTranslateFragment;
import ru.lazard.learnwords.ui.fragments.checkWords.learn.LearnFragment;
import ru.lazard.learnwords.ui.fragments.preferences.SettingsFragment;
import ru.lazard.learnwords.ui.fragments.checkWords.repeat.RepeatWordsFragment;
import ru.lazard.learnwords.ui.fragments.checkWords.spellcheck.SpellCheckFragment;
import ru.lazard.learnwords.ui.fragments.wordsList.dictionaries.DictionariesListFragment;
import ru.lazard.learnwords.ui.fragments.wordsList.words.WordsListFragment;
import ru.lazard.learnwords.ui.navigator.BackArrowController;
import ru.lazard.learnwords.utils.Utils;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private CoordinatorLayout coordinatorLayout;
    private DrawerLayout drawerLayout;
    private FloatingActionButton floatingActionButton;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private TTS tts;

    public TTS getTts() {
        return tts;
    }

    public static void show(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    public void addFragment(Fragment fragment, boolean isAddToBackStack) {
        String backStateName = fragment.getClass().getName();
        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);
        if (fragmentPopped) return;

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

        if (fragment instanceof LearnFragment) {
            //rewind to base fragment
            while (manager.popBackStackImmediate()) {
                ;
            }
        }
    }

    public CoordinatorLayout getCoordinatorLayout() {
        return coordinatorLayout;
    }

    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    public FloatingActionButton getFloatingActionButton() {
        return floatingActionButton;
    }

    public NavigationView getNavigationView() {
        return navigationView;
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

        if (id == R.id.nav_learnWords) {
            addFragment(new LearnFragment(), true);
        } else if (id == R.id.nav_repeatWords) {
            addFragment(new RepeatWordsFragment(), true);
        } else if (id == R.id.nav_checkTranslate) {
            addFragment(new CheckTranslateFragment(), true);
        } else if (id == R.id.nav_settings) {
            addFragment(new SettingsFragment(), true);
        } else if (id == R.id.nav_spellcheck) {
            addFragment(new SpellCheckFragment(), true);
        } else if (id == R.id.nav_wordsList) {
            addFragment(new DictionariesListFragment(), true);
        } else if (id == R.id.nav_read_book) {
            addFragment(ReadBookFragment.Companion.newInstance(null,null), true);
        } else if (id == R.id.nav_rate) {
            Utils.showMarketPage(this, R.string.navigation_menu_rate_exception_marketNotExist);
        } else if (id == R.id.nav_sendEmail) {
            String subject = String.format("%s [%s]", Utils.getApplicationName(this), Utils.getApplicationVersion(this));
            Utils.sendEmail(getString(R.string.supportEmail), subject, "", this);
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

    public void setSelectedNavigationMenu(int resId) {
        getNavigationView().getMenu().findItem(resId).setChecked(true);
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
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        navigationView.setNavigationItemSelectedListener(this);

        new BackArrowController(this);

        new NavCounterManager(this);

        addFragment(new LearnFragment(), false);
        this.tts=new TTS(this);
        this.tts.checkTTS();

        new StartIntentInvoker(this).invoke();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        new StartIntentInvoker(this).invoke();
    }
}
