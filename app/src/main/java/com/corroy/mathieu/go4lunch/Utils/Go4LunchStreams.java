package com.corroy.mathieu.go4lunch.Utils;

import com.corroy.mathieu.go4lunch.Models.Details;
import com.corroy.mathieu.go4lunch.Models.Google;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class Go4LunchStreams {

    // Google Places streams

    public static Observable<Google> streamFetchGooglePlaces(String location, int radius, String type) {
        Go4LunchService go4LunchService = Go4LunchService.retrofit.create(Go4LunchService.class);
        return go4LunchService.getGoogleRestaurant(location, radius, type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(500, TimeUnit.SECONDS);
    }

    public static Observable<Details> streamFetchGoogleDetails(String placeId) {
        Go4LunchService go4LunchService = Go4LunchService.retrofit.create(Go4LunchService.class);
        return go4LunchService.getGoogleDetails(placeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(500, TimeUnit.SECONDS);
    }
}
