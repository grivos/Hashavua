package com.invariantlabs.hashavua.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.List;

public class Util {

    public static void setRtl(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (activity.getWindow().getDecorView().getLayoutDirection() == View.LAYOUT_DIRECTION_LTR){
                activity.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }
    }

    /**
     * Converts a dp value to a px value
     *
     * @param dp the dp value
     */
    public static int dpToPx(Context context, float dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) ((dp * displayMetrics.density) + 0.5);
    }

    public static void hideKeyboard(Context ctx, View focusedView) {
        if (focusedView == null) return;
        InputMethodManager inputMethodManager = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
    }

    public static <E> boolean isEqualData(List<? extends E> a, List<? extends E> b) {
        if (a == null) {
            return b == null;
        } else if (b == null) {
            return false;
        }
        if (a.size() != b.size()) {
            return false;
        }
        for (int i = 0; i < a.size(); i++) {
            E a1 = a.get(i);
            E b1 = b.get(i);
            if (!a1.equals(b1)) {
                return false;
            }
        }
        return true;
    }

    private Util() {
        throw new AssertionError("No instances.");
    }

}
