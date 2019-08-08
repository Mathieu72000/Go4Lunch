package com.corroy.mathieu.go4lunch.Fragments;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import io.reactivex.disposables.Disposable;


public abstract class BaseFragment extends Fragment {

    protected Disposable disposable;
    private static final String SET_ID = "ID";
    private static final String SET_PICTURE = "PICTURE";
    private static final String restaurant = "restaurant";

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//       View mView = fragmentView(inflater, container, savedInstanceState);
//
//       return mView;
//    }
//
//    public abstract View fragmentView(LayoutInflater inflater,ViewGroup parent, Bundle savedInstanceState);

    // -----------
    // GETTERS
    // -----------

    protected static String getSetId() {
        return SET_ID;
    }

    protected static String getSetPicture() {
        return SET_PICTURE;
    }

    protected static String getRestaurant() {
        return restaurant;
    }

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
