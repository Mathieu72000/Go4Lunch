package com.corroy.mathieu.go4lunch.Views;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.corroy.mathieu.go4lunch.Models.NearbySearch.NearbyResult;
import com.corroy.mathieu.go4lunch.Models.NearbySearch.Location;
import com.corroy.mathieu.go4lunch.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ListViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.item_textview_name)
    TextView restaurantName;
    @BindView(R.id.item_textview_address)
    TextView restaurantAdress;
    @BindView(R.id.item_textview_opening)
    TextView restaurantOpenClose;
    @BindView(R.id.item_textview_distance)
    TextView restaurantDistance;
    @BindView(R.id.item_imageview_main_pic)
    ImageView restaurantPicture;
    @BindView(R.id.item_ratingBar)
    RatingBar restaurantRatingBar;

    private float[] distanceResults = new float[3];
    private boolean textOK = false;

    public ListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void updateWithGoogle(NearbyResult result, String userLocation){

        // ------------- NAME ------------
        this.restaurantName.setText(result.getName());
        // ------------- ADDRESS ----------
        this.restaurantAdress.setText(result.getVicinity());
        // ------------ RATING BAR ----------
        displayRating(result);
        // ----------- DISTANCE -----------
        displayDistance(userLocation, result.getGeometry().getLocation());
        String distance = Integer.toString(Math.round(distanceResults[0]));
        this.restaurantDistance.setText(itemView.getResources().getString(R.string.list_unit_distance, distance));
        // ---------- Opening -------------
        if( result.getOpeningHours() != null) {
            if (result.getOpeningHours().getOpenNow()) {
                    restaurantOpenClose.setText(R.string.open);
                    restaurantOpenClose.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.quantum_lightgreen));
                } else {
                    restaurantOpenClose.setText(R.string.close);
                    restaurantOpenClose.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.google_button));
            }
        } else {
            restaurantOpenClose.setText(itemView.getContext().getString(R.string.time_unavailable));
            restaurantOpenClose.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.colorBlack));
        }

        // -------------- PICTURE ------------------
        if (!(result.getPhotos() == null)){
            if (!(result.getPhotos().isEmpty())){
                Glide.with(itemView)
                        .load("https://maps.googleapis.com/maps/api/place/photo"+"?maxwidth="+75+"&maxheight="+75+"&photoreference="+result.getPhotos().get(0).getPhotoReference()+"&key=AIzaSyDXI74hOiHLi4l2vhUEs23260f055xyXvI")
                        .into(restaurantPicture);
            }
        }else{
            Glide.with(itemView)
                    .load(R.drawable.nopicture)
                    .apply(RequestOptions.centerCropTransform())
                    .into(restaurantPicture);
        }
    }

    private void displayRating(NearbyResult result){
        if(result.getRating() != 0){
            double googleRating = result.getRating();
            double rating = googleRating /  5 * 3;
            this.restaurantRatingBar.setRating((float)rating);
            this.restaurantRatingBar.setVisibility(View.VISIBLE);
        } else {
            this.restaurantRatingBar.setVisibility(View.GONE);
        }
    }

    private void displayDistance(String startLocation, Location endLocation){
        String[] separatedStart = startLocation.split(",");
        double startLatitude = Double.parseDouble(separatedStart[0]);
        double startLongitude = Double.parseDouble(separatedStart[1]);
        double endLatitude = endLocation.getLat();
        double endLongitude = endLocation.getLng();
        android.location.Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude,distanceResults);
    }
}