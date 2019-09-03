package com.corroy.mathieu.go4lunch.Models.Helper;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import java.util.HashMap;
import java.util.Map;

public class RestaurantHelper {

    private static final String COLLECTION_LIKED = "restaurantLike";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getLikedCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_LIKED);
    }

    // --- CREATE ---

    public static Task<Void>  createLike(String restaurantId, String userId){
        Map<String, Object> user = new HashMap<>();
        user.put(userId, true);
        return RestaurantHelper.getLikedCollection().document(restaurantId).set(user, SetOptions.merge());
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getLikeForTheRestaurant(String restaurantId){
        return RestaurantHelper.getLikedCollection().document(restaurantId).get();
    }

    // --- DELETE ---

    public static Boolean deleteLike(String restaurantId, String userId){
        RestaurantHelper.getLikeForTheRestaurant(restaurantId).addOnCompleteListener(task -> {
        if (task.isSuccessful()){
            Map<String, Object> update = new HashMap<>();
            update.put(userId, FieldValue.delete());
            RestaurantHelper.getLikedCollection().document(restaurantId).update(update);
        }
        });
        return true;
    }
}
