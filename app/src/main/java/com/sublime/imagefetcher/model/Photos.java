package com.sublime.imagefetcher.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by goonerdroid
 * on 29/1/18.
 */

public class Photos {

    @SerializedName("page")
    private int page;

    public int getPage() {
        return page;
    }
}
