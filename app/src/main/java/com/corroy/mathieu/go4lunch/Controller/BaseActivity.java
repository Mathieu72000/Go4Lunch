package com.corroy.mathieu.go4lunch.Controller;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    // --------------------
    // LIFE CYCLE
    // --------------------

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(this.getFragmentLayout());
        ButterKnife.bind(this); //Configure Butterknife
    }

    public abstract int getFragmentLayout();

    // ---------------
    // UTILS
    // ---------------

    @Nullable
    protected FirebaseUser getCurrentUser(){return FirebaseAuth.getInstance().getCurrentUser(); }

    protected Boolean isCurrentUserLogged(){return (this.getCurrentUser() != null); }
}
