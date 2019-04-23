package com.corroy.mathieu.go4lunch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RestaurantActivity extends AppCompatActivity {

    @BindView(R.id.restaurant_imageview)
    ImageView restaurantImageView;
    @BindView(R.id.restaurant_adresse)
    TextView restaurantAdresse;
    @BindView(R.id.restaurant_name)
    TextView restaurantName;
    @BindView(R.id.restaurant_tiret)
    TextView restaurantTiret;
    @BindView(R.id.restaurant_type)
    TextView restaurantType;
    @BindView(R.id.restaurant_recyclerView)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        ButterKnife.bind(this);
    }
}
