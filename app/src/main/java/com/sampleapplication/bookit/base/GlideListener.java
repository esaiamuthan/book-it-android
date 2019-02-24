package com.sampleapplication.bookit.base;

import android.graphics.drawable.Drawable;


public interface GlideListener {
    void onImageLoaded(Drawable resource);
    void onImageFailed();
}
