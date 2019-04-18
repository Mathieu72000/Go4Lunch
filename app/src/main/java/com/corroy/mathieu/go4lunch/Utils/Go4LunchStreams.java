package com.corroy.mathieu.go4lunch.Utils;

import com.corroy.mathieu.go4lunch.Models.Google;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by User on 11/04/2019.
 */
public class Go4LunchStreams {

    // Google Places streams

    public static Observable<Google> streamFetchGooglePlaces(String location, int radius, String type){
        Go4LunchService go4LunchService = Go4LunchService.retrofit.create(Go4LunchService.class);
        return go4LunchService.getGoogleRestaurant(location, radius, type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }
}
