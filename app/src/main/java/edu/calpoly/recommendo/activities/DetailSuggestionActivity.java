package edu.calpoly.recommendo.activities;


import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import edu.calpoly.recommendo.R;
import edu.calpoly.recommendo.managers.suggestions.Suggestion;
import edu.calpoly.recommendo.managers.suggestions.SuggestionsManager;

public class DetailSuggestionActivity extends Activity {

    private SuggestionsManager suggestionsManager;
    private final static String PLACES_API_KEY = "AIzaSyAWIWJ9WuWlQ2hHIlJgqCLBRXpmB3pMY2Y";

    private TextView name;
    private TextView address;
    private TextView type;
    private TextView category;
    private ImageView imageView;
    private ImageView address_icon;
    private ImageView type_icon;
    private ImageView category_icon;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_suggestion);

        index = getIntent().getIntExtra("key", 0);

        suggestionsManager = SuggestionsManager.getSharedManager();

        final Suggestion s = suggestionsManager.getSuggestions().get(index);

        name = (TextView) findViewById(R.id.name);
        address = (TextView) findViewById(R.id.address);
        type = (TextView) findViewById(R.id.type);
        category = (TextView) findViewById(R.id.category);
        imageView = (ImageView) findViewById(R.id.detail_image_view);
        address_icon = (ImageView) findViewById(R.id.address_icon);
        type_icon = (ImageView) findViewById(R.id.type_icon);
        category_icon = (ImageView) findViewById(R.id.category_icon);

        name.setText(s.getName());
        address.setText(s.getAddress());
        type.setText(s.getType());
        category.setText(s.getCategory());

        address_icon.setImageResource(R.drawable.address);
        type_icon.setImageResource(R.drawable.type);
        category_icon.setImageResource(R.drawable.category);

        // Load in picture if possible
        String picRef = s.getPicRef();
        if (picRef != null && !picRef.isEmpty()) {
            String path = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=1000&photoreference=" + picRef + "&key=" + PLACES_API_KEY;
            Glide.with(this).load(path).into(imageView);
        }
    }
}
