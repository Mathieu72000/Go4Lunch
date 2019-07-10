package com.corroy.mathieu.go4lunch.Views;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
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
    public void updateData(User user, RequestManager glide){
        if(user.getUsername().equals(getCurrentUser().getDisplayName())){
            name.setText(user.getUsername());

            glide.load(user.getUrlPicture())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.e("Glide", "Load failed", e);

                            assert  e != null;
                            for(Throwable t : e.getRootCauses()){
                                Log.e("Glide", "Caused by : ", t);
                            }

                            e.logRootCauses("Glide");
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Log.e("Glide", "Load success");
                            return false;
                        }
                    })
                    .apply(RequestOptions.circleCropTransform())
                    .into(picture);
        }
    }
    @Nullable
    protected FirebaseUser getCurrentUser(){ return FirebaseAuth.getInstance().getCurrentUser(); }
}
