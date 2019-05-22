package com.eli.orange.activity;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.eli.orange.activity.privacyPolicy.PrivacyPolicyActivity;
import com.eli.orange.fragments.LicencesFragment;
import com.eli.orange.fragments.BottomSheetFragment;
import com.eli.orange.fragments.uploadsFragment;
import com.eli.orange.fragments.userProfileFragment;
import com.eli.orange.models.Upload;
import com.eli.orange.models.User;
import com.eli.orange.utils.Constants;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.eli.orange.R;
import com.eli.orange.fragments.HomeFragment.homeFragment;
import com.eli.orange.fragments.HistoryFragment;
import com.eli.orange.fragments.SettingsFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtWebsite;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.viewfab)
    FloatingActionButton fab;

    private FirebaseAuth auth;


    @BindView(R.id.bottom_sheet)
    LinearLayout layoutBottomSheet;

    BottomSheetBehavior sheetBehavior;


    // and profile image
    private static final String urlNavHeaderBg = "https://i0.wp.com/www.newhopeschool.org/wp-content/uploads/2017/10/2560x1440-teal-blue-solid-color-background.jpg";
    private static final String urlProfileImg = "https://cdn.pixabay.com/photo/2016/08/20/05/36/avatar-1606914__340.png";

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_PHOTOS = "User Profile";
    private static final String TAG_MOVIES = "Accounts";
    private static final String TAG_NOTIFICATIONS = "notifications";
    private static final String TAG_UPLOADS = "Uploads";
    private static final String TAG_SETTINGS = "settings";
    private static final String TAG_LICENCES = "Licences";
    public static String CURRENT_TAG = TAG_HOME;
    private BottomSheetFragment bottomSheetFragment =null;
    private List<Upload> uploads;

    private DatabaseReference databaseReference;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;
    private  ActionBarDrawerToggle actionBarDrawerToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        fab.setVisibility(View.GONE);


        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        toolbar.getMenu().clear();
        setSupportActionBar(toolbar);

        uploads = new ArrayList<>();
        bottomSheetFragment = new BottomSheetFragment();

        showBottomSheet();

        mHandler = new Handler();
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);

        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                bottomSheet.setMinimumHeight(20);
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                bottomSheet.setMinimumHeight(200);
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet();
            }
        });


        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        txtWebsite = (TextView) navHeader.findViewById(R.id.website);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
        fab.setVisibility(View.GONE);

        // load nav menu header data
        if (auth.getCurrentUser() != null) {
            loadNavHeader();
        }

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }
    }

    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */
    private void loadNavHeader() {
        // name, website
        databaseReference.child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    updateUI(dataSnapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        // showing dot next to notifications label
        navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);
    }
    private void showBottomSheet(){
        if (!bottomSheetFragment.isVisible()) {
            bottomSheetFragment.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppBottomSheetDialogTheme);
            final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


            if (auth.getCurrentUser() == null) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            } else if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps();
            } else {
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
            }


        }
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            // show or hide the fab button
            toggleFab();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        // show or hide the fab button
        toggleFab();

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // home
                homeFragment homeFragment = new homeFragment();
                return homeFragment;
            case 1:
                // photos
                HistoryFragment historyFragment = new HistoryFragment();
                return historyFragment;
            case 2:
                // user profile fragment
                userProfileFragment userProfileFragment = new userProfileFragment();
                return userProfileFragment;
            case 3:
                // uploads fragment
                uploadsFragment uploadsFragment = new uploadsFragment();
                return uploadsFragment;

            case 4:
                // settings fragment
                SettingsFragment settingsFragment = new SettingsFragment();
                return settingsFragment;
            case 5:
                LicencesFragment licencesFragment = new LicencesFragment();
                return licencesFragment;
            default:
                return new homeFragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.nav_photos:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_PHOTOS;
                        break;
                    case R.id.nav_movies:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_MOVIES;
                        break;
                    case R.id.nav_notifications:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_UPLOADS;
                        break;
                    case R.id.nav_settings:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_SETTINGS;
                        break;
                    case R.id.nav_licences:
                        navItemIndex = 5;
                        CURRENT_TAG = TAG_LICENCES;
                        break;
                    case R.id.nav_about_us:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_privacy_policy:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
                        drawer.closeDrawers();
                        return true;
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

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
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected
        if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.main, menu);
        }

        // when fragment is notifications, load the menu created for notifications
        if (navItemIndex == 3) {
            getMenuInflater().inflate(R.menu.notifications, menu);

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            auth.signOut();
            showsnackbar("Logged Out Successfully");
            return true;
        }

        // user is in notifications fragment
        // and selected 'Mark all as Read'
        if (id == R.id.action_mark_all_read) {
            showsnackbar("All notifications marked as read!");
        }

        // user is in notifications fragment
        // and selected 'Clear All'
        if (id == R.id.action_clear_notifications) {
            showsnackbar("Clear all notifications!");
        }

        return super.onOptionsItemSelected(item);
    }

    // show or hide the fab
    private void toggleFab() {
        if (navItemIndex == 0)
            fab.show();
        else
            fab.hide();
    }

    void showsnackbar(String message) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_LONG)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                .show();
        //Other stuff in
    }

    void updateUI(DataSnapshot dataSnapshot) {
        User user = dataSnapshot.getValue(User.class);
        txtName.setText(user.getUsername());
        txtWebsite.setText(Html.fromHtml(user.getEmail()));

        // loading header background image
        Glide.with(getBaseContext()).load(urlNavHeaderBg)
                .into(imgNavHeaderBg);

        // Loading profile image
        Glide.with(getBaseContext())
                .load(user.getUserImageUrl())
                .apply(RequestOptions.circleCropTransform())
                .thumbnail(0.5f)
                .into(imgProfile);
    }

    public void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setTitle("Location Updates")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


}