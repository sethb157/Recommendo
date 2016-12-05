package edu.calpoly.recommendo.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import edu.calpoly.recommendo.R;
import edu.calpoly.recommendo.managers.suggestions.Suggestion;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    public ArrayList<Suggestion> mSuggestions;

    public MyAdapter() {
        // Begin with empty arraylist
        mSuggestions = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.individual_suggestion;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.bind(mSuggestions.get(position));
    }

    @Override
    public int getItemCount() {
        return mSuggestions.size();
    }

}