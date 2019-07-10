package com.corroy.mathieu.go4lunch.Views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.RequestManager;
import com.corroy.mathieu.go4lunch.Models.User;
import com.corroy.mathieu.go4lunch.R;
import java.util.List;

public class WorkmatesAdapter extends RecyclerView.Adapter<WorkmatesViewHolder> {

    private RequestManager glide;
    private List<User> user;
    private Context context;

    public WorkmatesAdapter(List<User> user, RequestManager glide){
        this.user = user;
        this.glide = glide;
    }

    @NonNull
    @Override
    public WorkmatesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.fragment_workmates_item, viewGroup, false);
        return new WorkmatesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmatesViewHolder workmatesViewHolder, int i) {
        workmatesViewHolder.updateData(user.get(i), glide);
    }

    @Override
    public int getItemCount() {
        return user.size();
    }
}
