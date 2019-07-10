package com.corroy.mathieu.go4lunch.Views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.corroy.mathieu.go4lunch.Models.Result;
import com.corroy.mathieu.go4lunch.R;
import java.util.List;

public class ListViewAdapter extends RecyclerView.Adapter<ListViewHolder> {

    private List<Result> mResultList;
    private RequestManager glide;
    private Context mContext;
    private String location;

    public ListViewAdapter(Context context, List<Result> result, RequestManager glide, String location){
        this.mContext = context;
        this.mResultList = result;
        this.glide = glide;
        this.location = location;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.fragment_listview_item, parent, false);
        return new ListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder listViewViewHolder, int position) {
        listViewViewHolder.updateWithGoogle(this.mResultList.get(position),mContext, glide, location);
    }

    @Override
    public int getItemCount() {
        return this.mResultList.size();
    }
}
