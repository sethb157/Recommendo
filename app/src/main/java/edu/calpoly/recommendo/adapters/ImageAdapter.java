package edu.calpoly.recommendo.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.MotionEventCompat;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

import edu.calpoly.recommendo.R;
import edu.calpoly.recommendo.activities.PreferenceItem;
import edu.calpoly.recommendo.activities.Preferences;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    ArrayList<PreferenceItem> icons;

    public ImageAdapter(Context c) {
        setIconObjects(mThumbIds);
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return icons.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, final ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(155, 155));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(5, 5, 5, 5);
        }
        else {
            imageView = (ImageView) convertView;
        }

        final PreferenceItem pItem = (PreferenceItem) getItem(position);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pItem.checked = !pItem.checked;
                notifyDataSetChanged();
            }
        });

        Drawable drawable = mContext.getResources().getDrawable(pItem.iconID, null);
        DrawableCompat.setTint(drawable, pItem.checked? ResourcesCompat.getColor(mContext.getResources(), R.color.Salmon, null): Color.BLACK);
        imageView.setImageDrawable(drawable);

        return imageView;
    }

    private Integer[] mThumbIds = {
            R.drawable.coffee, R.drawable.fitness,
            R.drawable.restaurant,R.drawable.movies,
            R.drawable.hiking, R.drawable.bowling,
            R.drawable.reading, R.drawable.nightclub,
            R.drawable.shopping
    };

    private ArrayList<PreferenceItem> setIconObjects(Integer[] ids) {
        icons = new ArrayList<PreferenceItem>();

        for (Integer id : ids) {
            icons.add(new PreferenceItem(id, false));
        }

        return icons;
    }
}
