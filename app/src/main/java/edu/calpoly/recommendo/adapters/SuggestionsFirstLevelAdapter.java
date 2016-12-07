package edu.calpoly.recommendo.adapters;

import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.calpoly.recommendo.R;
import edu.calpoly.recommendo.managers.suggestions.Suggestion;

/**
 * Created by sethbarrios on 12/7/16.
 */

public class SuggestionsFirstLevelAdapter extends RecyclerView.Adapter<FirstLevelViewHolder>{

    private static final String TAG = "FirstLevelAdapter";

    private ArrayList<ArrayList<Suggestion>> suggestionLists = new ArrayList<>();
    public void setSuggestionLists(ArrayList<ArrayList<Suggestion>> list) {suggestionLists = list;}

    private ArrayList<String> keysInOrder = new ArrayList<>();
    public void setKeysInOrder(ArrayList<String> keys) {keysInOrder = keys;}

    @Override
    public FirstLevelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FirstLevelViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.nested_suggestion_view, parent, false));
    }

    @Override
    public void onBindViewHolder(FirstLevelViewHolder holder, int position) {
        holder.bindToSuggestions(suggestionLists.get(position), keysInOrder.get(position));
    }

    @Override
    public int getItemCount() {
        return suggestionLists.size();
    }
}

class FirstLevelViewHolder extends RecyclerView.ViewHolder {

    TextView categoryTextView;
    RecyclerView recyclerView;
    SuggestionsSecondLevelAdapter adapter;
    ArrayList<Suggestion> mySuggestions;


    public FirstLevelViewHolder(View itemView) {
        super(itemView);

        // Get handle on UI elements
        recyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerView);
        categoryTextView = (TextView) itemView.findViewById(R.id.category_title);

        // Set up recyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new SuggestionsSecondLevelAdapter();
        recyclerView.setAdapter(adapter);
    }

    public void bindToSuggestions(ArrayList<Suggestion> suggestions, String categoryTitle) {
        categoryTextView.setText(categoryTitle.toUpperCase());
        mySuggestions = suggestions;
        adapter.setSuggestions(suggestions);
        adapter.notifyDataSetChanged();
    }

}
