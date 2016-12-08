package edu.calpoly.recommendo.activities;

/**
 * Created by Costin on 11/30/16.
 */

public class PreferenceItem {
    public final int iconID;
    public final String activityDescription;
    public boolean checked;

    public PreferenceItem(int iconID, String activityDescription, boolean checked) {
        this.iconID = iconID;
        this.activityDescription = activityDescription;
        this.checked = checked;
    }
}
