package com.example.fluffstroller.services;

import android.graphics.Bitmap;

import java.util.function.Consumer;

public interface PhotoService {
    void getPhoto(String pathToPhoto, Consumer<Bitmap> consumer);

    void updatePhoto(String pathToPhoto, Bitmap photo, Consumer<Boolean> updateStatus);
}
