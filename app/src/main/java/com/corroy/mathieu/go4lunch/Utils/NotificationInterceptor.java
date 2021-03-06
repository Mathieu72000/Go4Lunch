package com.corroy.mathieu.go4lunch.Utils;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.batch.android.BatchNotificationInterceptor;
import com.corroy.mathieu.go4lunch.Models.Helper.UserHelper;
import com.corroy.mathieu.go4lunch.R;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class NotificationInterceptor extends BatchNotificationInterceptor {

    private static final String GET_JOINED_RESTAURANT = "joinedRestaurant";
    private static final String GET_JOINED_RESTAURANT_ID = "restaurantId";
    private static final String GET_RESTAURANT_VICINITY = "vicinity";
    private static final String GET_USERNAME = "username";
    private static final String GET_USER_ID = "uid";
    private String currentUserJoinedRestaurant;
    private String restaurantVicinity;
    private String currentUserJoinedRestaurantId;
    private List<String> userList = new ArrayList<>();

    @Nullable
    @Override
    public NotificationCompat.Builder getPushNotificationCompatBuilder
            (@NonNull Context context,
             @NonNull NotificationCompat.Builder defaultBuilder,
             @NonNull Bundle pushIntentExtras,
             int notificationId) {

        userList.clear();
        currentUserJoinedRestaurantId = null;
        currentUserJoinedRestaurant = null;
        restaurantVicinity = null;

        // Get the joined restaurant name and address of the current user
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();
        Task<Void> delay = source.getTask();

        UserHelper.getBookingRestaurant(UserHelper.getCurrentUser().getUid()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                currentUserJoinedRestaurant = task.getResult().getString(GET_JOINED_RESTAURANT);
                currentUserJoinedRestaurantId = task.getResult().getString(GET_JOINED_RESTAURANT_ID);
                restaurantVicinity = task.getResult().getString(GET_RESTAURANT_VICINITY);
                source.setResult(null);
            } else {
                source.setResult(null);
            }
        });
        try {
            Tasks.await(delay);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        // Get the joined restaurant name of all the users
        TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();
        Task<Void> delayTask = taskCompletionSource.getTask();

        UserHelper.getAllUsernames().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String uid = UserHelper.getCurrentUser().getUid();
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    String coworkersJoinedRestaurant = documentSnapshot.getString(GET_JOINED_RESTAURANT_ID);
                    if (coworkersJoinedRestaurant != null) {
                        if (coworkersJoinedRestaurant.equals(currentUserJoinedRestaurantId)) {
                            String userId = documentSnapshot.getString(GET_USER_ID);
                            if (userId != null && !userId.equals(uid)) {
                                String coworkersName = documentSnapshot.getString(GET_USERNAME);
                                userList.add(coworkersName);
                            }
                        }
                    }
                }
                taskCompletionSource.setResult(null);
            } else {
                taskCompletionSource.setResult(null);
            }
        });
        try {
            Tasks.await(delayTask);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        // intercept the notification and modify it before display
        String textBrackets = userList.toString().replace("[", "").replace("]", "");

        if (currentUserJoinedRestaurant != null) {
            if (userList.isEmpty()) {
                new NotificationCompat.BigTextStyle(defaultBuilder).bigText(context.getString(R.string.notification_alone, currentUserJoinedRestaurant, restaurantVicinity));
            } else {
                new NotificationCompat.BigTextStyle(defaultBuilder).bigText(context.getString(R.string.notification, currentUserJoinedRestaurant, restaurantVicinity, textBrackets));
            }
            return defaultBuilder;
        } else {
            return null;
        }
    }
}