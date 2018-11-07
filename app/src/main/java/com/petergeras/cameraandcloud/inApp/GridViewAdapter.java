package com.petergeras.cameraandcloud.inApp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GridViewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<UploadInfoWithKey> infoList;

    private UploadInfo uploadInfo;

    private ImageView imageView;



    public GridViewAdapter(Context context, ArrayList<UploadInfoWithKey> infoList) {
        this.context = context;
        this.infoList = infoList;

    }

    @Override
    public int getCount() {
        return infoList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            // The layout of the image once it is loaded into the GridView
            imageView = new ImageView(this.context);
            imageView.setLayoutParams(new GridView.LayoutParams(350, 400));
            imageView.setPadding(8, 8, 8, 8);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }

        uploadInfo = infoList.get(position).getValue();

        Picasso.with(this.context).load(uploadInfo.getUri()).centerCrop().fit().into(imageView);

        // Once a user clicks on the image in the GridView, the user will be brought to the
        // PhotoFragment layout with the image it was clicked on. Uses method moveToPhotoFragment()
        // from MainActivity.
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity mainActivity = (MainActivity) v.getContext();
                imageView.setImageDrawable(((ImageView) v).getDrawable());

                MainActivity.selectedImageView = imageView.getDrawable();
                MainActivity.selectedUploadInfoWithKey = infoList.get(position);

                mainActivity.moveToPhotoFragment();
            }
        });

        return imageView;
    }
}
