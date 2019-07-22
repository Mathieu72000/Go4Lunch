package com.corroy.mathieu.go4lunch.Controller;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.widget.Toast;
import com.corroy.mathieu.go4lunch.Models.UserHelper;
import com.corroy.mathieu.go4lunch.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;
import java.util.Arrays;
import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseActivity {

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_main;
    }

    // FOR DESIGN
    // - Get Coordinator Layout
    @BindView(R.id.mainActivity_CoordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    //FOR DATA
    // - Identifier for Sign-In Activity
    private static final int RC_SIGN_IN = 123;
    private final int REQUEST_LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestLocationPermission();

        if(this.isCurrentUserLogged()) {
            this.startActivityIfLogged();
        }
    }

    // --------------------
    // ACTIONS
    // --------------------

    @OnClick(R.id.mainActivity_google_login)
    public void onClickGoogleButton() {
        if (this.isCurrentUserLogged()) {
            this.startActivityIfLogged();
        } else {
            this.startSignInActivityForGoogle();
        }
    }

    // Launch Sign-in
    @OnClick(R.id.mainActivity_facebook_login)
    public void onClickFacebookButton() {
        if (this.isCurrentUserLogged()) {
            this.startActivityIfLogged();
        } else {
            this.startSignInActivityForFacebook();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // - Handle SignIn Activity response on activity result
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    // --------------------
    // NAVIGATION
    // --------------------

    // - Launch Sign-In Activity for Google
    private void startSignInActivityForGoogle(){
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.GoogleBuilder().build())) //GOOGLE
                        .setIsSmartLockEnabled(false, true)
                        .build(), RC_SIGN_IN);
    }

    // - Launch Sign-In Activity for Facebook
    private void startSignInActivityForFacebook(){
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.FacebookBuilder().build())) //GOOGLE
                        .setIsSmartLockEnabled(false, true)
                        .build(), RC_SIGN_IN);
    }

    // --------------------
    // UI
    // --------------------

    // - Show Snack Bar with a message
    private void showSnackBar(CoordinatorLayout coordinatorLayout, String message){
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    // --------------------
    // UTILS
    // --------------------

    private void startActivityIfLogged(){
            Intent intent = new Intent(this, FirstScreenActivity.class);
            startActivity(intent);
    }

    private void createUserFirestore(){
        if(this.getCurrentUser() != null){
            String urlPicture = (this.getCurrentUser().getPhotoUrl() != null) ? this.getCurrentUser().getPhotoUrl().toString() : null;
            String username = this.getCurrentUser().getDisplayName();
            String email = this.getCurrentUser().getEmail();
            String uid = this.getCurrentUser().getUid();

            UserHelper.createUser(uid, username, email, urlPicture)
                    .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_during_creating), Toast.LENGTH_SHORT).show());
        }
    }

    // - Method that handles response after SignIn Activity close
    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data){

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                showSnackBar(this.coordinatorLayout, getResources().getString(R.string.connexion_success));
                this.createUserFirestore();
                this.startActivityIfLogged();
            } else { // ERRORS
                if (response == null) {
                    showSnackBar(this.coordinatorLayout, getResources().getString(R.string.authentication_canceled));
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackBar(this.coordinatorLayout, getResources().getString(R.string.no_network));
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackBar(this.coordinatorLayout, getResources().getString(R.string.error_restaurant));
                }
            }
        }
    }

    // -------------------
    // PERMISSIONS
    // -------------------

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    public void requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if(EasyPermissions.hasPermissions(this, perms)) {
        }
        else {
            EasyPermissions.requestPermissions(this, getResources().getString(R.string.location_permission), REQUEST_LOCATION_PERMISSION, perms);
        }
    }
}
