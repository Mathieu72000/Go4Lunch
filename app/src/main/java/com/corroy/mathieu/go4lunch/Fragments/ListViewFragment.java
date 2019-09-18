package com.corroy.mathieu.go4lunch.Fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import com.corroy.mathieu.go4lunch.Controller.MainScreenActivity;
import com.corroy.mathieu.go4lunch.Models.Helper.User;
import com.corroy.mathieu.go4lunch.Models.Helper.UserHelper;
import com.corroy.mathieu.go4lunch.Models.NearbySearch.Google;
import com.corroy.mathieu.go4lunch.Models.NearbySearch.NearbyResult;
import com.corroy.mathieu.go4lunch.R;
import com.corroy.mathieu.go4lunch.Controller.RestaurantActivity;
import com.corroy.mathieu.go4lunch.Utils.GPSTracker;
import com.corroy.mathieu.go4lunch.Utils.Go4LunchStreams;
import com.corroy.mathieu.go4lunch.Utils.ItemClickSupport;
import com.corroy.mathieu.go4lunch.Views.RestaurantsAdapter;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.observers.DisposableObserver;

public class ListViewFragment extends BaseFragment {

    @BindView(R.id.listview_recyclerview)
    RecyclerView recyclerView;
    private String position;
    private List<NearbyResult> nearbyResultList;
    private RestaurantsAdapter listViewAdapter;
    private AutoCompleteTextView autoCompleteTextView;
    private ArrayAdapter<String> adapter;

    public static ListViewFragment newInstance() {
        return new ListViewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_view, container, false);
        ButterKnife.bind(this, view);

        // LOCATION
        GPSTracker gpsTracker = new GPSTracker(getContext());
        position = gpsTracker.getLatitude() + "," + gpsTracker.getLongitude();

        this.configureRecyclerView();
        this.configureOnClickRecyclerView();
        this.executeHttpRequestWithRetrofit();
        setHasOptionsMenu(true);

        Toolbar toolbar = view.findViewById(R.id.first_screen_toolbar);

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
                if(s.length() > 0){
                    configureAutoPredictions(s);
                } else {
                    listViewAdapter.refreshAdapter(nearbyResultList);
                }

            }
        });

        autoCompleteTextView.setOnItemClickListener((parent, view1, position, id) -> {
            String item = adapter.getItem(position);
            List<NearbyResult> nearbyResultListFilter = new ArrayList<>();
            for (NearbyResult nearbyResult : nearbyResultList) {
                if(nearbyResult.getName().equals(item)){
                    nearbyResultListFilter.add(nearbyResult);
                }
            }
            listViewAdapter.refreshAdapter(nearbyResultListFilter);
        });
        return view;
    }

    private void configureRecyclerView() {
        this.nearbyResultList = new ArrayList<>();
        this.listViewAdapter = new RestaurantsAdapter(getContext(), nearbyResultList, position);
        this.recyclerView.setAdapter(this.listViewAdapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
    }

    private void configureOnClickRecyclerView() {
        ItemClickSupport.addTo(recyclerView)
                .setOnItemClickListener((recyclerView, position, v) -> {
                    String placeId = listViewAdapter.getmResultList().get(position).getPlaceId();
                    Intent restaurantActivity = new Intent(getContext(), RestaurantActivity.class);
                    restaurantActivity.putExtra(ID, placeId);
                    if (listViewAdapter.getmResultList().get(position).getPhotos() != null) {
                        restaurantActivity.putExtra(PICTURE, listViewAdapter.getmResultList().get(position).getPhotos().get(0).getPhotoReference());
                    }
                    Objects.requireNonNull(getContext()).startActivity(restaurantActivity);
                });
    }

    private void executeHttpRequestWithRetrofit() {
        disposable = Go4LunchStreams.getInstance().streamFetchGooglePlaces(position, 7000, RESTAURANT).subscribeWith(new DisposableObserver<Google>() {
            @Override
            public void onNext(Google google) {
                nearbyResultList.addAll(google.getResults());
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
                listViewAdapter.notifyItemRangeChanged(0, nearbyResultList.size());

            }
        });
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
