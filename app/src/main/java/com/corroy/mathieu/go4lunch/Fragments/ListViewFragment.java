package com.corroy.mathieu.go4lunch.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.corroy.mathieu.go4lunch.Models.Google;
import com.corroy.mathieu.go4lunch.Models.Result;
import com.corroy.mathieu.go4lunch.R;
import com.corroy.mathieu.go4lunch.Utils.GPSTracker;
import com.corroy.mathieu.go4lunch.Utils.Go4LunchStreams;
import com.corroy.mathieu.go4lunch.Utils.ListViewAdapter;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class ListViewFragment extends Fragment {

    @BindView(R.id.listview_recyclerview)
    RecyclerView recyclerView;

    private Disposable disposable;

    private GPSTracker gpsTracker;

    private String latLng;

    private List<Result> result;

    private ListViewAdapter listViewAdapter;

    public ListViewFragment() {
        // Required empty public constructor
    }

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

        gpsTracker = new GPSTracker(getContext());

        latLng = gpsTracker.getLatitude() + "," + gpsTracker.getLongitude();

        this.configureRecyclerView();

        this.executeHttpRequestWithRetrofit();
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.disposeWhenDestroy();
    }

    private void configureRecyclerView() {
        this.result = new ArrayList<>();
        this.listViewAdapter = new ListViewAdapter(result);
        this.recyclerView.setAdapter(this.listViewAdapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
    }

    private void executeHttpRequestWithRetrofit() {
        this.disposable = Go4LunchStreams.streamFetchGooglePlaces(latLng, 50000, "restaurant").subscribeWith(new DisposableObserver<Google>() {
            @Override
            public void onNext(Google google) {
                result.addAll(google.getResults());
                Log.i("LISTVIEW API", String.valueOf(google.getResults().size()));
                Log.i("LISTVIEW API", "ON NEXT");
                Log.i("LISTVIEW LOCATION", String.valueOf(latLng));
            }

            @Override
            public void onError(Throwable e) {
                Log.i("LISTVIEW API", "ON ERROR");
            }

            @Override
            public void onComplete() {
                listViewAdapter.notifyItemRangeChanged(0, result.size());
                Log.i("LISTVIEW API", "ON COMPLETE");
            }
        });
    }

    private void disposeWhenDestroy() {
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

//    private void updateUI(List<Result> mResult) {
//        result.clear();
//        result.addAll(mResult);
//        listViewAdapter.notifyDataSetChanged();
//    }
}
