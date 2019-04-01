package com.corroy.mathieu.go4lunch;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import butterknife.BindView;
import butterknife.OnClick;

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

    // --------------------
    // ACTIONS
    // --------------------

    @OnClick(R.id.mainActivity_google_login)
    public void onClickGoogleButton(){this.startSignInActivityForGoogle(); }
    @OnClick(R.id.mainActivity_facebook_login)
    public void onClickFacebookButton(){this.startSignInActivityForFacebook();}


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

    // - Method that handles response after SignIn Activity close
    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data){

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                showSnackBar(this.coordinatorLayout, "Connexion Success !");
            } else { // ERRORS
                if (response == null) {
                    showSnackBar(this.coordinatorLayout, "Authentification Canceled");
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackBar(this.coordinatorLayout, "No Network");
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackBar(this.coordinatorLayout, "Unknown Error");
                }
            }
        }
    }
}