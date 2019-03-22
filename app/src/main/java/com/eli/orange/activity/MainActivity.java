package com.eli.orange.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.eli.orange.activity.privacyPolicy.PrivacyPolicyActivity;
import com.eli.orange.fragments.bottomSheetFragment.BottomSheetFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.eli.orange.R;
import com.eli.orange.fragments.HomeFragment.homeFragment;
import com.eli.orange.fragments.NotificationsFragment;
import com.eli.orange.fragments.AccountsFragment;
import com.eli.orange.fragments.SettingsFragment;
import com.eli.orange.fragments.TransactionsFragment;

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
        @BindView(R.id.fab)
        FloatingActionButton fab;


    @BindView(R.id.bottom_sheet)
    LinearLayout layoutBottomSheet;

    BottomSheetBehavior sheetBehavior;


    // urls to load navigation header background image
        // and profile image
        private static final String urlNavHeaderBg = "https://i0.wp.com/www.newhopeschool.org/wp-content/uploads/2017/10/2560x1440-teal-blue-solid-color-background.jpg";
        private static final String urlProfileImg = "https://cdn.pixabay.com/photo/2016/08/20/05/36/avatar-1606914__340.png";

        // index to identify current nav menu item
        public static int navItemIndex = 0;

        // tags used to attach the fragments
        private static final String TAG_HOME = "home";
        private static final String TAG_PHOTOS = "Transactions";
        private static final String TAG_MOVIES = "Accounts";
        private static final String TAG_NOTIFICATIONS = "notifications";
        private static final String TAG_SETTINGS = "settings";
        public static String CURRENT_TAG = TAG_HOME;

        // toolbar titles respected to selected nav menu item
        private String[] activityTitles;

        // flag to load home fragment when user presses back key
        private boolean shouldLoadHomeFragOnBackPress = true;
        private Handler mHandler;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            ButterKnife.bind(this);

            setSupportActionBar(toolbar);

            mHandler = new Handler();
            sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);

            sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
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

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
                    bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
                }


            });

            // load nav menu header data
            loadNavHeader();

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
            txtName.setText("Elirehema Paul");
            txtWebsite.setText(Html.fromHtml("https://twitter.com/e_paul_"));

            // loading header background image
            Glide.with(this).load(urlNavHeaderBg)
                    .into(imgNavHeaderBg);

            // Loading profile image
            Glide.with(this)
                    .load(urlProfileImg)
                    .apply(RequestOptions.circleCropTransform())
                    .thumbnail(0.5f)
                    .into(imgProfile);

            // showing dot next to notifications label
            navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);
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
                    homeFragment homeFragment = new homeFragment(this);
                    return homeFragment;
                case 1:
                    // photos
                    AccountsFragment accountsFragment = new AccountsFragment();
                    return accountsFragment;
                case 2:
                    // movies fragment
                    TransactionsFragment moviesFragment = new TransactionsFragment();
                    return moviesFragment;
                case 3:
                    // notifications fragment
                    NotificationsFragment notificationsFragment = new NotificationsFragment();
                    return notificationsFragment;

                case 4:
                    // settings fragment
                    SettingsFragment settingsFragment = new SettingsFragment();
                    return settingsFragment;
                default:
                    return new homeFragment(this);
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
                            CURRENT_TAG = TAG_NOTIFICATIONS;
                            break;
                        case R.id.nav_settings:
                            navItemIndex = 4;
                            CURRENT_TAG = TAG_SETTINGS;
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


            ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

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
                Toast.makeText(getApplicationContext(), "Logout user!", Toast.LENGTH_LONG).show();
                return true;
            }

            // user is in notifications fragment
            // and selected 'Mark all as Read'
            if (id == R.id.action_mark_all_read) {
                Toast.makeText(getApplicationContext(), "All notifications marked as read!", Toast.LENGTH_LONG).show();
            }

            // user is in notifications fragment
            // and selected 'Clear All'
            if (id == R.id.action_clear_notifications) {
                Toast.makeText(getApplicationContext(), "Clear all notifications!", Toast.LENGTH_LONG).show();
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
    }