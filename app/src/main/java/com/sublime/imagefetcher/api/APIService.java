package com.sublime.imagefetcher.api;

import com.sublime.imagefetcher.model.ImageResponse;

import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by goonerDroid
 * on 29-01-2018.
 */
public interface APIService {


   @GET("rest/?method=flickr.interestingness.getList")
   RetrofitCall<ImageResponse> getImageList(@Query("api_key") String apiKey,
                                            @Query("per_page") String perPageCount,
                                            @Query("page") String pageCount,
                                            @Query("format") String formatType,
                                            @Query("nojsoncallback") String noJsonCallback);

}


