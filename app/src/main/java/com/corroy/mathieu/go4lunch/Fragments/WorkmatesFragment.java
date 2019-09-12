package com.corroy.mathieu.go4lunch.Fragments;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.corroy.mathieu.go4lunch.Models.Helper.User;
import com.corroy.mathieu.go4lunch.R;
import com.corroy.mathieu.go4lunch.Utils.FirebaseRequest;
import com.corroy.mathieu.go4lunch.Views.WorkmatesAdapter;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkmatesFragment extends BaseFragment {

    @BindView(R.id.fragment_workmates_recyclerview)
    RecyclerView recyclerView;

    private List<User> userList;
    private WorkmatesAdapter workmatesAdapter;

    public static WorkmatesFragment newInstance() {
      return  new WorkmatesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_workmates, container, false);
        ButterKnife.bind(this, view);
        this.configureRecyclerView();
        FirebaseRequest firebaseRequest = new FirebaseRequest();
        firebaseRequest.executeFireBaseRequestFragment(userList, workmatesAdapter);
        return view;
    }

    private void configureRecyclerView(){
        this.userList = new ArrayList<>();
        this.workmatesAdapter = new WorkmatesAdapter(userList);
        this.recyclerView.setAdapter(this.workmatesAdapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}