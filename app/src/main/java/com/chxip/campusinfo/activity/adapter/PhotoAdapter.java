package com.chxip.campusinfo.activity.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chxip.campusinfo.R;

import java.io.File;
import java.util.ArrayList;

import me.iwf.photopicker.utils.AndroidLifecycleUtils;

/**
 * Created by donglua on 15/5/31.
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {


  private ArrayList<String> photoPaths = new ArrayList<String>();
  private LayoutInflater inflater;

  private Context mContext;


  public final static int TYPE_ADD = 1;
  final static int TYPE_PHOTO = 2;

  private int MAX = 3;

  public PhotoAdapter(Context mContext, ArrayList<String> photoPaths) {
    this.photoPaths = photoPaths;
    this.mContext = mContext;
    inflater = LayoutInflater.from(mContext);

  }

  public int getMAX() {
    return MAX;
  }

  public void setMAX(int MAX) {
    this.MAX = MAX;
  }



  @Override public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = null;
    switch (viewType) {
      case TYPE_ADD:
        itemView = inflater.inflate(R.layout.item_add, parent, false);
        break;
      case TYPE_PHOTO:
        itemView = inflater.inflate(R.layout.pz_recyclerview_item, parent, false);
        break;
    }
    return new PhotoViewHolder(itemView);
  }


  @Override
  public void onBindViewHolder(final PhotoViewHolder holder, final int position) {

    if (getItemViewType(position) == TYPE_PHOTO) {
      boolean canLoadImage = AndroidLifecycleUtils.canLoadImage(holder.ivPhoto.getContext());
      if (canLoadImage) {
        if(photoPaths.get(position).contains("http")){
          Glide.with(mContext)
                  .load(photoPaths.get(position))
                  .centerCrop()
                  .thumbnail(0.1f)
                  .placeholder(me.iwf.photopicker.R.drawable.__picker_ic_photo_black_48dp)
                  .error(me.iwf.photopicker.R.drawable.__picker_ic_broken_image_black_48dp)
                  .into(holder.ivPhoto);
        }else{
          Uri uri = Uri.fromFile(new File(photoPaths.get(position)));
          Glide.with(mContext)
                  .load(uri)
                  .centerCrop()
                  .thumbnail(0.1f)
                  .placeholder(me.iwf.photopicker.R.drawable.__picker_ic_photo_black_48dp)
                  .error(me.iwf.photopicker.R.drawable.__picker_ic_broken_image_black_48dp)
                  .into(holder.ivPhoto);
        }
      }
    }
  }


  @Override
  public int getItemCount() {
    int count = photoPaths.size() + 1;
    if (count > MAX) {
      count = MAX;
    }
    return count;
  }

  @Override
  public int getItemViewType(int position) {
    return (position == photoPaths.size() && position != MAX) ? TYPE_ADD : TYPE_PHOTO;
  }

  public static class PhotoViewHolder extends RecyclerView.ViewHolder {
    private ImageView ivPhoto;
    private View vSelected;
    public PhotoViewHolder(View itemView) {
      super(itemView);
      ivPhoto   = (ImageView) itemView.findViewById(me.iwf.photopicker.R.id.iv_photo);
      vSelected = itemView.findViewById(me.iwf.photopicker.R.id.v_selected);
      if (vSelected != null) vSelected.setVisibility(View.GONE);
    }
  }

}
