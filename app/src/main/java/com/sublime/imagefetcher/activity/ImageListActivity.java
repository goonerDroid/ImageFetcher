package com.sublime.imagefetcher.activity;

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
import com.sublime.imagefetcher.adapter.PhotoItemAdapter;
import com.sublime.imagefetcher.R;
import com.sublime.imagefetcher.api.APIError;
import com.sublime.imagefetcher.api.APIRequest;
import com.sublime.imagefetcher.api.OnRequestComplete;
import com.sublime.imagefetcher.model.ImageResponse;
import com.sublime.imagefetcher.model.Photo;
import com.sublime.imagefetcher.utils.AppConstants;
import com.sublime.imagefetcher.utils.AppUtils;
import com.sublime.imagefetcher.utils.Timber;
import com.sublime.imagefetcher.widgets.EndlessRecyclerViewScrollListener;
import com.sublime.imagefetcher.widgets.ItemOffsetDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import butterknife.OnTouch;

public class ImageListActivity extends AppCompatActivity {

    private static final int MIN_CHARACTER_COUNT = 2;
    private static final int DEFAULT_PAGE_COUNT = 1;
    private static final int TOTAL_IMAGE_COUNT = 30;//Value set according to problem statement.

    private PhotoItemAdapter mItemAdapter;
    private List<Photo> originalPhotosList = new ArrayList<>();
    private List<Photo> searchPhotosList = new ArrayList<>();
    private EndlessRecyclerViewScrollListener scrollListener;


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);
        ButterKnife.bind(this);
        initView();
        fetchImages(DEFAULT_PAGE_COUNT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fetchImages(DEFAULT_PAGE_COUNT);//fetches data after connecting to internet if user navigates through settings.
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

        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Timber.wtf("onLoad more called");
                if (totalItemsCount >= TOTAL_IMAGE_COUNT){
                    Toast.makeText(ImageListActivity.this,"That's all folks!",Toast.LENGTH_SHORT).show();
                }else {
                    page++;
                    fetchImages(page);
                }
            }
        };
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
                        originalPhotosList.addAll(imageResponse.getPhotos().getPhotoList());
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

    @OnTextChanged(value = R.id.et_search, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void onSearch(CharSequence sequence){
        if (sequence.toString().length() == 0){
            AppUtils.hideKeyboard(ImageListActivity.this,recyclerView);//gives a standard view.
        } else if (sequence.toString().length() < MIN_CHARACTER_COUNT){
            searchPhotosList.clear();
            mItemAdapter.notifyDataChanged(originalPhotosList);
            recyclerView.addOnScrollListener(scrollListener);
        }else if (sequence.toString().length() > MIN_CHARACTER_COUNT){
            for (Photo photo : originalPhotosList){
                if (photo.getTitle() != null && photo.getTitle().toLowerCase().contains(sequence.toString().toLowerCase())){
                    searchPhotosList.clear();
                    searchPhotosList.add(photo);
                }
            }
            recyclerView.clearOnScrollListeners();
            mItemAdapter.notifyDataChanged(searchPhotosList);
        }
    }

    //-- Hides keyboard on scroll
    @OnTouch(R.id.recyclerView)
    public boolean onRecyclerTouch(View view){
        AppUtils.hideKeyboard(ImageListActivity.this,view);
        return false;
    }
}
