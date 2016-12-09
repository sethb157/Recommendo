package edu.calpoly.recommendo.adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import edu.calpoly.recommendo.R;
import edu.calpoly.recommendo.activities.DetailSuggestionActivity;
import edu.calpoly.recommendo.managers.suggestions.Suggestion;

/**
 * Created by sethbarrios on 12/7/16.
 */

public class SuggestionsSecondLevelAdapter extends RecyclerView.Adapter<SecondLevelViewHolder> {

    private ArrayList<Suggestion> suggestions = new ArrayList<>();
    // This is for passing along to the detail view
    // Since the data source is nested
    public int categoryIndex = -1;

    public  void setSuggestions(ArrayList<Suggestion> suggestions) {this.suggestions = suggestions;}

    @Override
    public SecondLevelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SecondLevelViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.suggestion_view, parent, false));
    }

    @Override
    public void onBindViewHolder(SecondLevelViewHolder holder, int position) {
        holder.bindToSuggestion(suggestions.get(position));
        holder.categoryPosition = categoryIndex;
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }
}

class SecondLevelViewHolder extends RecyclerView.ViewHolder {

    public TextView textView;
    public ImageView imageView;
    public View itemView;
    public int categoryPosition;

    public SecondLevelViewHolder(View itemView) {
        super(itemView);

        // Get UI handles
        this.itemView = itemView;
        textView = (TextView) itemView.findViewById(R.id.suggestion_text_view);
        imageView = (ImageView) itemView.findViewById(R.id._suggestion_image_view);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(v.getContext(), DetailSuggestionActivity.class);
                myIntent.putExtra(DetailSuggestionActivity.CATEGORY_INDEX_KEY, categoryPosition);
                myIntent.putExtra("key", getAdapterPosition());
                v.getContext().startActivity(myIntent);
            }
        });
    }

    public void bindToSuggestion(Suggestion suggestion) {
        textView.setText(suggestion.getName());

        // Load in picture if possible
        String picRef = suggestion.getPicRef();
        if (picRef != null && !picRef.isEmpty()) {

            String path = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=100&photoreference=" + picRef + "&key=" + "AIzaSyAWIWJ9WuWlQ2hHIlJgqCLBRXpmB3pMY2Y";
            Glide.with(itemView.getContext()).load(path).into(imageView);
            imageView.setPadding(0, 0, 0 ,0);
        }
    }
}