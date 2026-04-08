package com.example.cameragalleryapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import androidx.documentfile.provider.DocumentFile;
import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private final Context appInterface;
    private final ArrayList<DocumentFile> dataSource;

    public ImageAdapter(Context context, ArrayList<DocumentFile> fileList) {
        this.appInterface = context;
        this.dataSource = fileList;
    }

    @Override
    public int getCount() {
        return dataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return dataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AssetHolder assetHolder;

        if (convertView == null) {
            // Updated to reference your specific filename
            convertView = LayoutInflater.from(appInterface).inflate(R.layout.activity_image_adapter, parent, false);

            assetHolder = new AssetHolder();
            assetHolder.imgPreview = convertView.findViewById(R.id.gridImageView);
            convertView.setTag(assetHolder);
        } else {
            assetHolder = (AssetHolder) convertView.getTag();
        }

        DocumentFile currentImg = dataSource.get(position);

        if (currentImg != null) {
            assetHolder.imgPreview.setImageURI(currentImg.getUri());
        }

        return convertView;
    }

    private static class AssetHolder {
        ImageView imgPreview;
    }
}