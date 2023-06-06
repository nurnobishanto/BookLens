package com.softitbd.booklens;

import android.graphics.Bitmap;
import android.net.Uri;

public class ImageModel {
    String title;
    Uri uri;
    Bitmap bitmap;
    Boolean is_bitmap;

    public ImageModel(String title, Uri uri, Bitmap bitmap, Boolean is_bitmap) {
        this.title = title;
        this.uri = uri;
        this.bitmap = bitmap;
        this.is_bitmap = is_bitmap;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Boolean getIs_bitmap() {
        return is_bitmap;
    }

    public void setIs_bitmap(Boolean is_bitmap) {
        this.is_bitmap = is_bitmap;
    }
}
