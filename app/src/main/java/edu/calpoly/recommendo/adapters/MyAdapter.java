package edu.calpoly.recommendo.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

import edu.calpoly.recommendo.R;
import edu.calpoly.recommendo.suggestions.Suggestion;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private ArrayList<Suggestion> mSuggestions;

    public MyAdapter(ArrayList<Suggestion> entries) {
        mSuggestions = entries;
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