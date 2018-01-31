package com.sublime.imagefetcher.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.sublime.imagefetcher.R;
import com.sublime.imagefetcher.model.Photo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by goonerdroid
 * on 29/1/18.
 */

public class PhotoItemAdapter extends RecyclerView.Adapter<PhotoItemAdapter.ItemViewHolder>{

    private List<Photo> photosList = new ArrayList<>();
    private RequestManager requestManager;

    public PhotoItemAdapter(RequestManager glideRequestManager) {
        requestManager = glideRequestManager;
    }

    public void addItems(List<Photo> dataList) {
        photosList.addAll(dataList);
        this.notifyItemRangeChanged(getItemCount(),photosList.size() - 1);
    }

    public void notifyDataChanged(List<Photo> dataList){
        photosList.clear();
        photosList.addAll(dataList);
        this.notifyDataSetChanged();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_list_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Photo photo = photosList.get(position);
        String photoURL = "https://farm" + photo.getFarm() + ".staticflickr.com/" + photo.getServer()
                + "/" + photo.getId() + "_" + photo.getSecret() + "_c.jpg";//returns medium size image
        String lowResPhotoURL = "https://farm" + photo.getFarm() + ".staticflickr.com/" + photo.getServer()
                + "/" + photo.getId() + "_" + photo.getSecret() + "_t.jpg";//returns thumbnail size image


        requestManager.load(photoURL)
                .thumbnail(requestManager.load(lowResPhotoURL)
                        .thumbnail(0.5f)
                )
                .into(holder.ivPhoto);
        holder.tvPhotoName.setText(photo.getTitle());
    }

    @Override
    public int getItemCount() {
        return photosList.size();
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_photo)
        ImageView ivPhoto;
        @BindView(R.id.tv_photo_name)
        TextView tvPhotoName;

        ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
