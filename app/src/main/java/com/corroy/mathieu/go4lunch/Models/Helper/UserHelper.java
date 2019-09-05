package com.corroy.mathieu.go4lunch.Models.Helper;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import java.util.HashMap;
import java.util.Map;

public class UserHelper {

    private static final String COLLECTION_NAME = "users";
    private static final String COLLECTION_LIKED = "restaurantLike";

    // --- COLLECTION REFERENCE ---
    private static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
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

    // --- UPDATE ---
    public static Task<Void> updateUserRestaurant(String userId, String joinedRestaurant){
        return UserHelper.getUsersCollection().document(userId).update("joinedRestaurant", joinedRestaurant);
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
        return UserHelper.getUsersCollection().document(userId).update(update);
    }
}
