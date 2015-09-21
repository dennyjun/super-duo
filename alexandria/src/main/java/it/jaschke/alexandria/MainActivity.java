package it.jaschke.alexandria;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import it.jaschke.alexandria.api.Callback;


public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks, Callback {

    public static final String MESSAGE_EVENT = "MESSAGE_EVENT";
    public static final String MESSAGE_KEY = "MESSAGE_EXTRA";
    public static boolean IS_TABLET = false;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment navigationDrawerFragment;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence title;
    private BroadcastReceiver messageReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IS_TABLET = isTablet();
        if (IS_TABLET) {
            setContentView(R.layout.activity_main_tablet);
        } else {
            setContentView(R.layout.activity_main);
        }

        messageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter(MESSAGE_EVENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, filter);

        navigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        title = getTitle();

        // Set up the drawer.
        navigationDrawerFragment.setUp(R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment nextFragment;
        String tag;
        switch (position) {
            default:
            case 0:
                nextFragment = new ListOfBooks();
                tag = ListOfBooks.class.getSimpleName();
                break;
            case 1:
                nextFragment = new AddBook();
                tag = AddBook.class.getSimpleName();
                break;
            case 2:
                nextFragment = new About();
                tag = About.class.getSimpleName();
                break;

        }

        fragmentManager.beginTransaction()
                .replace(R.id.container, nextFragment, tag)
                .addToBackStack((String) title)
                .commit();

        if (isTablet()) {
            if (findViewById(R.id.fullBookTitle) != null) {
                fragmentManager.beginTransaction()
                        .replace(R.id.right_container, new Fragment(), Fragment.class.getSimpleName())
                        .addToBackStack(Fragment.class.getSimpleName())
                        .commit();
            }
        }
    }

    public void setTitle(int titleId) {
        title = getString(titleId);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(title);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!navigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
        super.onDestroy();
    }

    @Override
    public void onItemSelected(String ean) {
        Bundle args = new Bundle();
        args.putString(BookDetail.EAN_KEY, ean);

        BookDetail fragment = new BookDetail();
        fragment.setArguments(args);

        int id = R.id.container;
        if (findViewById(R.id.right_container) != null) {
            id = R.id.right_container;
        }
        getSupportFragmentManager().beginTransaction()
                .replace(id, fragment, BookDetail.class.getSimpleName())
                .addToBackStack((String) title)
                .commit();
    }

    private boolean isTablet() {
        return getResources().getBoolean(R.bool.isTablet);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() < 2) {
            finish();
        } else {
            final int lastEntry = getSupportFragmentManager().getBackStackEntryCount() - 1;
            title = getSupportFragmentManager().getBackStackEntryAt(lastEntry).getName();
            if (title.toString().equals(Fragment.class.getSimpleName())) {
                getSupportFragmentManager().popBackStack();
                title = getSupportFragmentManager().getBackStackEntryAt(lastEntry - 1).getName();
            }
            navigationDrawerFragment.updateSelected(getPosition(title.toString()));
            restoreActionBar();
        }
        super.onBackPressed();

    }

    private int getPosition(final String name) {
        if (name.equals(getString(R.string.books))
                || name.equals(getString(R.string.book_detail))) {
            return 0;
        } else if (name.equals(getString(R.string.scan))
                || name.equals(getString(R.string.found_book))) {
            return 1;
        } else if (name.equals(getString(R.string.about))) {
            return 2;
        } else {
            return -1;
        }
    }

    private class MessageReceiver extends BroadcastReceiver {                                       // fix typo from MessageReciever to MessageReceiver
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra(MESSAGE_KEY) != null) {
                Toast.makeText(MainActivity.this,
                        intent.getStringExtra(MESSAGE_KEY),
                        Toast.LENGTH_LONG).show();
            }
        }
    }


}