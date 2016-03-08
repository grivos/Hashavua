package com.invariantlabs.hashavua.view;

import android.content.Context;

import com.invariantlabs.hashavua.R;

import java.io.IOException;

public class ErrorMessageDeterminer {

    public String getErrorMessage(Throwable e, boolean pullToRefresh, Context context) {
        final int stringRes;
        if (e instanceof IOException) {
            stringRes = pullToRefresh? R.string.error_network : R.string.error_network_try_again;
        } else {
            stringRes = pullToRefresh? R.string.error_general : R.string.error_general_try_again;
        }
        return context.getString(stringRes);
    }
}
