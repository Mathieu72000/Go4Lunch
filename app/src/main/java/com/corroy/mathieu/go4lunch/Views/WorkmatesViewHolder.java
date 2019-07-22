package com.corroy.mathieu.go4lunch.Views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.corroy.mathieu.go4lunch.Models.User;
import com.corroy.mathieu.go4lunch.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    // Get the username and picture in firebase and display it
    public void updateData(User user){
        if(user.getUsername().equals(getCurrentUser().getDisplayName())){
            name.setText(user.getUsername());

            Glide.with(itemView)
                    .load(user.getUrlPicture())
                    .apply(RequestOptions.circleCropTransform())
                    .into(picture);
        }
    }
    @Nullable
    protected FirebaseUser getCurrentUser(){ return FirebaseAuth.getInstance().getCurrentUser(); }
}
