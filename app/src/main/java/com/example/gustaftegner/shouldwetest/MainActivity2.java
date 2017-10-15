package com.example.gustaftegner.shouldwetest;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gustaftegner.shouldwetest.LoginClasses.WelcomeActivity;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity implements PlacePickerFragment.onFabListener, ChooseVenueDialog.onBarSelectedListener {

    public final static String TAG = MainActivity2.class.getSimpleName();

    //Defining Variables
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private int[] tabIcons = {R.drawable.ic_people_black_24dp, R.drawable.ic_place_grey_24dp, R.drawable.ic_add_black_24dp};
    //Parsestuff
    private ParseUser mCurrentUser;

    //Floating action button


    //Facebook stuff

    private String facebookUserId;
    private String facebookUserName;


    TextView karmaText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Welcome to mainactivity2! Oncreate called");
        setContentView(R.layout.activity_main2);

        //LoadFacebookUser();

        mCurrentUser = ParseUser.getCurrentUser();

        /**
        //ContentFragment fragment = new ContentFragment();
        MainViewFragment mainViewFragment = new MainViewFragment();
        //PlacePickerFragment fragment = new PlacePickerFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, mainViewFragment);
        fragmentTransaction.commit();

         **/


        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        ImageView logga = (ImageView) toolbar.findViewById(R.id.toolbar_image);
        //Picasso.with(this).load(R.drawable.logowhite).centerCrop().fit().into(logga);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        setupTabIcons();




    }

    private void setupTabIcons(){
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MainViewFragment(), "ONE");
        adapter.addFragment(new PlacePickerFragment(), "TWO");
        adapter.addFragment(new FriendsFragment(), "THREE");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onFabClicked(Location location) {

        Bundle args = new Bundle();
        args.putDouble("Latitude", location.getLatitude());
        args.putDouble("Longitude", location.getLongitude());


        showDialog(args);
    }

    void showDialog(Bundle args) {

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = ChooseVenueDialog.newInstance(3);

        newFragment.setArguments(args);
        newFragment.show(ft, "dialog");
    }

    @Override
    public void onBarClicked(ShouldWeVenue venue) {
        Log.d(TAG, "Onbarclicked");
        Bundle args = new Bundle();
        args.putString("venueid", venue.getObjectId());
        showReviewDialog(args);

    }

    void showReviewDialog(Bundle args){
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = AddReviewDialog.newInstance(3);


        newFragment.setArguments(args);
        newFragment.show(ft, "dialog");
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            //
            // return mFragmentTitleList.get(position);
            return null;
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        AppEventsLogger.activateApp(this);

    }

    @Override
    public void onPause(){

        super.onPause();

        AppEventsLogger.deactivateApp(this);

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

        /**if (actionBarDrawerToggle.isDrawerIndicatorEnabled() && actionBarDrawerToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "returning true");
            return true;
        }**/
        Log.d(TAG, "MenuItem id: " + item.getItemId());
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d(TAG, "home clicked");
                onSupportNavigateUp();


                //NavUtils.navigateUpFromSameTask(this);
                //onBackPressed();
                return true;
            case R.id.action_refresh:
                Log.d(TAG, "Refresh called");
                return true;

            case R.id.action_settings:
                Log.d(TAG, "settings clicked");
                return true;
            case R.id.action_logout:
                ParseUser.logOut();
                navigateToLogin();

        }

        return super.onOptionsItemSelected(item);
    }



    public void navigateToLogin(){
        Intent signup = new Intent(this, WelcomeActivity.class);
        signup.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        signup.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(signup);
    }

    public void replaceFragment(Fragment fragment, boolean addToBackStack)
    {
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (addToBackStack)
        {
            transaction.addToBackStack(fragment.getClass().getSimpleName());

        }

        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        transaction.add(R.id.frame, fragment); //replace instead of add
        transaction.commit();
        getSupportFragmentManager().executePendingTransactions();

    }



}