package com.corroy.mathieu.go4lunch;


import com.corroy.mathieu.go4lunch.Models.Details.Details;
import com.corroy.mathieu.go4lunch.Models.NearbySearch.Google;
import com.corroy.mathieu.go4lunch.Utils.Go4LunchStreams;

import org.junit.BeforeClass;
import org.junit.Test;

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
}