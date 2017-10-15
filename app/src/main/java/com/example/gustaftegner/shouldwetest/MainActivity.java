package com.example.gustaftegner.shouldwetest;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.Location;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gustaftegner.shouldwetest.ContentFragment;
import com.example.gustaftegner.shouldwetest.LoginClasses.WelcomeActivity;
import com.example.gustaftegner.shouldwetest.R;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ContentFragment.onFabListener, ChooseVenueFragment.onBarSelectedListener, AddReviewFragment.onPointsListener {

    public final static String TAG = MainActivity.class.getSimpleName();

    //Defining Variables
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

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
        Log.d(TAG, "onCreate mainactivity called");
        setContentView(R.layout.activity_main);

        //LoadFacebookUser();

        mCurrentUser = ParseUser.getCurrentUser();


        //ContentFragment fragment = new ContentFragment();
        MainViewFragment mainViewFragment = new MainViewFragment();
        //PlacePickerFragment fragment = new PlacePickerFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, mainViewFragment);
        fragmentTransaction.commit();


        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            Log.d(TAG, "toolbar is not null");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);

        }

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);


        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {


                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.profile:
                        //Toast.makeText(getApplicationContext(), "Inbox Selected", Toast.LENGTH_SHORT).show();

                        return true;

                    // For rest of the options we just show a toast on click

                    case R.id.venuemap:
                        ContentFragment fragment = new ContentFragment();
                        //PlacePickerFragment fragment = new PlacePickerFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frame, fragment);
                        fragmentTransaction.commit();
                        //Toast.makeText(getApplicationContext(), "Stared Selected", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.friends:
                        //Toast.makeText(getApplicationContext(), "Send Selected", Toast.LENGTH_SHORT).show();
                        FriendsFragment friendsFragment = new FriendsFragment();
                        //PlacePickerFragment fragment = new PlacePickerFragment();
                        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.frame, friendsFragment);
                        ft.commit();
                        return true;

                    default:
                        Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();


        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                setNavIcon();
            }
        });


        LoadFacebookStuff();

        ActionBar actionBar = getSupportActionBar();
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.custom_menu, null);



        int points = mCurrentUser.getInt(ParseConstants.KEY_POINTS);

            karmaText = (TextView) mCustomView.findViewById(R.id.karma_text);
            karmaText.setText("+" + String.valueOf(points));




        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);


    }
    @Override
    public void updatePoints(){
        int points = mCurrentUser.getInt(ParseConstants.KEY_POINTS);
        Log.d(TAG, String.valueOf(points));
        karmaText.setText("+" + String.valueOf(points));
    }

    public void LoadFacebookStuff(){
        Log.d(TAG, "Loading facebook stuff");
        if(AccessToken.getCurrentAccessToken() != null){
            Log.d(TAG, "accesstoken not null");

            GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                    try{
                        Log.d(TAG, "GraphRequest onCompleted");
                        String name = jsonObject.getString("name");
                        String id = jsonObject.getString("id");
                        //String birth = jsonObject.getString("birthyear");
                        //String gender = jsonObject.getString("gender");

                        Log.d(TAG, "name: " + name + " id: " + id);

                        TextView tx = (TextView) navigationView.findViewById(R.id.email);
                        tx.setText(name);

                        ProfilePictureView profilePictureView;
                        profilePictureView = (ProfilePictureView) navigationView.findViewById(R.id.profileHeaderImage);
                        String fbid = id;

                        profilePictureView.setPresetSize(ProfilePictureView.NORMAL);
                        profilePictureView.setProfileId(fbid);

                        //mCurrentUser.put(ParseConstants.KEY_FACEBOOK_ID, id);
                        //mCurrentUser.put(ParseConstants.KEY_FACEBOOK_NAME, name);
                        /**mCurrentUser.put(ParseConstants.KEY_FACEBOOK_GENDER, gender);
                        Log.d("LoadFacebook", gender);
                        mCurrentUser.put(ParseConstants.KEY_FACEBOOK_BIRTHYEAR, 1992);**/

                        //mCurrentUser.saveInBackground();
                        //Log.d(TAG, "Facebookid " + fbid);


                    }catch (JSONException e){
                        Log.d(TAG,"Error " +  e.toString());
                    }
                }
            });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,link");
            request.setParameters(parameters);
            request.executeAsync();

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

    protected void setNavIcon() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        Log.d(TAG, "SetNavIcon backstack count: " + backStackEntryCount);
        if(backStackEntryCount > 0){
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            //actionBarDrawerToggle.syncState();

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            getSupportActionBar().setHomeButtonEnabled(true);


            Log.d(TAG, "setdisplayshowhomeenabled");
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            //getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);


        }else if(backStackEntryCount == 0){
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        }
        actionBarDrawerToggle.setDrawerIndicatorEnabled(backStackEntryCount == 0);
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
                mCurrentUser = ParseUser.getCurrentUser();
                mCurrentUser.fetchInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                        if(e == null){


                        int points = mCurrentUser.getInt(ParseConstants.KEY_POINTS);
                        Log.d(TAG, "Points: " + String.valueOf(points));
                        karmaText.setText("+" + String.valueOf(points));

                        }else{
                            Log.d(TAG, e.toString());
                        }
                    }
                });


            case R.id.action_settings:
                Log.d(TAG, "settings clicked");
                return true;
            case R.id.action_logout:
                ParseUser.logOut();
                navigateToLogin();

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        Log.d(TAG, "onSupportNavigateUp");
        //onBackPressed();
        getSupportFragmentManager().popBackStack();
        return true;
    }

    public void navigateToLogin(){
        Intent signup = new Intent(this, WelcomeActivity.class);
        signup.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        signup.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(signup);
    }

    public void replaceFragment(Fragment fragment,boolean addToBackStack)
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

    @Override
    public void onFabClicked(Location location) {
        ChooseVenueFragment fragment = new ChooseVenueFragment();

        Bundle args = new Bundle();
        args.putDouble("Latitude", location.getLatitude());
        args.putDouble("Longitude", location.getLongitude());

        fragment.setArguments(args);

        replaceFragment(fragment, true);

        /**Intent intent = new Intent(this, VenuePickerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);**/


    }

    @Override
    public void onBarClicked(String placeId, String name) {
        /**
        mCurrentUser.put(ParseConstants.KEY_BAR_CHECKIN_ID, placeId);
        mCurrentUser.put(ParseConstants.KEY_BAR_CHECKIN_NAME, name);
        Toast.makeText(MainActivity.this, "Checked in at: " + name, Toast.LENGTH_SHORT).show();**/

        //Go to new fragment
        AddReviewFragment fragment = new AddReviewFragment();
        ArrayList<String> idName = new ArrayList<>();
        idName.add(placeId);
        idName.add(name);
        Bundle args = new Bundle();
        args.putStringArrayList("idname", idName);

        fragment.setArguments(args);
        //getSupportActionBar().setTitle(name);
        replaceFragment(fragment, true);


    }


}