package com.sublime.imagefetcher;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.style.FoldingCube;
import com.sublime.imagefetcher.api.APIError;
import com.sublime.imagefetcher.api.APIRequest;
import com.sublime.imagefetcher.api.OnRequestComplete;
import com.sublime.imagefetcher.model.ImageResponse;
import com.sublime.imagefetcher.utils.AppConstants;
import com.sublime.imagefetcher.utils.AppUtils;
import com.sublime.imagefetcher.widgets.EndlessRecyclerViewScrollListener;
import com.sublime.imagefetcher.widgets.ItemOffsetDecoration;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageListActivity extends AppCompatActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private static final int DEFAULT_PAGE_COUNT = 1;
    private static final int TOTAL_IMAGE_COUNT = 30;
    private  PhotoItemAdapter mItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchImages(DEFAULT_PAGE_COUNT);
    }

    private void initView() {
        //init loading indicator.
        FoldingCube foldingCube = new FoldingCube();
        foldingCube.setColor(getResources().getColor(R.color.colorAccent));
        progressBar.setIndeterminateDrawable(foldingCube);


        //init recyclerview.
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.hasFixedSize();
        mItemAdapter = new PhotoItemAdapter(Glide.with(this));
        recyclerView.setAdapter(mItemAdapter);
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (totalItemsCount >= TOTAL_IMAGE_COUNT){
                    Toast.makeText(ImageListActivity.this,"That's all folks!",Toast.LENGTH_SHORT).show();
                }else {
                    page++;
                    fetchImages(page);
                }
            }
        };
        // Adds the scroll listener to RecyclerView
        recyclerView.addOnScrollListener(scrollListener);
    }

    private void fetchImages(int pageCount) {
        if (new AppUtils(this).isInternetConnected()) {
            APIRequest apiRequest = APIRequest.init();
            apiRequest.fetchImageList(AppConstants.PER_PAGE_COUNT, pageCount, new OnRequestComplete() {
                @Override
                public void onSuccess(Object object) {
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    ImageResponse imageResponse = (ImageResponse) object;
                    if (imageResponse != null && imageResponse.getStatus().equalsIgnoreCase(AppConstants.RESPONSE_STATUS)) {
                        mItemAdapter.addItems(imageResponse.getPhotos().getPhotoList());
                    }else {
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.INVISIBLE);
                        String errorMssg = AppConstants.SERVER_ERROR_MSG;
                        Toast.makeText(ImageListActivity.this, errorMssg, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onAPIFailure(APIError apiError) {
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.INVISIBLE);
                    String errorMssg = AppConstants.SERVER_ERROR_MSG;
                    if (apiError.getError().equals(AppConstants.NETWORK_ERROR_MSG)) {
                        errorMssg = AppConstants.NETWORK_ERROR_MSG;
                    }
                    Toast.makeText(ImageListActivity.this, errorMssg, Toast.LENGTH_LONG).show();
                }
            });
        }else{
            progressBar.setVisibility(View.GONE);
            //Displays snackbar with action if no network present
            Snackbar snackbar = Snackbar.make(findViewById(R.id.container),AppConstants.NETWORK_ERROR_MSG,
                    Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.setting_string, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                }
            });
            snackbar.show();
        }
    }
}
