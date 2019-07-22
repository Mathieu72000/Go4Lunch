package com.corroy.mathieu.go4lunch.Fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.corroy.mathieu.go4lunch.Models.Google;
import com.corroy.mathieu.go4lunch.Models.Result;
import com.corroy.mathieu.go4lunch.R;
import com.corroy.mathieu.go4lunch.Controller.RestaurantActivity;
import com.corroy.mathieu.go4lunch.Utils.GPSTracker;
import com.corroy.mathieu.go4lunch.Utils.Go4LunchStreams;
import com.corroy.mathieu.go4lunch.Utils.ItemClickSupport;
import com.corroy.mathieu.go4lunch.Views.ListViewAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.observers.DisposableObserver;

public class ListViewFragment extends BaseFragment {

    @BindView(R.id.listview_recyclerview)
    RecyclerView recyclerView;

    private GPSTracker gpsTracker;
    private String latLng;
    private List<Result> result;
    private ListViewAdapter listViewAdapter;
    private String setId = "ID";
    private String setPicture = "PICTURE";
    private String restaurant = "restaurant";

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
        gpsTracker = new GPSTracker(getContext());
        latLng = gpsTracker.getLatitude() + "," + gpsTracker.getLongitude();

        this.configureRecyclerView();
        this.configureOnClickRecyclerView();
        this.executeHttpRequestWithRetrofit();
        // Inflate the layout for this fragment
        return view;
    }

    private void configureRecyclerView() {
        this.result = new ArrayList<>();
        this.listViewAdapter = new ListViewAdapter(getContext(), result, latLng);
        this.recyclerView.setAdapter(this.listViewAdapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
    }

    private void configureOnClickRecyclerView(){
        ItemClickSupport.addTo(recyclerView)
                .setOnItemClickListener((recyclerView, position, v) -> {
                    String placeId = result.get(position).getPlaceId();
                    Intent restaurantActivity = new Intent(getContext(), RestaurantActivity.class);
                    restaurantActivity.putExtra(setId, placeId);
                    if(result.get(position).getPhotos() != null) {
                        restaurantActivity.putExtra(setPicture, result.get(position).getPhotos().get(0).getPhotoReference());
                    }
                    Objects.requireNonNull(getContext()).startActivity(restaurantActivity);
                });
    }

    private void executeHttpRequestWithRetrofit() {
        disposable = Go4LunchStreams.streamFetchGooglePlaces(latLng, 7000, restaurant).subscribeWith(new DisposableObserver<Google>() {
            @Override
            public void onNext(Google google) {
                result.addAll(google.getResults());
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
                listViewAdapter.notifyItemRangeChanged(0, result.size());
            }
        });
    }
}
