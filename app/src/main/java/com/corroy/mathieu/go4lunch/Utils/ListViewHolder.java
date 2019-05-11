package com.corroy.mathieu.go4lunch.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.corroy.mathieu.go4lunch.Fragments.MapViewFragment;
import com.corroy.mathieu.go4lunch.Models.Metrix.Matrix;
import com.corroy.mathieu.go4lunch.Models.Metrix.Row;
import com.corroy.mathieu.go4lunch.Models.OpeningHours;
import com.corroy.mathieu.go4lunch.Models.Result;
import com.corroy.mathieu.go4lunch.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class ListViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.listview_name_textview)
    TextView restaurantName;
    @BindView(R.id.listview_adress_textview)
    TextView restaurantAdress;
    @BindView(R.id.listview_restaurant_type_textview)
    TextView restaurantType;
    @BindView(R.id.listview_openclose_textview)
    TextView restaurantOpenClose;
    @BindView(R.id.listview_distance_textview)
    TextView restaurantDistance;
    @BindView(R.id.listView_picture)
    ImageView restaurantPicture;

    List<Row> listMatrix;
    private Disposable disposable;

    // need the position (from the GPSTRACKER)
    private String position;
    private GPSTracker gpsTracker;


    public ListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        listMatrix = new ArrayList<>();
    }

    public void updateWithGoogle(Result result){

        // ------------- NAME ------------
        this.restaurantName.setText(result.getName());
        // ------------- Adress ----------
        this.restaurantAdress.setText(result.getAdrAddress());
        this.restaurantType.setText(result.getVicinity());
        String destinations = result.getGeometry().getLocation().getLat() + "," + result.getGeometry().getLocation().getLng();
        executeHttpRequestMatrix(destinations);
        // ---------- Opening -------------
        if( result.getOpeningHours() != null){
            if(result.getOpeningHours().getOpenNow() != null){
                if(result.getOpeningHours().getOpenNow()){
                    restaurantOpenClose.setText("Open");
                } else {
                    restaurantOpenClose.setText("Closed");
                }
            }
        }
        if (result.getPhotos() != null){
            Glide.with(this.itemView.getContext())
                    .load(result.getPhotos().get(0).getPhotoReference())
                    .into(restaurantPicture);
        }
    }

    private void executeHttpRequestMatrix(String destinations){
//        position = gpsTracker.getLatitude() + "," + gpsTracker.getLongitude();
        this.disposable = Go4LunchStreams.streamFetchMatrix(position, destinations).subscribeWith(new DisposableObserver<Matrix>(){

            @Override
            public void onNext(Matrix matrix) {
                listMatrix.addAll(matrix.getRows());
                Log.i("MATRIX API", "ON NEXT");
            }

            @Override
            public void onError(Throwable e) {
                Log.i("MATRIX API", "ON ERROR");

            }

            @Override
            public void onComplete() {
                restaurantDistance.setText(listMatrix.get(0).getElements().get(0).getDistance().getValue() + "m");
                if (disposable != null && !disposable.isDisposed()) disposable.dispose();
                Log.i("MATRIX API", "ON COMPLETE");
            }
        });
    }
}