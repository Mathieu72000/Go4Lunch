package com.corroy.mathieu.go4lunch.Fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.corroy.mathieu.go4lunch.Models.Google;
import com.corroy.mathieu.go4lunch.Models.Result;
import com.corroy.mathieu.go4lunch.R;
import com.corroy.mathieu.go4lunch.Utils.GPSTracker;
import com.corroy.mathieu.go4lunch.Utils.Go4LunchStreams;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class MapViewFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMyLocationClickListener, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMarkerClickListener{

    private MapView mMapView;
    private View mView;
    private GoogleMap mGoogleMap;
    private Disposable mDisposable;
    private List<Result> result;
    private GPSTracker mGPSTracker;
    private String position;
    private LatLng latLng;

    public MapViewFragment() {
        // Required empty public constructor
    }

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
        return mView;
    }

    // Called immediately after onCreateView
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mGPSTracker = new GPSTracker(getContext());

        // Instantiate map
        mMapView = mView.findViewById(R.id.mapview);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
            result = new ArrayList<>();
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onPause(){
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onStop(){
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onLowMemory(){
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        this.disposeWhenDestroy();
    }

    // Called when the map is ready to be used
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setOnMarkerClickListener(this);
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.setOnMyLocationButtonClickListener(this);
                mGoogleMap.setOnMyLocationClickListener(this);
            } else {
                // Show rationale and request permission
                }
            }
                latLng = new LatLng(mGPSTracker.getLatitude(), mGPSTracker.getLongitude());
                position = mGPSTracker.getLatitude() + "," + mGPSTracker.getLongitude();
                googleMap.addMarker(new MarkerOptions().position(latLng).title("Marker Title").snippet("Marker Description"));
                Toast.makeText(getContext(), position, Toast.LENGTH_LONG).show();
                CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                // Set the Location Button
                View myLocationButton = ((View) mMapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) myLocationButton.getLayoutParams();
                rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                rlp.setMargins(0, 0, 170, 190);

        this.executeHttpRequestWithRetrofit();
    }

    // Handle the user click to change the marker color
    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant_marker_green));
        return false;
    }

    // Send a toast when the user click on a marker
    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(getContext(), "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(getContext(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false;
    }

    // ------------------
    // RETROFIT
    // ------------------

    // Create a new subscriber
    private void executeHttpRequestWithRetrofit(){
        this.mDisposable = Go4LunchStreams.streamFetchGooglePlaces(position, 15000, "restaurant").subscribeWith(new DisposableObserver<Google>() {
            @Override
            public void onNext(Google google) {
                result.addAll(google.getResults());
                Log.i("GOOGLE API", String.valueOf(google.getResults().size()));
                Log.i("GOOGLE API", "ON NEXT");
                Log.i("LOCATION", String.valueOf(position));

            }

            @Override
            public void onError(Throwable e) {
                Log.i("GOOGLE API", e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.i("GOOGLE API", "ON COMPLETE");
                updateGoogleUi();
            }
        });
    }

    // Dispose subscription
    private void disposeWhenDestroy(){
        if (this.mDisposable != null && !this.mDisposable.isDisposed()) this.mDisposable.dispose();
    }

    private void updateGoogleUi(){
        Drawable draw = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_restaurant_marker_orange, null);
        Bitmap marker = ((BitmapDrawable)draw).getBitmap();
        for(Result mResult : result){
            LatLng restau = new LatLng(mResult.getGeometry().getLocation().getLat(), mResult.getGeometry().getLocation().getLng());
            mGoogleMap.addMarker(new MarkerOptions().position(restau).title(mResult.getName()).snippet(mResult.getVicinity())
                    .icon(BitmapDescriptorFactory.fromBitmap(marker)));
        }
    }
}
