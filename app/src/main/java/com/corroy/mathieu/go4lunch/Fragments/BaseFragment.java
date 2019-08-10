package com.corroy.mathieu.go4lunch.Fragments;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import io.reactivex.disposables.Disposable;


public abstract class BaseFragment extends Fragment {

    protected Disposable disposable;
    public static final String ID = "ID";
    public static final String PICTURE = "PICTURE";
    public static final String RESTAURANT = "restaurant";

    // ---------------
    // FIREBASE
    // ---------------

    @Nullable
    protected FirebaseUser getCurrentUser(){return FirebaseAuth.getInstance().getCurrentUser(); }

    // ----------------------------------------------------------

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.disposeWhenDestroy();
    }

    protected void disposeWhenDestroy() {
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }
}
