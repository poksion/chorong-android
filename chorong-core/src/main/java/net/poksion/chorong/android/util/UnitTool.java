package net.poksion.chorong.android.util;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class UnitTool {
    private final Resources resources;

    public UnitTool(Resources resources) {
        this.resources = resources;
    }

    public int changeDipToPixel(int dp) {
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }

    public int changePixelToDip(int pixel) {
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, pixel, metrics);
    }
}
