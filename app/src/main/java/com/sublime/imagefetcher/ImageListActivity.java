package com.sublime.imagefetcher;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sublime.imagefetcher.api.APIError;
import com.sublime.imagefetcher.api.APIRequest;
import com.sublime.imagefetcher.api.OnRequestComplete;
import com.sublime.imagefetcher.model.ImageResponse;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageListActivity extends AppCompatActivity {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;


    private  PhotoItemAdapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);
        ButterKnife.bind(this);
        initView();
        fetchImages();
    }

    private void fetchImages() {
        APIRequest apiRequest = APIRequest.init();
        apiRequest.fetchImageList(5, 1, new OnRequestComplete() {
            @Override
            public void onSuccess(Object object) {
                ImageResponse imageResponse = (ImageResponse) object;
                if (imageResponse != null)itemAdapter.addItems(imageResponse.getPhotos().getPhotoList());
            }

            @Override
            public void onAPIFailure(APIError apiError) {

            }
        });
    }

    private void initView() {
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.hasFixedSize();
        itemAdapter = new PhotoItemAdapter(Glide.with(this));
        recyclerView.setAdapter(itemAdapter);
    }
}
