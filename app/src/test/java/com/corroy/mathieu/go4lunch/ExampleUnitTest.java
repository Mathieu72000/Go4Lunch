package com.corroy.mathieu.go4lunch;


import com.corroy.mathieu.go4lunch.Models.Details.Details;
import com.corroy.mathieu.go4lunch.Models.Helper.UserHelper;
import com.corroy.mathieu.go4lunch.Models.NearbySearch.Google;
import com.corroy.mathieu.go4lunch.Utils.Go4LunchStreams;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    private static final String GET_JOINED_RESTAURANT = "joinedRestaurant";
    private static final String GET_JOINED_RESTAURANT_ID = "restaurantId";
    private static final String GET_RESTAURANT_VICINITY = "vicinity";
    private static final String GET_USERNAME = "username";
    private static final String GET_USER_ID = "uid";
    private String currentUserJoinedRestaurant;
    private String restaurantVicinity;
    private String currentUserJoinedRestaurantId;
    private List<String> userList = new ArrayList<>();

    @BeforeClass
    public static void setupClass() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(__ -> Schedulers.trampoline());
    }

    @Test
    public void checkGooglePlacesHttpRequest() {
        Observable<Google> placesObservable = Go4LunchStreams.getInstance().streamFetchGooglePlaces("48.143488, 0.170379", 5000, "restaurant");
        TestObserver<Google> testObserver = new TestObserver<>();
        placesObservable.subscribeWith(testObserver)
                .assertNoErrors()
                .assertNoTimeout()
                .awaitTerminalEvent();

        Google google = testObserver.values().get(0);
        assertTrue(google.getResults().size() > 0);
    }

    @Test
    public void checkGoogleDetailsHttpRequest() {
        Observable<Details> detailsObservable = Go4LunchStreams.getInstance().streamFetchGoogleDetailsInfo("ChIJfZZ5c4hk4kcRjA5LWuH5sE0");
        TestObserver<Details> testObserver = new TestObserver<>();
        detailsObservable.subscribeWith(testObserver)
                .assertNoErrors()
                .assertNoTimeout()
                .awaitTerminalEvent();

        Details details = testObserver.values().get(0);
        assertNotNull(details.getResult().getPlaceId());
    }

    @Test
    public void checkFirebaseResult() {
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

        assertNotNull(currentUserJoinedRestaurant);
    }
}