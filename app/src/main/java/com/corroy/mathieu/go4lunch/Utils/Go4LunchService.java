package com.corroy.mathieu.go4lunch.Utils;

import com.corroy.mathieu.go4lunch.Models.Details;
import com.corroy.mathieu.go4lunch.Models.Google;
import com.corroy.mathieu.go4lunch.Models.Metrix.Matrix;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by User on 11/04/2019.
 */
public interface Go4LunchService {

    // Create a GET Request on a URL complement

    @GET("maps/api/place/nearbysearch/json?key=AIzaSyDXI74hOiHLi4l2vhUEs23260f055xyXvI")
    Observable<Google> getGoogleRestaurant(@Query("location") String location,
                                           @Query("radius") int radius,
                                           @Query("type") String type);

    @GET("maps/api/place/details/json?key=AIzaSyDXI74hOiHLi4l2vhUEs23260f055xyXvI")
    Observable<Details> getGoogleDetails(@Query("placeid") String placeId);

    @GET("maps/api/distancematrix/json?key=AIzaSyDXI74hOiHLi4l2vhUEs23260f055xyXvI")
    Observable<Matrix> getGoogleDistance(@Query("origins") String origins,
                                         @Query("destinations") String destinations);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();

    Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
}
