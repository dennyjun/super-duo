package barqsoft.footballscores;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {
    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String MY_MAIN_FRAGMENT_KEY = "myMain";
    private static final String CURRENT_PAGER_KEY = "Pager_Current";
    private static final String SELECTED_MATCH_KEY = "Selected_match";
    public static int selectedMatchId;
    public static int currentFragment = 2;
    private final String saveTag = "Save Test";
    private PagerFragment myMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(LOG_TAG, getString(R.string.reached_mainactivity_oncreate_msg));
        if (savedInstanceState == null) {
            myMain = new PagerFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, myMain)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            final Intent startAbout = new Intent(this, AboutActivity.class);
            startActivity(startAbout);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        final int currentPager = myMain.pagerHandler.getCurrentItem();
        Log.v(saveTag, getString(R.string.will_save_msg));
        Log.v(saveTag, getString(R.string.fragment_prefix_msg) + String.valueOf(currentPager));
        Log.v(saveTag, getString(R.string.selected_id_prefix_msg) + selectedMatchId);
        outState.putInt(CURRENT_PAGER_KEY, currentPager);
        outState.putInt(SELECTED_MATCH_KEY, selectedMatchId);
        getSupportFragmentManager().putFragment(outState, MY_MAIN_FRAGMENT_KEY, myMain);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        currentFragment = savedInstanceState.getInt(CURRENT_PAGER_KEY);
        selectedMatchId = savedInstanceState.getInt(SELECTED_MATCH_KEY);
        Log.v(saveTag, getString(R.string.will_retrieve_msg));
        Log.v(saveTag, getString(R.string.fragment_prefix_msg) + String.valueOf(currentFragment));
        Log.v(saveTag, getString(R.string.selected_id_prefix_msg) + selectedMatchId);
        myMain = (PagerFragment) getSupportFragmentManager()
                .getFragment(savedInstanceState, MY_MAIN_FRAGMENT_KEY);
        super.onRestoreInstanceState(savedInstanceState);
    }
}
