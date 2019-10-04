package com.corroy.mathieu.go4lunch.Models.Helper;

import androidx.annotation.Nullable;
import com.corroy.mathieu.go4lunch.Models.Details.Result;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserHelper {

    public interface OnRequestListener{
        void onResult(List<User> userList);
    }

    private static final String COLLECTION_USERS = "users";
    private static final String COLLECTION_LIKED = "restaurantLike";
    private static final String COLLECTION_RESTAURANTID = "restaurantId";

    // --- COLLECTION REFERENCE ---

    private static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_USERS);
    }

    private static CollectionReference getLikedCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_LIKED);
    }

    // --- CREATE ---

    public static Task<Void> createUser(String uid, String username, String urlPicture) {
        // 1 - Create User object
        User userToCreate = new User(uid, username, urlPicture);

        // 2 - Add a new User Document to Firestore
        return UserHelper.getUsersCollection()
                .document(uid) // Setting uID for Document
                .set(userToCreate); // Setting object for Document
    }

    public static Task<Void>  createLike(String restaurantId, String userId){
        Map<String, Object> user = new HashMap<>();
        user.put(userId, true);
        return UserHelper.getLikedCollection().document(restaurantId).set(user, SetOptions.merge());
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getUser(String uid){
        return UserHelper.getUsersCollection().document(uid).get();
    }

    private static Task<DocumentSnapshot> getLikeForTheRestaurant(String restaurantId){
        return UserHelper.getLikedCollection().document(restaurantId).get();
    }

    public static Task<QuerySnapshot> restoreLike(String uid){
        return UserHelper.getLikedCollection().whereEqualTo(uid, true).get();
    }

    public static Task<QuerySnapshot> getRestaurant(String restaurantId){
        return UserHelper.getUsersCollection().whereEqualTo("restaurantId", restaurantId).get();
    }

    public static Task<DocumentSnapshot> getBookingRestaurant(String uid){
        return UserHelper.getUsersCollection().document(uid).get();
    }

    // --- UPDATE ---

    public static Task<Void> updateUserRestaurant(String userId, String joinedRestaurant, String restaurantId){
        return UserHelper.getUsersCollection().document(userId).update("joinedRestaurant", joinedRestaurant, "restaurantId", restaurantId);
    }

    public static Task<Void> updateUser(String uid, String username, String urlPicture){
        return UserHelper.getUsersCollection().document(uid).update("username", username, "urlPicture", urlPicture);
    }

    // ---- DELETE ---

    public static Boolean deleteLike(String restaurantId, String userId){
        UserHelper.getLikeForTheRestaurant(restaurantId).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Map<String, Object> update = new HashMap<>();
                update.put(userId, FieldValue.delete());
                UserHelper.getLikedCollection().document(restaurantId).update(update);
            }
        });
        return true;
    }

    public static Task<Void> deleteUserRestaurant(String userId){
        Map<String, Object> update = new HashMap<>();
        update.put("joinedRestaurant", FieldValue.delete());
        update.put("restaurantId", FieldValue.delete());
        return UserHelper.getUsersCollection().document(userId).update(update);
    }

    // ------------
    // FIREBASE
    // ------------

    public static void getRestaurantInfo(Result result, OnRequestListener onRequestListener){
        List<User> users = new ArrayList<>();
        FirebaseFirestore.getInstance()
                .collection(COLLECTION_USERS)
                .whereEqualTo(COLLECTION_RESTAURANTID, result.getPlaceId())
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        List<DocumentSnapshot> myListOfDocuments = task.getResult().getDocuments();
                        for(DocumentSnapshot documentSnapshot : myListOfDocuments){
                                User user = documentSnapshot.toObject(User.class);
                                user.setJoinedRestaurant(result.getName());
                                user.setRestaurantId(result.getPlaceId());
                                if(!user.getUid().equals(getCurrentUser().getUid())){
                                    users.add(user);}
                        }
                        onRequestListener.onResult(users);
                    }
                });
    }

    public static void getCoWorkers(OnRequestListener onRequestListener){
        List<User> users = new ArrayList<>();
        FirebaseFirestore.getInstance()
                .collection(COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        List<DocumentSnapshot> myListOfDocuments = task.getResult().getDocuments();
                        for(DocumentSnapshot documentSnapshot : myListOfDocuments){
                            User user = documentSnapshot.toObject(User.class);
                            if(!user.getUid().equals(getCurrentUser().getUid())){
                                users.add(user);}
                        }
                        onRequestListener.onResult(users);
                    }
                });
    }

    @Nullable
    public static FirebaseUser getCurrentUser(){return FirebaseAuth.getInstance().getCurrentUser(); }

    public static boolean isCurrentUserLogged(){return (getCurrentUser() != null); }

}
