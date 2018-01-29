package com.sublime.imagefetcher;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sublime.imagefetcher.api.APIError;
import com.sublime.imagefetcher.api.APIRequest;
import com.sublime.imagefetcher.api.OnRequestComplete;

public class ImageListActivity extends AppCompatActivity {

    private APIRequest apiRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);
        apiRequest = APIRequest.init();
        apiRequest.fetchImageList(5, 1, new OnRequestComplete() {
            @Override
            public void onSuccess(Object object) {

            }

            @Override
            public void onAPIFailure(APIError apiError) {

            }
        });
    }
}
