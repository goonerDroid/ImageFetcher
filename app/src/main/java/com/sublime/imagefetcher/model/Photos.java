package com.sublime.imagefetcher.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by goonerdroid
 * on 29/1/18.
 */

public class Photos {

    @SerializedName("page")
    private int page;
    @SerializedName("perpage")
    private int perPage;
    @SerializedName("total")
    private int total;
    @SerializedName("photo")
    private List<Photo> photoList;

    public int getPage() {
        return page;
    }

    public int getPerPage() {
        return perPage;
    }

    public int getTotal() {
        return total;
    }

    public List<Photo> getPhotoList() {
        return photoList;
    }
}
