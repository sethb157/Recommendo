package edu.calpoly.recommendo.activities;


import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import edu.calpoly.recommendo.R;

public class DetailSuggestionActivity extends Activity {
    private TextView name;
    private TextView address;
    private TextView type;
    private TextView category;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_suggestion);

        index = getIntent().getIntExtra("key", 0);

        //grab current suggestion

        name = (TextView) findViewById(R.id.name);
        address = (TextView) findViewById(R.id.address);
        type = (TextView) findViewById(R.id.type);
        category = (TextView) findViewById(R.id.category);
    }
}
