package com.example.countingshakes.view;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.countingshakes.R;
import com.example.countingshakes.view.fragments.RankingFragment;
import com.example.countingshakes.view.fragments.ShakeFragment;
import com.example.countingshakes.view.fragments.SignInFragment;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.countingshakes.utils.Utils.TAG;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private int navDrawerCurrentItem;
    private NavigationView navigationView;
    private TextView txtUsername;
    private TextView txtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize FB sdk
        FacebookSdk.sdkInitialize(getApplicationContext());


        //Set the action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                checkFacebookSignIn();

            }

        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //Get the navigation drawer and sets the listener
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //Set the current selection to ShakeFragment because is the default
        navDrawerCurrentItem = R.id.nav_shake;

        //First we need to get the header of the nav drawer in order to get its elements
        final View headerView = navigationView.getHeaderView(0);
        //ImageView imgProfile = (ImageView) findViewById(R.id.profile_image);
        txtUsername = (TextView) headerView.findViewById(R.id.txt_username);
        txtEmail = (TextView) headerView.findViewById(R.id.txt_email);

        //Set the ShakeFragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment shakeFragment = new ShakeFragment();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, shakeFragment).commit();


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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id != navDrawerCurrentItem) {
            navDrawerCurrentItem = id;
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment selectedFragment = null;
            if (id == R.id.nav_shake) {
                selectedFragment = new ShakeFragment();
            } else if (id == R.id.nav_ranking) {
                selectedFragment = new RankingFragment();
            } else if (id == R.id.nav_profile) {
                selectedFragment = new SignInFragment();
            }
            fragmentManager.beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void updateNavDrawerHeader(String username, String email) {
        txtUsername.setText(username);
        txtEmail.setText(email);
    }

    /**
     * Method that validates if there exists a valid Facebook Access Token
     * in order to update the header of the nav drawer
     */
    private void checkFacebookSignIn() {
        Log.d(TAG, "checkFacebookSignIn: ");
        AccessToken currentAccessToken = AccessToken.getCurrentAccessToken();

        Log.d(TAG, "currentAccessToken >> " + currentAccessToken);
        if (currentAccessToken != null) {
            Log.d(TAG, "checkFacebookSignIn: si hay sign in");
            GraphRequest request = GraphRequest.newMeRequest(
                    currentAccessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {
                            //Call to Graph API in order to get the user information
                            String email = "";
                            String username = "";
                            try {
                                email = (String) response.getJSONObject().get("email");
                                username = (String) response.getJSONObject().get("name");
                            } catch (JSONException e) {
                                Log.e(TAG, "onCompleted: JSONException", e);
                            }
                            updateNavDrawerHeader(username, email);
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "name,email");
            request.setParameters(parameters);
            request.executeAsync();
        } else {
            updateNavDrawerHeader("Guest", "");
        }
    }


}
