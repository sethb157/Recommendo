package edu.calpoly.recommendo.activities;

/**
 * Created by Costin on 11/30/16.
 */

public class PreferenceItem {
    public final int iconID;
    public boolean checked;

    public PreferenceItem(int icondID, boolean checked) {
        this.iconID = icondID;
        this.checked = checked;
    }
}
