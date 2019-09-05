package com.corroy.mathieu.go4lunch.Views;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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
    public void updateData(User user){
        if(user.getJoinedRestaurant() != null) {
            name.setText(itemView.getContext().getString(R.string.user_name, user.getUsername(), user.getJoinedRestaurant()));
        } else {
            // todo Mettre en italic
            name.setText(itemView.getContext().getString(R.string.decided));
        }

            Glide.with(itemView)
                    .load(user.getUrlPicture())
                    .apply(RequestOptions.circleCropTransform())
                    .into(picture);
        }
}
