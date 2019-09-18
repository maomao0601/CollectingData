package org.lzu.collectingdata;

import android.content.res.Resources;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.IntegerRes;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;

/**
 * Created by Administrator on 2016/11/25.
 */

public class ScreenInfo {
    private static DisplayMetrics pm = Resources.getSystem().getDisplayMetrics();

    public static int width() {
        return pm.widthPixels;
    }

    public static int height() {
        return pm.heightPixels;
    }

    public static int widthDp() {
        return Math.round(width() / density());
    }

    public static int heightDp() {
        return Math.round(height() / density());
    }

    public static float density() {
        return pm.density;
    }

    public static int dimen(@DimenRes int res) {
        return MyApplication.context.getResources().getDimensionPixelSize(res);
    }

    public static int color(@ColorRes int res) {
        return ContextCompat.getColor(MyApplication.context, res);
    }

    public static int intValue(@IntegerRes int res) {
        return MyApplication.context.getResources().getInteger(res);
    }

    public static float toDip(float size) {
        return size / density();
    }

    public static int toPix(float size) {
        return Math.round(size * density());
    }

    public static float toPixFloat(float size) {
        return size * density();
    }
}
