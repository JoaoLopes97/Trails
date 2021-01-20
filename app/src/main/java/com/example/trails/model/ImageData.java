package com.example.trails.model;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.Serializable;

public class ImageData implements Serializable {
    private Uri uri;
    private Bitmap bitmap;

    public ImageData(Uri uri, Bitmap bitmap) {
        this.uri = uri;
        this.bitmap = bitmap;
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
}
