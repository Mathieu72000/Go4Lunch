package com.corroy.mathieu.go4lunch.Fragments;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import io.reactivex.disposables.Disposable;


public abstract class BaseFragment extends Fragment {

    protected Disposable disposable;

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.disposeWhenDestroy();
    }

    protected void disposeWhenDestroy() {
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

    // ---------------
    // FIREBASE
    // ---------------

    @Nullable
    protected FirebaseUser getCurrentUser(){return FirebaseAuth.getInstance().getCurrentUser(); }
}
