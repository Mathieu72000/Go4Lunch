package com.corroy.mathieu.go4lunch.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.corroy.mathieu.go4lunch.Controller.RestaurantActivity;
import com.corroy.mathieu.go4lunch.Models.Google;
import com.corroy.mathieu.go4lunch.Models.Result;
import com.corroy.mathieu.go4lunch.R;
import com.corroy.mathieu.go4lunch.Utils.GPSTracker;
import com.corroy.mathieu.go4lunch.Utils.Go4LunchStreams;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import io.reactivex.observers.DisposableObserver;

public class MapViewFragment extends BaseFragment implements OnMapReadyCallback, GoogleMap.OnMyLocationClickListener, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMarkerClickListener {

    private View mView;
    private SupportMapFragment mapFragment;
    private GoogleMap mGoogleMap;
    private List<Result> resultList;
    private GPSTracker mGPSTracker;
    private String position;
    private LatLng latLng;
    private String placeId;
    private String restaurant = "restaurant";

    public static MapViewFragment newInstance() {
        return new MapViewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_map_view, container, false);

        mGPSTracker = new GPSTracker(getContext());

        mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        resultList = new ArrayList<>();

        return mView;
    }

    // ------------------
    // RETROFIT
    // ------------------

    // Create a new subscriber
    private void executeHttpRequestWithRetrofit() {
        disposable = Go4LunchStreams.streamFetchGooglePlaces(position, 7000, restaurant).subscribeWith(new DisposableObserver<Google>() {
            @Override
            public void onNext(Google google) {
                resultList.addAll(google.getResults());
                updateGoogleUi();
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
    }

    // Called when the map is ready to be used
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setOnMarkerClickListener(this);
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.setOnMyLocationButtonClickListener(this);
            mGoogleMap.setOnMyLocationClickListener(this);
        } else {
            // Show rationale and request permission
        }
        latLng = new LatLng(mGPSTracker.getLatitude(), mGPSTracker.getLongitude());
        position = mGPSTracker.getLatitude() + "," + mGPSTracker.getLongitude();
        googleMap.addMarker(new MarkerOptions().position(latLng).title("Marker Title").snippet("Marker Description"));
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

//              Set the Location Button
        View myLocationButton = ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) myLocationButton.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 170, 190);

        this.executeHttpRequestWithRetrofit();
    }

    // Handle the user click to change the marker color
    @Override
    public boolean onMarkerClick(Marker marker) {
            placeId = marker.getTitle();
        Intent intent = new Intent(getContext(), RestaurantActivity.class);
//        intent.putExtra(getId(), placeId);
        startActivity(intent);
            return false;
    }

    // Send a toast when the user click on a marker
    @Override
    public void onMyLocationClick(@NonNull Location location) {
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    // Add markers for each result on Gmap
    private void updateGoogleUi(){
        int height = 90;
        int width = 90;
        BitmapDrawable bitmapDrawable = (BitmapDrawable)getResources().getDrawable(R.drawable.ic_restaurant_marker_orange);
        Bitmap bitmap = bitmapDrawable.getBitmap();
        Bitmap iconSize = Bitmap.createScaledBitmap(bitmap, width, height, false);
        for(Result mResult : resultList){
            LatLng restau = new LatLng(mResult.getGeometry().getLocation().getLat(), mResult.getGeometry().getLocation().getLng());
            Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(restau)
                    .title(mResult.getName())
                    .snippet(mResult.getVicinity())
                    .icon(BitmapDescriptorFactory.fromBitmap(iconSize)));
            marker.setTag(mResult);
        }
    }
}
