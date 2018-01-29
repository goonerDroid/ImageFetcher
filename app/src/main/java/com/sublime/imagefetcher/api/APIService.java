package com.sublime.imagefetcher.api;

import com.sublime.imagefetcher.model.Photos;

import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Namdev on 2/24/2016.
 */
public interface APIService {


   @GET("rest/?method=flickr.interestingness.getList")
   RetrofitCall<Photos> getImageList(@Query("api_key") String apiKey,
                                     @Query("per_page") String perPageCount,
                                     @Query("page") String pageCount,
                                     @Query("format") String formatType,
                                     @Query("nojsoncallback") String noJsonCallback);

}


