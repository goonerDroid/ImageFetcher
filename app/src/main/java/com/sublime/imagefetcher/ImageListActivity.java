package com.sublime.imagefetcher;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.style.FoldingCube;
import com.sublime.imagefetcher.api.APIError;
import com.sublime.imagefetcher.api.APIRequest;
import com.sublime.imagefetcher.api.OnRequestComplete;
import com.sublime.imagefetcher.model.ImageResponse;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageListActivity extends AppCompatActivity {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;


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
                progressBar.setVisibility(View.GONE);
                ImageResponse imageResponse = (ImageResponse) object;
                if (imageResponse != null)itemAdapter.addItems(imageResponse.getPhotos().getPhotoList());
            }

            @Override
            public void onAPIFailure(APIError apiError) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void initView() {
        //inits loading indicator.
        FoldingCube foldingCube = new FoldingCube();
        foldingCube.setBounds(0, 0, 50, 50);
        foldingCube.setColor(getResources().getColor(R.color.colorAccent));
        progressBar.setIndeterminateDrawable(foldingCube);


        //inits recyclerview.
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.hasFixedSize();
        itemAdapter = new PhotoItemAdapter(Glide.with(this));
        recyclerView.setAdapter(itemAdapter);
    }
}
