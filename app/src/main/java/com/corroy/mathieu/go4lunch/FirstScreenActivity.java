package com.corroy.mathieu.go4lunch;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.corroy.mathieu.go4lunch.Views.PagerAdapter;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import butterknife.BindView;


public class FirstScreenActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    // FOR DESIGN
    @BindView(R.id.first_screen_toolbar)
    Toolbar toolbar;
    @BindView(R.id.first_screen_drawerlayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.first_screen_viewpager)
    ViewPager viewPager;
    @BindView(R.id.first_screen_bottom_navigation)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.first_screen_navigation_view)
    NavigationView navigationView;
    public ImageView profileImageView;
    public TextView emailTextView;
    public TextView nameTextView;

    // FOR DATA
    private static final int SIGN_OUT_TASK = 10;
    FirebaseAuth admin;

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_first_screen;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.configureToolbar();

        this.configureNavigationView();

        this.configureBottomNavigationView();

        this.configureDrawerLayout();

        View view = navigationView.getHeaderView(0);
        profileImageView = view.findViewById(R.id.header_profile_picture);
        emailTextView = view.findViewById(R.id.header_email);
        nameTextView = view.findViewById(R.id.header_name);

        this.updateUIWhenCreating();

        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
    }

    // -------------------
    // ACTIONS
    // -------------------

    public void onBackPressed() {
        super.onBackPressed();
        this.signOutUserFromFirebase();
    }

    // -------------------
    // CONFIGURATION
    // -------------------

    // Handle Navigation View Click
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.yourLunch:
                Toast.makeText(this, "Encoding YourLunch", Toast.LENGTH_SHORT).show();
                break;
            case R.id.settings:
                Toast.makeText(this, "Encoding Settings", Toast.LENGTH_SHORT).show();
                break;
            case R.id.logout:
                this.signOutUserFromFirebase();
                break;
            default:
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    // Handle Bottom Navigation View Click
    private void configureBottomNavigationView() {
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {

            int id = menuItem.getItemId();

            // Set current location in the ViewPager to handle the position of the fragments
            switch (id) {
                case R.id.map_view:
                    viewPager.setCurrentItem(0);
                    break;
                case R.id.list_view:
                    viewPager.setCurrentItem(1);
                    break;
                case R.id.workmates:
                    viewPager.setCurrentItem(2);
                    break;
                default:
                    viewPager.setCurrentItem(0);
                    break;
            }
            return true;
        });
    }

    // Configure Toolbar
    private void configureToolbar() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("I'm Hungry!");
    }

    // Configure DrawerLayout
    private void configureDrawerLayout() {
        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toogle);
        toogle.syncState();
    }

    // Configure NavigationView
    private void configureNavigationView() {
        navigationView.setNavigationItemSelectedListener(this);
    }

    //     Update UI when activity is creating
    private void updateUIWhenCreating() {

        if (this.getCurrentUser() != null) {

            // Get picture URL from FireBase
            if (this.getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(this.getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.centerCropTransform())
                        .into(profileImageView);
            }

            // Get email & username
            String email = TextUtils.isEmpty(this.getCurrentUser().getEmail()) ?
                    "No Email Found" : this.getCurrentUser().getEmail();
            this.emailTextView.setText(email);

            String username = TextUtils.isEmpty(this.getCurrentUser().getDisplayName()) ?
                    "No Username Found" : this.getCurrentUser().getDisplayName();
            this.nameTextView.setText(username);
        }
    }

    // -----------------
    // REST REQUEST
    // -----------------

    // Create http requests for SignOut

    private void signOutUserFromFirebase() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRequestCompleted(SIGN_OUT_TASK));
    }

    // -------------
    // UI
    // -------------

    // Create OnCompleteListener called after tasks ended
    private OnSuccessListener<Void> updateUIAfterRequestCompleted(final int origin) {
        return aVoid -> {
            switch (origin) {
                case SIGN_OUT_TASK:
                    finish();
                    break;
                default:
                    break;
            }
        };
    }
}