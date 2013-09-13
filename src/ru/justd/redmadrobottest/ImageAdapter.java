package ru.justd.redmadrobottest;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

@SuppressLint("NewApi")
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private static ImageAdapter imageAdapter;
	private static List<MyItem> myItemsList;
	private static ImageView testIV;

	
	public ImageAdapter(Context c, List<MyItem> myItemsList2, ImageView testIV2){
    	this.mContext = c;
    	this.testIV = testIV2;
    	this.myItemsList = myItemsList2;
	}

    private ImageAdapter() {

    }

    public int getCount() {
        return myItemsList.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        MyItem item = myItemsList.get(position);
        item.setImageView(imageView);
        ImageLoader.loadImageAsync(item.getImageUrl(), imageView, position, testIV);

        return imageView;
    }
    


}