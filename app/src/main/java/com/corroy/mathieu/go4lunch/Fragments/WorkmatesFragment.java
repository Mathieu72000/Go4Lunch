package com.corroy.mathieu.go4lunch.Fragments;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.corroy.mathieu.go4lunch.Controller.FirstScreenActivity;
import com.corroy.mathieu.go4lunch.Models.User;
import com.corroy.mathieu.go4lunch.Models.UserHelper;
import com.corroy.mathieu.go4lunch.R;
import com.corroy.mathieu.go4lunch.Views.WorkmatesAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkmatesFragment extends BaseFragment {

    @BindView(R.id.fragment_workmates_recyclerview)
    RecyclerView recyclerView;

    private List<User> userList;

    private WorkmatesAdapter workmatesAdapter;

    private String COLLECTION_NAME = "users";


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
        this.executeFirebaseRequest();
        return view;
    }

    public void configureRecyclerView(){
        this.userList = new ArrayList<>();
        this.workmatesAdapter = new WorkmatesAdapter(FirstScreenActivity.userList);
        this.recyclerView.setAdapter(this.workmatesAdapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void executeFirebaseRequest(){
        FirebaseFirestore.getInstance()
                .collection(COLLECTION_NAME)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        List<DocumentSnapshot> myListOfDocuments = task.getResult().getDocuments();
                        for(DocumentSnapshot documentSnapshot : myListOfDocuments){
                            UserHelper.getUser(documentSnapshot.getId()).addOnSuccessListener(documentSnapshot1 -> {
                                User user = documentSnapshot1.toObject(User.class);
                                if(!user.getUid().equals(getCurrentUser().getUid())){
                                    userList.add(user);}
                                workmatesAdapter.notifyDataSetChanged();
                            });
                        }
                    }
                });
    }
}