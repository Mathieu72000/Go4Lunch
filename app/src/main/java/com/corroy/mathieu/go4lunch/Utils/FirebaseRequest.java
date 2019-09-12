package com.corroy.mathieu.go4lunch.Utils;

import androidx.annotation.Nullable;
import com.corroy.mathieu.go4lunch.Models.Details.Result;
import com.corroy.mathieu.go4lunch.Models.Helper.User;
import com.corroy.mathieu.go4lunch.Models.Helper.UserHelper;
import com.corroy.mathieu.go4lunch.Views.WorkmatesAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class FirebaseRequest {

    private static final String COLLECTION_NAME = "users";
    private static final String COLLECTION_FIELD = "joinedRestaurant";

    // Execute FireBase request to get the collection
    public void executeFireBaseRequestActivity(Result result, List<User> userList, WorkmatesAdapter workmatesAdapter){
        FirebaseFirestore.getInstance()
                .collection(COLLECTION_NAME)
                .whereEqualTo(COLLECTION_FIELD, result.getName())
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        List<DocumentSnapshot> myListOfDocuments = task.getResult().getDocuments();
                        for(DocumentSnapshot documentSnapshot : myListOfDocuments){
                            UserHelper.getUser(documentSnapshot.getId()).addOnSuccessListener(documentSnapshot1 -> {
                                User user = documentSnapshot1.toObject(User.class);
                                user.setJoinedRestaurant(result.getName());
                                user.setRestaurantId(result.getPlaceId());
                                if(!user.getUid().equals(getCurrentUser().getUid())){
                                    userList.add(user);}
                                workmatesAdapter.notifyDataSetChanged();
                            });
                        }
                    }
                });
    }

    public void executeFireBaseRequestFragment(List<User> userList, WorkmatesAdapter workmatesAdapter){
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

    @Nullable
    private FirebaseUser getCurrentUser(){return FirebaseAuth.getInstance().getCurrentUser(); }

}
