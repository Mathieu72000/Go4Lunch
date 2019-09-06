package com.corroy.mathieu.go4lunch.Views;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.corroy.mathieu.go4lunch.Models.Helper.User;
import com.corroy.mathieu.go4lunch.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkmatesViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.workmates_name)
    TextView name;
    @BindView(R.id.workmates_picture)
    ImageView picture;

    public WorkmatesViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    // Get the username and picture of the user in fireBase and display it
    @SuppressLint("NewApi")
    public void updateData(User user){
        if(user.getJoinedRestaurant() != null) {
            name.setText(itemView.getContext().getString(R.string.user_name, user.getUsername(), user.getJoinedRestaurant()));
            name.setTypeface(name.getTypeface(), Typeface.NORMAL);
            name.setTextColor(itemView.getContext().getColor(R.color.colorBlack));
            name.setAlpha((float) 1);

        } else {
            name.setText(itemView.getContext().getString(R.string.decided));
            name.setTypeface(name.getTypeface(), Typeface.ITALIC);
            name.setTextColor(itemView.getContext().getColor(R.color.colorGray));
            name.setAlpha((float) 0.5);
        }

            Glide.with(itemView)
                    .load(user.getUrlPicture())
                    .apply(RequestOptions.circleCropTransform())
                    .into(picture);
        }
}
