package com.sublime.imagefetcher.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by goonerdroid
 * on 28/1/18.
 */

public  class AppUtils {

    private Context context;

    public AppUtils(Context context) {
        this.context = context;
    }

    //--- Check device connected to or not internet
    public boolean isInternetConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
}
