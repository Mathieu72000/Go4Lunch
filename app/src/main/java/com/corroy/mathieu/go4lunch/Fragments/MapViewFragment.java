package com.corroy.mathieu.go4lunch.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import com.corroy.mathieu.go4lunch.Controller.MainScreenActivity;
import com.corroy.mathieu.go4lunch.Controller.RestaurantActivity;
import com.corroy.mathieu.go4lunch.Models.Helper.User;
import com.corroy.mathieu.go4lunch.Models.Helper.UserHelper;
import com.corroy.mathieu.go4lunch.Models.NearbySearch.Google;
import com.corroy.mathieu.go4lunch.Models.NearbySearch.NearbyResult;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import io.reactivex.observers.DisposableObserver;

public class MapViewFragment extends BaseFragment implements OnMapReadyCallback, GoogleMap.OnMyLocationClickListener, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraIdleListener {

    private SupportMapFragment mapFragment;
    private GoogleMap mGoogleMap;
    private List<NearbyResult> nearbyResultList;
    private GPSTracker mGPSTracker;
    private String position;
    private AutoCompleteTextView autoCompleteTextView;
    private ArrayAdapter<String> adapter;
    private MarkerOptions markerOptions;
    private int height = 90;
    private int width = 90;
    private boolean isFirstLifeCycle = true;

    public static MapViewFragment newInstance() {
        return new MapViewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_map_view, container, false);

        mGPSTracker = new GPSTracker(getContext());

        mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        nearbyResultList = new ArrayList<>();

        markerOptions = new MarkerOptions();

        // AUTOCOMPLETE TEXTVIEW

        Toolbar toolbar = mView.findViewById(R.id.first_screen_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        autoCompleteTextView = getActivity().findViewById(R.id.autoCompleteTextView);

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                    configureAutoPredictions(s);
                    //todo refresh marker
                }
            });

            autoCompleteTextView.setOnItemClickListener((parent, view1, position, id) -> {
                String item = adapter.getItem(position);
                List<NearbyResult> nearbyResultListFilter = new ArrayList<>();
                for (NearbyResult nearbyResult : nearbyResultList) {
                    if (nearbyResult.getName().equals(item)) {
                        nearbyResultListFilter.add(nearbyResult);
                    }
                }
                // todo Refresh marker

            });
            return mView;
        }

        @Override
        public void onResume(){
        super.onResume();
        if(isFirstLifeCycle == false) {
            mGoogleMap.clear();
            updateGoogleUi();
        }
    }



    // ------------------
    // RETROFIT
    // ------------------

    // Create a new subscriber
    private void executeHttpRequestWithRetrofit() {
        disposable = Go4LunchStreams.getInstance().streamFetchGooglePlaces(position, 7000, RESTAURANT).subscribeWith(new DisposableObserver<Google>() {
            @Override
            public void onNext(Google google) {
                nearbyResultList.addAll(google.getResults());
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
            mGoogleMap.setOnCameraIdleListener(this);
            }
        LatLng latLng = new LatLng(mGPSTracker.getLatitude(), mGPSTracker.getLongitude());
        position = mGPSTracker.getLatitude() + "," + mGPSTracker.getLongitude();

        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        //Set the Location Button
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
        Intent intent = new Intent(getContext(), RestaurantActivity.class);
        NearbyResult tag = (NearbyResult) marker.getTag();
        intent.putExtra(ID, tag.getPlaceId());
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
    private void updateGoogleUi() {
        isFirstLifeCycle = false;
        for (NearbyResult mResult : nearbyResultList) {
            LatLng restaurant = new LatLng(mResult.getGeometry().getLocation().getLat(), mResult.getGeometry().getLocation().getLng());

            // todo retravailler la méthode pour récupérer l'ensemble des utilisateurs
            UserHelper.getRestaurant(mResult.getPlaceId()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    markerOptions.position(restaurant);
                    markerOptions.title(mResult.getName());
                    markerOptions.snippet(mResult.getVicinity());
                    if(task.getResult().isEmpty()){
                        BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_restaurant_marker_orange);
                        Bitmap bitmap = bitmapDrawable.getBitmap();
                        Bitmap iconSize = Bitmap.createScaledBitmap(bitmap, width, height, false);
                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(iconSize));
                    } else {
                        BitmapDrawable bitmapDrawable1 = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_restaurant_marker_green);
                        Bitmap bitmap1 = bitmapDrawable1.getBitmap();
                        Bitmap iconSize1 = Bitmap.createScaledBitmap(bitmap1, width, height, false);
                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(iconSize1));
                    }
                    Marker marker = mGoogleMap.addMarker(markerOptions);
                    marker.setTag(mResult);
                }
            });
        }
    }

    // Call when the camera has ended
    // Get the latLngBounds from the camera and set it into a MainScreenActivity variable
    @Override
    public void onCameraIdle() {
        ((MainScreenActivity) getActivity()).setLatLngBounds(mGoogleMap.getProjection().getVisibleRegion().latLngBounds);
    }

    private void configureAutoPredictions(Editable s) {
        PlacesClient placesClient = Places.createClient(getContext());

        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        LatLngBounds latLngBounds = ((MainScreenActivity) getActivity()).getLatLngBounds();
        RectangularBounds bounds = RectangularBounds.newInstance(latLngBounds.southwest, latLngBounds.northeast);

        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setLocationRestriction(bounds)
                .setCountry("fr")
                .setQuery(s.toString())
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setSessionToken(token)
                .build();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            List<String> restaurantList = new ArrayList<>();

            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                if (prediction.getPlaceTypes().contains(Place.Type.RESTAURANT)) {

                    restaurantList.add(prediction.getPrimaryText(null).toString());

                }
            }
            adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, restaurantList);

            autoCompleteTextView.setAdapter(adapter);
        });
    }
}
