package com.corroy.mathieu.go4lunch.Views;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.corroy.mathieu.go4lunch.Models.Helper.User;
import com.corroy.mathieu.go4lunch.R;
import java.util.List;

public class WorkmatesAdapter extends RecyclerView.Adapter<WorkmatesViewHolder> {

    private List<User> user;

    public WorkmatesAdapter(List<User> user){
        this.user = user;
    }

    @NonNull
    @Override
    public WorkmatesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_workmates_item, viewGroup, false);
        return new WorkmatesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmatesViewHolder workmatesViewHolder, int i) {
        workmatesViewHolder.updateData(user.get(i));
    }

    @Override
    public int getItemCount() {
        return user.size();
    }
}
