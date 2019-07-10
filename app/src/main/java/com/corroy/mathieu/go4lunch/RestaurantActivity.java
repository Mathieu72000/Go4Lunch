package com.corroy.mathieu.go4lunch;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.corroy.mathieu.go4lunch.Models.Details;
import com.corroy.mathieu.go4lunch.Models.Result;
import com.corroy.mathieu.go4lunch.Models.User;
import com.corroy.mathieu.go4lunch.Models.UserHelper;
import com.corroy.mathieu.go4lunch.Utils.Go4LunchStreams;
import com.corroy.mathieu.go4lunch.Views.WorkmatesAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class RestaurantActivity extends AppCompatActivity {

    @BindView(R.id.activity_restaurant_restaurant_picture)
    ImageView restaurantImageView;
    @BindView(R.id.activity_restaurant_address)
    TextView restaurantAdresse;
    @BindView(R.id.activity_restaurant_name)
    TextView restaurantName;
    @BindView(R.id.activity_restaurant_recycler)
    RecyclerView recyclerView;
    @BindView(R.id.activity_restaurant_ratingBar)
    RatingBar ratingBar;
    @BindView(R.id.activity_restaurant_button_like)
    Button likeBtn;
    private Disposable disposable;
    private String placeId;
    private String picture;
    private List<Result> resultList;
    private List<User> userList;
    private Result result;
    private WorkmatesAdapter workmatesAdapter;
    private String COLLECTION_NAME = "users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        placeId = intent.getStringExtra("ID");
        picture = intent.getStringExtra("PICTURE");

        resultList = new ArrayList<>();

        this.configureRecyclerView();
        this.executeHttpRequestWithRetrofit();
        this.executeFirebaseRequest();
    }

    // Configure the recyclerView and glue it with the adapter
    public void configureRecyclerView(){
        this.userList = new ArrayList<>();
        this.workmatesAdapter = new WorkmatesAdapter(userList, Glide.with(this));
        this.recyclerView.setAdapter(this.workmatesAdapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        this.disposeWhenDestroy();
    }

    // Execute API request to get details of the restaurant
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

    // Execute Firebase request to get the collection
    private void executeFirebaseRequest(){
        FirebaseFirestore.getInstance()
                .collection(COLLECTION_NAME)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        List<DocumentSnapshot> myListOfDocuments = task.getResult().getDocuments();
                        for(DocumentSnapshot documentSnapshot : myListOfDocuments){
                            UserHelper.getUser(documentSnapshot.getId()).addOnSuccessListener(documentSnapshot1 -> {
                                User user = documentSnapshot1.toObject(User.class);
                                user.setActualRestau(result.getName());
                                if(!user.getUid().equals(getCurrentUser().getUid())){
                                    userList.add(user);}
                                workmatesAdapter.notifyDataSetChanged();
                            });
                        }
                    }
                });
    }

    private void updateUI(){
        // Get the restaurant name
        restaurantName.setText(result.getName());

        // Get the restaurant vicinity
        String vicinity = result.getTypes().get(0) + " - " + result.getVicinity();
        restaurantAdresse.setText(vicinity);

        // Get the restaurant rating
        if(result.getRating() != null){
            double googleRating = result.getRating();
            double rating = googleRating /  5 * 3;
            this.ratingBar.setRating((float)rating);
            this.ratingBar.setVisibility(View.VISIBLE);
        } else {
            this.ratingBar.setVisibility(View.GONE);
        }

        // Get the restaurant picture
        if (picture != null){
            Glide.with(this)
                    .load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=200&maxheight=150&key=AIzaSyApi61iqZP6ZR-mGkYvZkTSLH7OskLQJj0&photoreference=" + picture)
                    .into(restaurantImageView);
        }
    }
    @OnClick(R.id.activity_restaurant_button_call)
    public void onClickCall(){
        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", result.getFormattedPhoneNumber(), null));
        startActivity(callIntent);
    }

    // Display the restaurant website when the user click on the button
    @OnClick(R.id.activity_restaurant_button_website)
    public void onClickWeb(){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(result.getWebsite()));
        startActivity(browserIntent);
    }

    // Dispose subscription
    private void disposeWhenDestroy(){
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

    @Nullable
    protected FirebaseUser getCurrentUser(){ return FirebaseAuth.getInstance().getCurrentUser(); }

}