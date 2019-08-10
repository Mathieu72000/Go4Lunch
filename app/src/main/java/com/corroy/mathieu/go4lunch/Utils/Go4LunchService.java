package com.corroy.mathieu.go4lunch.Utils;

import com.corroy.mathieu.go4lunch.Models.Details;
import com.corroy.mathieu.go4lunch.Models.Google;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Go4LunchService {

    // Create a GET Request on a URL complement

    @GET("maps/api/place/nearbysearch/json?key=AIzaSyDXI74hOiHLi4l2vhUEs23260f055xyXvI")
    Observable<Google> getGoogleRestaurant(@Query("location") String location,
                                           @Query("radius") int radius,
                                           @Query("type") String type);

    @GET("maps/api/place/details/json?key=AIzaSyDXI74hOiHLi4l2vhUEs23260f055xyXvI")
    Observable<Details> getGoogleDetails(@Query("placeid") String placeId);
}
