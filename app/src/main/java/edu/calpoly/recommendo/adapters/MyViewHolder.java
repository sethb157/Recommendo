package edu.calpoly.recommendo.adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

import edu.calpoly.recommendo.R;
import edu.calpoly.recommendo.activities.DetailSuggestionActivity;
import edu.calpoly.recommendo.suggestions.Suggestion;

public class MyViewHolder extends RecyclerView.ViewHolder {

    private TextView tv;
    public Suggestion suggestion;

    public MyViewHolder(final View itemView) {
        super(itemView);
        tv = (TextView) itemView.findViewById(R.id.specific_suggestion);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(v.getContext(), DetailSuggestionActivity.class);
                myIntent.putExtra("key", getAdapterPosition()); //Optional parameters
                v.getContext().startActivity(myIntent);
            }
        });
    }

    public void bind(final Suggestion s) {
        this.suggestion = s;
        tv.setText(s.getName());
    }
}
