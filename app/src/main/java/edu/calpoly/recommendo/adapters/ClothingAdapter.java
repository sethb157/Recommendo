package edu.calpoly.recommendo.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import edu.calpoly.recommendo.R;
import edu.calpoly.recommendo.managers.suggestions.Suggestion;
import edu.calpoly.recommendo.managers.suggestions.SuggestionsManager;

/**
 * Created by sethbarrios on 12/7/16.
 */

public class ClothingAdapter extends RecyclerView.Adapter<ClothingViewHolder> {
    private static final String TAG = "CLOTHING_ADAPTER";
    private ArrayList<Suggestion> suggestions = new ArrayList<>();

    public void setSuggestions(ArrayList<Suggestion> suggestions) {this.suggestions = suggestions;}

    @Override
    public ClothingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ClothingViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.clothing_view, parent, false));
    }

    @Override
    public void onBindViewHolder(ClothingViewHolder holder, int position) {
        holder.bind(suggestions.get(position));
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }
}

class ClothingViewHolder extends RecyclerView.ViewHolder {

    private TextView textView;
    private ImageView imageView;

    public ClothingViewHolder(View itemView) {
        super(itemView);

        textView = (TextView) itemView.findViewById(R.id.clothing_text_view);
        imageView = (ImageView) itemView.findViewById(R.id.clothing_image_view);
    }

    public void bind(Suggestion suggestion) {
        textView.setText(suggestion.getName());
        imageView.setImageResource(SuggestionsManager.getResourceIDForName(suggestion.getName()));
//        Glide.with(itemView.getContext()).load(SuggestionsManager.getResourceIDForName(suggestion.getName())).into(imageView);
    }
}
