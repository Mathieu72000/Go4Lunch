package com.corroy.mathieu.go4lunch.Views;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.corroy.mathieu.go4lunch.Models.Details.Result;
import com.corroy.mathieu.go4lunch.Models.NearbySearch.NearbyResult;
import com.corroy.mathieu.go4lunch.R;

import java.util.List;

public class ListViewAdapter extends RecyclerView.Adapter<ListViewHolder> {

    private List<NearbyResult> mResultList;
    private Context mContext;
    private String location;

    public ListViewAdapter(Context context, List<NearbyResult> result, String location) {
        this.mContext = context;
        this.mResultList = result;
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
        listViewViewHolder.updateWithGoogle(this.mResultList.get(position), location);
    }

    @Override
    public int getItemCount() {
        return this.mResultList.size();
    }

    public void refreshAdapter(List<NearbyResult> nearbyResults){
        mResultList = nearbyResults;
        notifyDataSetChanged();
    }

    public List<NearbyResult> getmResultList() {
        return mResultList;
    }
}
