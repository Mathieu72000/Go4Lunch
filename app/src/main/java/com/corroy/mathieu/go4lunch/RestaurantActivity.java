package com.corroy.mathieu.go4lunch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.corroy.mathieu.go4lunch.Models.Details;
import com.corroy.mathieu.go4lunch.Models.Result;
import com.corroy.mathieu.go4lunch.Utils.Go4LunchStreams;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class RestaurantActivity extends AppCompatActivity {

    @BindView(R.id.restaurant_imageview)
    ImageView restaurantImageView;
    @BindView(R.id.restaurant_adresse)
    TextView restaurantAdress;
    @BindView(R.id.restaurant_name)
    TextView restaurantName;
    @BindView(R.id.restaurant_type)
    TextView restaurantType;
    @BindView(R.id.restaurant_recyclerView)
    RecyclerView recyclerView;
    private Disposable disposable;
    private String placeId;
    private String picture;
    private List<Result> resultList;
    private Result result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        placeId = intent.getStringExtra("placeid");
        picture = intent.getStringExtra("picture");

        resultList = new ArrayList<>();

        this.executeHttpRequestWithRetrofit();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        this.disposeWhenDestroy();
    }


    private void executeHttpRequestWithRetrofit(){
        this.disposable = Go4LunchStreams.streamFetchGoogleDetails(placeId).subscribeWith(new DisposableObserver<Details>() {
            @Override
            public void onNext(Details details) {
                result = details.getResult();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                updateUI();
            }
        });
    }

    private void updateUI(){
        restaurantName.setText(result.getName());
        String type = result.getTypes().get(0) + "-";
        String vicinity = result.getVicinity();
        restaurantType.setText(type);
        restaurantAdress.setText(vicinity);

        if (picture != null){
            Glide.with(this)
                    .load(result.getPhotos().get(0).getPhotoReference())
                    .into(restaurantImageView);
        }
    }

    // Dispose subscription
    private void disposeWhenDestroy(){
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }
}