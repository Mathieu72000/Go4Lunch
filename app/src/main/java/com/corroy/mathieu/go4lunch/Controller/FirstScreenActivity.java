package com.corroy.mathieu.go4lunch.Controller;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.corroy.mathieu.go4lunch.Models.Helper.User;
import com.corroy.mathieu.go4lunch.Models.Helper.UserHelper;
import com.corroy.mathieu.go4lunch.R;
import com.corroy.mathieu.go4lunch.Views.PagerAdapter;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
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
    private ImageView profileImageView;
    private TextView emailTextView;
    private TextView nameTextView;
    private LatLngBounds latLngBounds;

    // FOR DATA
    private static final int SIGN_OUT_TASK = 10;

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
    // CONFIGURATION
    // -------------------

    // Handle Navigation View Click
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.yourLunch:
                break;
            case R.id.settings:
                break;
            case R.id.logout:
                this.signOutUserFromFireBase();
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
        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.im_hungry));
    }

    // Configure DrawerLayout
    private void configureDrawerLayout() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
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
                        .apply(RequestOptions.circleCropTransform())
                        .into(profileImageView);
            }

            // Get email & username
            String email = TextUtils.isEmpty(this.getCurrentUser().getEmail()) ?
                    getResources().getString(R.string.no_email_found) : this.getCurrentUser().getEmail();
            this.emailTextView.setText(email);

            UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(documentSnapshot -> {
                User currentUser = documentSnapshot.toObject(User.class);
                assert currentUser != null;
                String username = TextUtils.isEmpty(currentUser.getUsername()) ?
                        getResources().getString(R.string.no_username_found) : currentUser.getUsername();
                nameTextView.setText(username);
            });
                }
        }

    // -----------------
    // REST REQUEST
    // -----------------

    // Create http requests for SignOut

    private void signOutUserFromFireBase() {
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
            if (origin == SIGN_OUT_TASK) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        };
    }

    public LatLngBounds getLatLngBounds() {
        return latLngBounds;
    }

    public void setLatLngBounds(LatLngBounds latLngBounds) {
        this.latLngBounds = latLngBounds;
    }
}