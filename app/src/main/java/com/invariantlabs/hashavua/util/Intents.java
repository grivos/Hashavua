package com.invariantlabs.hashavua.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.widget.Toast;

import java.util.List;

import static android.widget.Toast.LENGTH_LONG;

public class Intents {

    public static boolean maybeStartActivity(Context context, Intent intent) {
        if (hasHandler(context, intent)) {
            context.startActivity(intent);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Queries on-device packages for a handler for the supplied {@link Intent}.
     */
    private static boolean hasHandler(Context context, Intent intent) {
        List<ResolveInfo> handlers = context.getPackageManager().queryIntentActivities(intent, 0);
        return !handlers.isEmpty();
    }

    private Intents() {
        throw new AssertionError("No instances.");
    }
}
