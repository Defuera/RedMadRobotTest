package ru.justd.redmadrobottest;

import android.annotation.SuppressLint;
import android.content.ClipData.Item;
import android.net.Uri;
import android.widget.ImageView;

class MyItem {
	private ImageView imageView;
	private String imageUrl;
	private boolean isSelected;

	public MyItem(Uri arg0) {
	}

	public ImageView getImageView() {
		return imageView;
	}

	public void setImageView(ImageView imageView) {
		this.imageView = imageView;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
	
}