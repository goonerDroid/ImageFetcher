package com.sublime.imagefetcher.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by goonerdroid
 * on 29/1/18.
 */

public class ImageResponse {

    @SerializedName("photos")
    private Photos photos;
    @SerializedName("stat")
    private String status;

    public Photos getPhotos() {
        return photos;
    }

    public String getStatus() {
        return status;
    }

}
