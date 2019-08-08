package com.corroy.mathieu.go4lunch.Utils;

import com.corroy.mathieu.go4lunch.Models.AutoComplete.AutoComplete;
import com.corroy.mathieu.go4lunch.Models.Details;
import com.corroy.mathieu.go4lunch.Models.Google;
import com.corroy.mathieu.go4lunch.Models.Result;
import java.util.List;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class Go4LunchStreams {

    private static Go4LunchStreams go4LunchStreams;

    private Go4LunchStreams(){}

    // Google Places streams

    public Observable<Google> streamFetchGooglePlaces(String location, int radius, String type) {
        Go4LunchService go4LunchService = retrofit.create(Go4LunchService.class);
        return go4LunchService.getGoogleRestaurant(location, radius, type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(500, TimeUnit.SECONDS);
    }

    public Observable<Details> streamFetchGoogleDetails(String placeId) {
        Go4LunchService go4LunchService = retrofit.create(Go4LunchService.class);
        return go4LunchService.getGoogleDetails(placeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(500, TimeUnit.SECONDS);
    }

    public Observable<List<Result>> streamFetchAutoCompleteInfo(String input, String location, int radius){
        Go4LunchService go4LunchService = retrofit.create(Go4LunchService.class);
        return go4LunchService.getPlaceAutoComplete(input, location, radius)
                .flatMapIterable(AutoComplete::getPredictions)
                .flatMap(info -> go4LunchService.getGoogleDetails(info.getPlaceId()))
                .map(Details::getResult)
                .toList()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(500, TimeUnit.SECONDS);
    }

    public static Go4LunchStreams getInstance(){
        if(go4LunchStreams == null){
            synchronized (Go4LunchStreams.class){
                if(go4LunchStreams == null)
                    go4LunchStreams = new Go4LunchStreams();
                }
            }
        return go4LunchStreams;
        }

    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();
    }
